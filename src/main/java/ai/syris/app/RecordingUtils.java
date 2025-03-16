package ai.syris.app;

import javax.sound.sampled.*;
import javax.websocket.CloseReason;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RecordingUtils {

    private static AudioWebSocketClient client = new AudioWebSocketClient();

    /**
     * Starts audio recording.
     */
    public static void startRecording() {
        try {
            client.start();
            System.out.println("Recording started...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Stops the audio recording.
     */
    public static void stopRecording() {
        if (RecordingState.getInstance().getTargetDataLine() != null && client.getOpeenSession()!=null && client.getOpeenSession().isOpen()) {
            client.onClose(null, new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Manual closed."));
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
}
