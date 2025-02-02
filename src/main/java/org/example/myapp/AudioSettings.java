package org.example.myapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class AudioSettings extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Settings & Tools");

        // Create components
        Label settingsLabel = new Label("Settings");
        Label audioLabel = new Label("Audio");
        Label microphoneInputLabel = new Label("Microphone Input Level");
        Label microphoneInUseLabel = new Label("Microphone in use");
        Label microphoneVolumeLabel = new Label("Microphone Volume");
        Label backgroundVoiceLabel = new Label("Background Voice Suppression");
        Label recommendedRangeLabel = new Label("Recommended Range");

        Slider microphoneInputLevelSlider = new Slider(0, 100, 50); // Example values
        microphoneInputLevelSlider.setShowTickLabels(true);
        microphoneInputLevelSlider.setShowTickMarks(true);
        microphoneInputLevelSlider.setMajorTickUnit(20);
        microphoneInputLevelSlider.setMinorTickCount(10);

        ComboBox<String> microphoneInUseComboBox = new ComboBox<>();
        microphoneInUseComboBox.getItems().addAll("Microphone (3-Fifine Microphone)", "Internal Microphone");
        microphoneInUseComboBox.setValue("Microphone (3-Fifine Microphone)"); // Set default selection

        Slider microphoneVolumeSlider = new Slider(0, 100, 50); // Example values
        microphoneVolumeSlider.setShowTickLabels(true);
        microphoneVolumeSlider.setShowTickMarks(true);
        microphoneVolumeSlider.setMajorTickUnit(20);
        microphoneVolumeSlider.setMinorTickCount(10);

        CheckBox backgroundVoiceCheckBox = new CheckBox("Off");

        // Create note label
        Label noteLabel = new Label("Note: Voice suppression level resets to off at 5 AM daily.");

        // Create GridPane layout
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10); // Horizontal gap between columns
        gridPane.setVgap(10); // Vertical gap between rows
        gridPane.setPadding(new Insets(20)); // Padding around the grid

        // Add components to the grid
        gridPane.add(settingsLabel, 0, 0); // Row 0, Column 0
        gridPane.add(audioLabel, 0, 1);

        gridPane.add(microphoneInputLabel, 0, 2);
        gridPane.add(microphoneInputLevelSlider, 1, 2);
        gridPane.add(recommendedRangeLabel, 0, 3);

        gridPane.add(microphoneInUseLabel, 0, 4);
        gridPane.add(microphoneInUseComboBox, 1, 4);

        gridPane.add(microphoneVolumeLabel, 0, 5);
        gridPane.add(microphoneVolumeSlider, 1, 5);

        gridPane.add(backgroundVoiceLabel, 0, 6);
        gridPane.add(backgroundVoiceCheckBox, 1, 6);

        gridPane.add(noteLabel, 0, 7, 2, 1); // Span 2 columns

        // Create and set the scene
        Scene scene = new Scene(gridPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}