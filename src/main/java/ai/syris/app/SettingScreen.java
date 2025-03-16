package ai.syris.app;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingScreen {

    private final ParentDTO parentDTO = new ParentDTO();
    private final Button saveButton = new Button("Save");
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private TargetDataLine currentLine;

    public void showConsole(Stage newStage) {
        ListView<String> settingsList = new ListView<>();
        settingsList.getItems().addAll("General", "Audio", "Keyboard shortcuts", "Speech model");
        settingsList.setPrefWidth(200);

        Label confirmationLabel = new Label("Settings saved!");
        confirmationLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
        confirmationLabel.setVisible(false);

        saveButton.setVisible(false);
        saveButton.setOnAction(e -> saveSettings(saveButton, confirmationLabel));

        // General settings pane
        VBox generalSettings = new VBox(10);
        generalSettings.setPadding(new Insets(10));
        setupGeneralSettings(generalSettings, saveButton);

        // Audio settings pane
        VBox audioSettings = new VBox(10);
        audioSettings.setPadding(new Insets(10));
        setupAudioSettings(audioSettings, saveButton);

        // Keyboard shortcuts pane
        VBox keyboardShortcuts = new VBox(10);
        keyboardShortcuts.setPadding(new Insets(10));
        setupKeyboardShortcuts(keyboardShortcuts, saveButton);

        // Speech model settings pane
        VBox speechModelSettings = new VBox(10);
        speechModelSettings.setPadding(new Insets(10));
        setupSpeechModelSettings(speechModelSettings, saveButton);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(settingsList);
        mainLayout.setCenter(generalSettings);
        mainLayout.setBottom(saveButton);
        mainLayout.setBottom(new VBox(saveButton, confirmationLabel));
        VBox.setMargin(confirmationLabel, new Insets(10)); // Adjust position

        BorderPane.setMargin(saveButton, new Insets(10));

        settingsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "General":
                    mainLayout.setCenter(generalSettings);
                    break;
                case "Audio":
                    mainLayout.setCenter(audioSettings);
                    break;
                case "Keyboard shortcuts":
                    mainLayout.setCenter(keyboardShortcuts);
                    break;
                case "Speech model":
                    mainLayout.setCenter(speechModelSettings);
                    break;
            }
        });

        Scene scene = new Scene(mainLayout, 800, 600);

        newStage.getIcons().add(new Image("logo.png"));
        newStage.setTitle("Syris AI Settings");
        newStage.setScene(scene);

        // Show the new stage
        newStage.show();
    }

    private void setupGeneralSettings(VBox generalSettings, Button saveButton) {
        Label widgetSizeLabel = new Label("Widget size:");
        RadioButton largeSize = new RadioButton("Large");
        RadioButton compactSize = new RadioButton("Compact");
        ToggleGroup widgetSizeGroup = new ToggleGroup();
        largeSize.setToggleGroup(widgetSizeGroup);
        compactSize.setToggleGroup(widgetSizeGroup);
        largeSize.setSelected(true);

        CheckBox rememberPosition = new CheckBox("Remember my widget's last position");
        rememberPosition.setSelected(false);

        generalSettings.getChildren().addAll(
                rememberPosition,
                widgetSizeLabel, largeSize, compactSize
        );

        largeSize.selectedProperty().addListener(createChangeListener(() ->
                parentDTO.getGeneralSettings().setWidgetSize("Large")));
        compactSize.selectedProperty().addListener(createChangeListener(() ->
                parentDTO.getGeneralSettings().setWidgetSize("Compact")));
        rememberPosition.selectedProperty().addListener(createChangeListener(() ->
                parentDTO.getGeneralSettings().setRememberPosition(rememberPosition.isSelected())));

    }

    private void setupAudioSettings(VBox audioSettings, Button saveButton) {
        Label micInputLabel = new Label("Microphone Input Level:");
        ProgressBar micInputProgress = new ProgressBar(0.5);

        Label micInUseLabel = new Label("Microphone in use:");
        ComboBox<String> micComboBox = new ComboBox<>();
        // Get all mixers (audio devices)
        Mixer.Info[] mixers = AudioSystem.getMixerInfo();

        for (Mixer.Info mixerInfo : mixers) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            // Check if the mixer supports a TargetDataLine (microphone input)
            Line.Info[] targetLines = mixer.getTargetLineInfo();
            for (Line.Info lineInfo : targetLines) {
                if (TargetDataLine.class.isAssignableFrom(lineInfo.getLineClass())) {
                    micComboBox.getItems().add(mixerInfo.getName() + " (" + mixerInfo.getDescription() + ")");
                }
            }
        }

        micComboBox.setValue("---SELECT MIC----");
        micComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals("---SELECT MIC---")) {
                parentDTO.getAudioSettings().setSelectedMicrophone(newVal);
                startMicMonitoring(newVal, micInputProgress, mixers);
            }
        });

        audioSettings.getChildren().addAll(
                micInputLabel, micInputProgress,
                micInUseLabel, micComboBox
        );
    }

    private void setupKeyboardShortcuts(VBox keyboardShortcuts, Button saveButton) {
        Label shortcutsHeader = new Label("Configure Keyboard Shortcuts:");
        Label micToggleShortcutLabel = new Label("Mic Toggle Shortcut:");

        TextField micToggleShortcut = new TextField("F4");

        // Add a listener to detect changes and update the DTO
        micToggleShortcut.textProperty().addListener(createChangeListener(() ->
                parentDTO.getKeyboardShortcuts().setMicToggleShortcut(micToggleShortcut.getText())));

        // Add the header, label, and text field to the VBox
        keyboardShortcuts.getChildren().addAll(
                shortcutsHeader,
                micToggleShortcutLabel,
                micToggleShortcut
        );
    }

    private void setupSpeechModelSettings(VBox speechModelSettings, Button saveButton) {
        Label specialtyLabel = new Label("Medical specialty:");
        ComboBox<String> specialtyComboBox = new ComboBox<>();
        specialtyComboBox.getItems().addAll("Radiology");
        specialtyComboBox.setValue("Radiology");

        Label accentLabel = new Label("English accent:");
        ComboBox<String> accentComboBox = new ComboBox<>();
        accentComboBox.getItems().addAll("Indian English");
        accentComboBox.setValue("Indian English");

        Label spellingLabel = new Label("Spelling:");
        ComboBox<String> spellingComboBox = new ComboBox<>();
        spellingComboBox.getItems().addAll("UK sp.");
        spellingComboBox.setValue("UK sp.");

        speechModelSettings.getChildren().addAll(
                specialtyLabel, specialtyComboBox,
                accentLabel, accentComboBox,
                spellingLabel, spellingComboBox
        );

        specialtyComboBox.valueProperty().addListener(createChangeListener(() ->
                parentDTO.getSpeechModelSettings().setSpecialty(specialtyComboBox.getValue())));
    }

    private void saveSettings(Button saveButton, Label confirmationLabel) {
        // Collect form values and call the API
        System.out.println("Settings saved!");

        parentDTO.getGeneralSettings().commit();
        parentDTO.getAudioSettings().commit();
        parentDTO.getKeyboardShortcuts().commit();
        parentDTO.getSpeechModelSettings().commit();

        toggleSaveButton();

        // Show the confirmation message
        confirmationLabel.setVisible(true);

        // Automatically hide the confirmation message after 2 seconds
        Timeline hideConfirmation = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> confirmationLabel.setVisible(false))
        );
        hideConfirmation.play();
    }

    private void toggleSaveButton() {
        saveButton.setVisible(parentDTO.hasChanges());
    }

    private ChangeListener<Object> createChangeListener(Runnable onChange) {
        return (observable, oldValue, newValue) -> {
            onChange.run();
            toggleSaveButton();
        };
    }

    public static class Shortcut {
        private final SimpleStringProperty action;
        private final SimpleStringProperty keys;

        public Shortcut(String action, String keys) {
            this.action = new SimpleStringProperty(action);
            this.keys = new SimpleStringProperty(keys);
        }

        public String getAction() {
            return action.get();
        }

        public SimpleStringProperty actionProperty() {
            return action;
        }

        public String getKeys() {
            return keys.get();
        }

        public SimpleStringProperty keysProperty() {
            return keys;
        }

        public static javafx.collections.ObservableList<Shortcut> getDefaultShortcuts() {
            return FXCollections.observableArrayList(
                    new Shortcut("Mic on/off", "F4")
            );
        }
    }

    private void startMicMonitoring(String selectedMic, ProgressBar micInputProgress, Mixer.Info[] mixers) {
        // Stop any currently running microphone
        if (currentLine != null && currentLine.isOpen()) {
            currentLine.stop();
            currentLine.close();
        }

        try {
            // Find the selected microphone
            Mixer selectedMixer = null;
            for (Mixer.Info mixerInfo : mixers) {
                if ((mixerInfo.getName() + " (" + mixerInfo.getDescription() + ")").equals(selectedMic)) {
                    selectedMixer = AudioSystem.getMixer(mixerInfo);
                    break;
                }
            }

            if (selectedMixer != null) {
                // Configure and open the TargetDataLine
                AudioFormat format = new AudioFormat(44100.0f, 16, 1, true, true);
                DataLine.Info lineInfo = new DataLine.Info(TargetDataLine.class, format);
                currentLine = (TargetDataLine) selectedMixer.getLine(lineInfo);

                currentLine.open(format);
                currentLine.start();
                System.out.println(currentLine.getLineInfo().toString());

                RecordingState.getInstance().changeMicrophone(currentLine);
                executor.submit(() -> {
                    byte[] buffer = new byte[1024];
                    while (RecordingState.getInstance().isRecording() && currentLine.isOpen()) {
                        int bytesRead = currentLine.read(buffer, 0, buffer.length);

                        if (bytesRead > 0) {

                            // Calculate the RMS (Root Mean Square) of the audio data
                            double rms = calculateRMS(buffer, bytesRead);

                            // Update the progress bar on the JavaFX Application Thread
                            Platform.runLater(() -> micInputProgress.setProgress(rms));
                        }
                    }
                });
            }
        } catch (LineUnavailableException e) {
            e.printStackTrace();
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
