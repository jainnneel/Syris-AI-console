//package org.example.myapp;
//
//
//import javax.sound.sampled.*;
//import javax.websocket.*;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.net.URI;
//import java.nio.ByteBuffer;
//
//@ClientEndpoint
//public class SpeechToText {
//
//    private static final String SERVER_URI = "wss://livekit.syris.in?access_token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ2aWRlbyI6eyJyb29tSm9pbiI6dHJ1ZSwicm9vbSI6Ijg1M2IwNTEwLTllYjItNGU3Zi04NWM2LTU0M2U3N2VmMjJmYiIsImNhblB1Ymxpc2giOnRydWUsImNhblN1YnNjcmliZSI6dHJ1ZSwiY2FuUHVibGlzaERhdGEiOnRydWV9LCJzdWIiOiJ1c2VyLTEwOWFiOTk4LTA3NzItNGFmYy1hNTRhLTQ0NzNmMzk1OTMyMyIsImlzcyI6IkFQSUpMQjJOSmlpSEJRaSIsIm5iZiI6MTczODQxMDk0OCwiZXhwIjoxNzM4NDMyNTQ4fQ.neo75-sgYI2Eo8VW76BDAwWuqoVizpaZK5ktk-VTgLA"; // Replace with actual WebSocket URL
//    private static final int SAMPLE_RATE = 16000;
//    private static final int SAMPLE_SIZE = 16;
//    private static final int CHANNELS = 1;
//    private static final int BUFFER_SIZE = 4096; // Adjust buffer size if needed
//
//    private Session session;
//
//    public static void main(String[] args) throws LineUnavailableException {
//        SpeechToText client = new SpeechToText();
//        client.connect();
////        client.startRecording();
//    }
//
//    public void connect() {
//        try {
//            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//            container.connectToServer(this, new URI(SERVER_URI+"/853b0510-9eb2-4e7f-85c6-543e77ef22fb"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @OnOpen
//    public void onOpen(Session session) {
//        System.out.println("Connected to WebSocket Server");
//        this.session = session;
//    }
//
//    @OnClose
//    public void onClose(Session session, CloseReason reason) {
//        System.out.println("WebSocket closed: " + reason);
//    }
//
//    public void startRecording() throws LineUnavailableException {
//        AudioFormat format = new AudioFormat(SAMPLE_RATE, SAMPLE_SIZE, CHANNELS, true, false);
//        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
//
//        try (TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info)) {
//            line.open(format);
//            line.start();
//
//            ByteArrayOutputStream audioStream = new ByteArrayOutputStream();
//            byte[] buffer = new byte[BUFFER_SIZE];
//
//            System.out.println("Recording and streaming... Press ENTER to stop.");
//
//            // Send WAV header first
//            byte[] wavHeader = createWavHeader(0);  // Temporary header
//            if (session != null && session.isOpen()) {
//                session.getBasicRemote().sendBinary(ByteBuffer.wrap(wavHeader));
//            }
//
//            // Start a thread to stop on ENTER key
//            Thread stopper = new Thread(() -> {
//                try {
//                    System.in.read();
//                    line.stop();
//                    line.close();
//
//                    // Update the WAV header with the actual data length
//                    byte[] finalWav = finalizeWav(audioStream.toByteArray());
//                    session.getBasicRemote().sendBinary(ByteBuffer.wrap(finalWav));
//
//                    System.out.println("Stopped recording.");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            });
//
//            stopper.start();
//
//            while (line.isOpen()) {
//                int bytesRead = line.read(buffer, 0, buffer.length);
//                audioStream.write(buffer, 0, bytesRead);
//
//                // Stream the WAV data in real time
//                if (session != null && session.isOpen()) {
//                    session.getBasicRemote().sendBinary(ByteBuffer.wrap(buffer));
//                }
//            }
//
//        } catch (LineUnavailableException | IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private byte[] createWavHeader(int audioDataLength) {
//        int totalLength = audioDataLength + 36;
//        byte[] header = new byte[44];
//
//        // "RIFF" chunk
//        header[0] = 'R'; header[1] = 'I'; header[2] = 'F'; header[3] = 'F';
//        header[4] = (byte) (totalLength & 0xff);
//        header[5] = (byte) ((totalLength >> 8) & 0xff);
//        header[6] = (byte) ((totalLength >> 16) & 0xff);
//        header[7] = (byte) ((totalLength >> 24) & 0xff);
//
//        // "WAVE" format
//        header[8] = 'W'; header[9] = 'A'; header[10] = 'V'; header[11] = 'E';
//
//        // "fmt " subchunk
//        header[12] = 'f'; header[13] = 'm'; header[14] = 't'; header[15] = ' ';
//        header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0;  // Subchunk1Size (16 for PCM)
//        header[20] = 1; header[21] = 0;  // AudioFormat (PCM = 1)
//        header[22] = (byte) CHANNELS;
//        header[23] = 0;
//        header[24] = (byte) (SAMPLE_RATE & 0xff);
//        header[25] = (byte) ((SAMPLE_RATE >> 8) & 0xff);
//        header[26] = (byte) ((SAMPLE_RATE >> 16) & 0xff);
//        header[27] = (byte) ((SAMPLE_RATE >> 24) & 0xff);
//        int byteRate = SAMPLE_RATE * CHANNELS * SAMPLE_SIZE / 8;
//        header[28] = (byte) (byteRate & 0xff);
//        header[29] = (byte) ((byteRate >> 8) & 0xff);
//        header[30] = (byte) ((byteRate >> 16) & 0xff);
//        header[31] = (byte) ((byteRate >> 24) & 0xff);
//        header[32] = (byte) (CHANNELS * SAMPLE_SIZE / 8);
//        header[33] = 0;
//        header[34] = (byte) SAMPLE_SIZE;
//        header[35] = 0;
//
//        // "data" subchunk
//        header[36] = 'd'; header[37] = 'a'; header[38] = 't'; header[39] = 'a';
//        header[40] = (byte) (audioDataLength & 0xff);
//        header[41] = (byte) ((audioDataLength >> 8) & 0xff);
//        header[42] = (byte) ((audioDataLength >> 16) & 0xff);
//        header[43] = (byte) ((audioDataLength >> 24) & 0xff);
//
//        return header;
//    }
//
//    private byte[] finalizeWav(byte[] audioData) {
//        byte[] finalWav = new byte[44 + audioData.length];
//        System.arraycopy(createWavHeader(audioData.length), 0, finalWav, 0, 44);
//        System.arraycopy(audioData, 0, finalWav, 44, audioData.length);
//        return finalWav;
//    }
//
//}
