package org.example.myapp;

import javafx.scene.control.Button;
import lombok.Data;
import lombok.Getter;

import javax.sound.sampled.TargetDataLine;

@Getter
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
    }

    public void toggleRecording() {
        if (isRecording) {
            // Add logic for stopping the recording

        } else {
            // Add logic for starting the recording

        }
        isRecording = !isRecording;
    }


}
