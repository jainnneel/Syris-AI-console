package org.example.myapp;

import javafx.application.Platform;
import javafx.scene.control.ProgressBar;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordingUtils {

    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * Starts audio recording.
     */
    public void startRecording() {
        try {
            TargetDataLine targetDataLine = getTargetedDataLine();
            targetDataLine.open(targetDataLine.getFormat());
            targetDataLine.start();

            // Start recording in a new thread
            executor.submit(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    while (RecordingState.getInstance().isRecording()) {
                        int bytesRead = targetDataLine.read(buffer, 0, buffer.length);
                        System.out.println(bytesRead);

                        double rms = calculateRMS(buffer, bytesRead);

                        // Update the progress bar on the JavaFX Application Thread
//                        Platform.runLater(() -> micInputProgress.setProgress(rms));

                        // Here, you would normally process or save the recorded data
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            System.out.println("Recording started...");
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    private static TargetDataLine getTargetedDataLine() throws LineUnavailableException {
        TargetDataLine targetDataLine = RecordingState.getInstance().getTargetDataLine();

        if(!targetDataLine.isActive()) {
            List<Mixer> aLlAvailableMics = RecordingUtils.getALlAvailableMics();
            if(!aLlAvailableMics.isEmpty()) {
                targetDataLine = (TargetDataLine) aLlAvailableMics.get(0).getLine(new DataLine.Info(TargetDataLine.class, new AudioFormat(44100.0f, 16, 1, true, true)));
            } else {
                // No mics available
            }
        }

        return targetDataLine;
    }

    /**
     * Stops the audio recording.
     */
    public static void stopRecording() {
        if (RecordingState.getInstance().getTargetDataLine() != null) {
            RecordingState.getInstance().getTargetDataLine().stop();
            RecordingState.getInstance().getTargetDataLine().close();
            executor.shutdownNow();
            System.out.println("Recording stopped.");
        }
    }

    public static List<Mixer> getALlAvailableMics() {
        List<Mixer> mics = new ArrayList<>();

        // Get all mixers (audio devices)
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();

        for (Mixer.Info mixerInfo : mixers) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            // Check if the mixer supports a TargetDataLine (microphone input)
            Line.Info[] targetLines = mixer.getTargetLineInfo();
            for (Line.Info lineInfo : targetLines) {
                if (TargetDataLine.class.isAssignableFrom(lineInfo.getLineClass())) {
                    mics.add(AudioSystem.getMixer(mixerInfo));
                }
            }
        }

        return mics;
    }

    public static double calculateRMS(byte[] buffer, int bytesRead) {
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
