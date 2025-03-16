package ai.syris.app;


import javax.sound.sampled.TargetDataLine;

public class RecordingState {

    private static RecordingState instance; // Singleton instance

    private TargetDataLine targetDataLine; // Instance variable
    private volatile boolean isRecording = false; // Instance variable

    // Private constructor to prevent instantiation
    private RecordingState() {
    }

    // Public method to get the singleton instance
    public static synchronized RecordingState getInstance() {
        if (instance == null) {
            instance = new RecordingState();
        }
        return instance;
    }

    public void changeMicrophone(TargetDataLine targetDataLine) {
        this.targetDataLine = targetDataLine;
        RecordingUtils.stopRecording();
        RecordingUtils.startRecording();
    }

    public void toggleRecording() {
        if (isRecording) {
            // Add logic for stopping the recording
            RecordingUtils.stopRecording();

        } else {
            // Add logic for starting the recording
            RecordingUtils.startRecording();
        }
        isRecording = !isRecording;
    }

    public TargetDataLine getTargetDataLine() {
        return targetDataLine;
    }

    public boolean isRecording() {
        return isRecording;
    }
}
