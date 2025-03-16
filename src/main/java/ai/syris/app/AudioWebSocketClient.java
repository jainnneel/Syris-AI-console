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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static javax.websocket.ContainerProvider.getWebSocketContainer;
import static ai.syris.app.BackgroundIndicatorWidget.stopRecoding;

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

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(transcript);
        clipboard.setContents(selection, null);

        // Simulate Ctrl + V to paste the copied text
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        robot.delay(500);

        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("Connection closed: " + reason);
        RecordingState.getInstance().getTargetDataLine().stop();
        RecordingState.getInstance().getTargetDataLine().close();
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
//            AudioFormat format = new AudioFormat(DEVICE_RATE, 16, 1, true, false);
            microphone = RecordingState.getInstance().getTargetDataLine();
//            microphone.open(format);
//            microphone.start();
            byte[] buffer = new byte[CHUNK_SIZE * 2];
            System.out.println(microphone.isActive());
            System.out.println("Sending audio...");
            while (opeenSession.isOpen()) {
                int bytesRead = microphone.read(buffer, 0, buffer.length);
                if (!isSilent(buffer, 0.01)) {
                    if (bytesRead > 0) {
                        byte[] resampled = resample(buffer, DEVICE_RATE, TARGET_RATE);
                        opeenSession.getBasicRemote().sendBinary(ByteBuffer.wrap(resampled));
                    }
                }
                Thread.sleep(100);
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

    public static boolean isSilent(byte[] audioData, double threshold) {
        double sum = 0;
        for (byte b : audioData) {
            sum += b * b;
        }
        double rms = Math.sqrt(sum / audioData.length);
        return false;
//        return rms < threshold; // If RMS is below the threshold, consider it silent
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
}
