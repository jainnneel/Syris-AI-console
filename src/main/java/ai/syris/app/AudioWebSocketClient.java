package ai.syris.app;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.sound.sampled.*;
import javax.websocket.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ai.syris.app.BackgroundIndicatorWidget.stopRecoding;
import static ai.syris.app.BackgroundIndicatorWidget.updateProgressBarWidget;
import static ai.syris.app.SettingScreen.updateProgressBar;
import static javax.websocket.ContainerProvider.getWebSocketContainer;

@ClientEndpoint
public class AudioWebSocketClient {
    private static final String SERVER_URI = "ws://room.syris.in";
    private static final int DEVICE_RATE = 44100;
    private static final int TARGET_RATE = 16000;
    private static final int CHUNK_SIZE = 1024;
    private ExecutorService executor;

    private Session opeenSession;
    private TargetDataLine microphone;

    public Session getOpeenSession() {
        return opeenSession;
    }

    public void start() {
        WebSocketContainer container = getWebSocketContainer();
        executor = Executors.newFixedThreadPool(2);
        try {
            container.connectToServer(this, new URI(SERVER_URI));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.opeenSession = session;
        System.out.println("Connected to server.");
        executor.submit(this::captureAndSendAudio);
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received: " + message);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String transcript = jsonNode.get("transcript").asText();

        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        robot.delay(500);

        if (transcript.startsWith("DL-"))
        {
            int cnt = Integer.parseInt(transcript.split("-")[1]);
            for (int i = 0; i < cnt; i++)
            {
                robot.keyPress(KeyEvent.VK_CONTROL);
                robot.keyPress(KeyEvent.VK_BACK_SPACE);
                robot.keyRelease(KeyEvent.VK_V);
                robot.keyRelease(KeyEvent.VK_BACK_SPACE);
            }
            return;
        }

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(" " + transcript);
        clipboard.setContents(selection, null);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Connection closed: " + reason);
        microphone.stop();
        microphone.close();
        try {
            opeenSession.close();
            microphone.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stopRecoding();
        executor.shutdown();
    }

    private void captureAndSendAudio() {
        try {
            Mixer.Info[] mixers = AudioSystem.getMixerInfo();

            // Find the selected microphone
            Mixer selectedMixer = null;

            for (Mixer.Info mixerInfo : mixers) {
                if ((mixerInfo.getName() + " (" + mixerInfo.getDescription() + ")").equals(RecordingState.getInstance().getTargetDataLine())) {
                    selectedMixer = AudioSystem.getMixer(mixerInfo);
                    break;
                }
            }

            AudioFormat format = new AudioFormat(DEVICE_RATE, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                System.err.println("Line not supported");
                return;
            }

            microphone = (TargetDataLine) selectedMixer.getLine(info);
            microphone.open(format);
            microphone.start();

            byte[] buffer = new byte[CHUNK_SIZE * 2];

            System.out.println("Sending audio...");
            while (opeenSession.isOpen()) {
                int bytesRead = microphone.read(buffer, 0, buffer.length);
                if (bytesRead > 0) {
                    byte[] resampled = resample(buffer, DEVICE_RATE, TARGET_RATE);
                    double rms = calculateRMS(buffer, bytesRead);
                    updateProgressBar(rms);
                    updateProgressBarWidget(rms);
                    opeenSession.getAsyncRemote().sendBinary(ByteBuffer.wrap(resampled));
                }
                Thread.sleep(10);
            }
        } catch (Exception e) {
            System.out.println("Connection has been closed.");
            e.printStackTrace();
        } finally {
            if (microphone != null) {
                microphone.close();
            }
        }
    }

    private byte[] resample(byte[] audioData, int srcRate, int targetRate) {
        try {
            // Define source format (original)
            AudioFormat srcFormat = new AudioFormat(srcRate, 16, 1, true, false);
            // Define target format (converted)
            AudioFormat targetFormat = new AudioFormat(targetRate, 16, 1, true, false);

            // Convert byte array to audio input stream
            ByteArrayInputStream bais = new ByteArrayInputStream(audioData);
            AudioInputStream sourceStream = new AudioInputStream(bais, srcFormat, audioData.length / srcFormat.getFrameSize());

            // Resample using AudioSystem
            AudioInputStream resampledStream = AudioSystem.getAudioInputStream(targetFormat, sourceStream);

            // Convert back to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = resampledStream.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return audioData; // Return original if resampling fails
        }
    }

    private double calculateRMS(byte[] buffer, int bytesRead) {
        long sum = 0;
        for (int i = 0; i < bytesRead - 1; i += 2) {
            // Convert two bytes to a single audio sample (16-bit PCM)
            int sample = (buffer[i + 1] << 8) | (buffer[i] & 0xFF);
            sum += sample * sample;
        }

        double rms = Math.sqrt(sum / (bytesRead / 2.0));
        return Math.min(rms / 32768.0, 1.0); // Normalize to 0.0 - 1.0
    }
}
