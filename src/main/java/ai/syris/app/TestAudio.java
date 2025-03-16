package ai.syris.app;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;

public class TestAudio {
    public static void main(String[] args) {
        // Get all mixers (audio devices)
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();

        System.out.println("Available Microphones:");
        for (Mixer.Info mixerInfo : mixers) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            // Check if the mixer supports a TargetDataLine (microphone input)
            Line.Info[] targetLines = mixer.getTargetLineInfo();
            for (Line.Info lineInfo : targetLines) {
                if (TargetDataLine.class.isAssignableFrom(lineInfo.getLineClass())) {
                    System.out.println("- " + mixerInfo.getName() + " (" + mixerInfo.getDescription() + ")");
                }
            }
        }
    }
}
