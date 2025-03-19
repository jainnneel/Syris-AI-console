package ai.syris.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import okhttp3.*;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingScreen {

    private final ConfigDTO configDTO = new ConfigDTO();
    private final Button saveButton = new Button("Save");
    private static ProgressBar micInputProgress;
    private final String ACCENT_COLOR = "#3498db";
    private final String HOVER_COLOR = "#2980b9";
    private final String SIDEBAR_BG_COLOR = "#2c3e50";
    private final String SIDEBAR_SELECTED_COLOR = "#1abc9c";
    private final String SIDEBAR_HOVER_COLOR = "#34495e";

    public void showConsole(Stage newStage) {
        micInputProgress = new ProgressBar(0.1);

        setCurrentUserConfig();

        // Create a custom styled navigation panel
        VBox navigationPanel = createEnhancedNavigationPanel();

        // Create confirmation label with animation
        Label confirmationLabel = new Label("Settings saved successfully!");
        confirmationLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 14px; -fx-font-weight: bold;");
        confirmationLabel.setVisible(false);

        // Style save button
        styleButton(saveButton, ACCENT_COLOR, HOVER_COLOR);
        saveButton.setPrefWidth(120);
        saveButton.setVisible(false);
        saveButton.setOnAction(e -> {
            try {
                saveSettings(saveButton, confirmationLabel);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        // General settings pane with enhanced styling
        VBox generalSettings = new VBox(20);
        generalSettings.setPadding(new Insets(25));
        generalSettings.setStyle("-fx-background-color: white;");
        setupEnhancedGeneralSettings(generalSettings, saveButton);

        // Audio settings pane
        VBox audioSettings = new VBox(20);
        audioSettings.setPadding(new Insets(25));
        audioSettings.setStyle("-fx-background-color: white;");
        setupAudioSettings(audioSettings, saveButton);

        // Keyboard shortcuts pane
        VBox keyboardShortcuts = new VBox(20);
        keyboardShortcuts.setPadding(new Insets(25));
        keyboardShortcuts.setStyle("-fx-background-color: white;");
        setupKeyboardShortcuts(keyboardShortcuts, saveButton);

        // Speech model settings pane
        VBox speechModelSettings = new VBox(20);
        speechModelSettings.setPadding(new Insets(25));
        speechModelSettings.setStyle("-fx-background-color: white;");
        setupSpeechModelSettings(speechModelSettings, saveButton);

        // Create bottom bar for save button
        HBox bottomBar = new HBox();
        bottomBar.setPadding(new Insets(15));
        bottomBar.setAlignment(Pos.CENTER_RIGHT);
        bottomBar.getChildren().addAll(confirmationLabel, saveButton);
        bottomBar.setSpacing(20);
        bottomBar.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; -fx-border-width: 1 0 0 0;");
        HBox.setHgrow(confirmationLabel, Priority.ALWAYS);

        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(navigationPanel);
        mainLayout.setCenter(generalSettings);
        mainLayout.setBottom(bottomBar);
        mainLayout.setStyle("-fx-background-color: white;");

        // Create a Scene and set it to the Stage
        Scene scene = new Scene(mainLayout, 900, 650);

        newStage.getIcons().add(new Image("logo.png"));
        newStage.setTitle("Syris AI Settings");
        newStage.setScene(scene);

        // Show the new stage
        newStage.show();
    }

    private void setCurrentUserConfig() {
        String url = "http://localhost:8080/api/user-settings/" + UserPersistence.getCurrentLoginUser().getId();
        OkHttpClient client = new OkHttpClient();
        ObjectMapper objectMapper = new ObjectMapper(); // Jackson for JSON conversion

        // Build request
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + UserPersistence.loadUser())
                .addHeader("Content-Type", "application/json")
                .build();

        // Execute request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }

            // Parse and return the updated Config object
            Config config = objectMapper.readValue(response.body().string(), Config.class);
            configDTO.getGeneralSettings().setRememberPosition(config.isWidgetPosition());
            configDTO.getGeneralSettings().setWidgetSize(config.getWidgetSize() == null ? configDTO.getGeneralSettings().getDefaultWidgetSize() : config.getWidgetSize());
            configDTO.getKeyboardShortcuts().setMicToggleShortcut(config.getMicToggleShortcut() == null ? configDTO.getKeyboardShortcuts().getDefaultMicToggleShortcut() : config.getMicToggleShortcut());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private VBox createEnhancedNavigationPanel() {
        // Main container for sidebar
        VBox navigationPanel = new VBox();
        navigationPanel.setPrefWidth(240);
        navigationPanel.setStyle(
                "-fx-background-color: " + SIDEBAR_BG_COLOR + ";" +
                        "-fx-padding: 0;"
        );

        // App title/logo section
        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        titleBox.setPadding(new Insets(20, 15, 20, 15));
        titleBox.setStyle("-fx-background-color: " + SIDEBAR_BG_COLOR + ";");

        // Create a placeholder for logo image
        Rectangle logoPlaceholder = new Rectangle(32, 32);
        logoPlaceholder.setFill(Color.web("#1abc9c"));
        logoPlaceholder.setArcWidth(8);
        logoPlaceholder.setArcHeight(8);

        // App title
        Label titleLabel = new Label("Syris AI Settings");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        titleBox.getChildren().addAll(logoPlaceholder, titleLabel);

        // Create navigation items
        VBox menuItems = new VBox();
        menuItems.setPadding(new Insets(10, 0, 0, 0));

        // Create the content panels for each section
        VBox generalSettings = new VBox(20);
        generalSettings.setPadding(new Insets(25));
        generalSettings.setStyle("-fx-background-color: white;");
        setupEnhancedGeneralSettings(generalSettings, saveButton);

        VBox audioSettings = new VBox(20);
        audioSettings.setPadding(new Insets(25));
        audioSettings.setStyle("-fx-background-color: white;");
        setupAudioSettings(audioSettings, saveButton);

        VBox keyboardShortcuts = new VBox(20);
        keyboardShortcuts.setPadding(new Insets(25));
        keyboardShortcuts.setStyle("-fx-background-color: white;");
        setupKeyboardShortcuts(keyboardShortcuts, saveButton);

        VBox speechModelSettings = new VBox(20);
        speechModelSettings.setPadding(new Insets(25));
        speechModelSettings.setStyle("-fx-background-color: white;");
        setupSpeechModelSettings(speechModelSettings, saveButton);

        // Define menu items with icons and content panes
        List<NavigationItem> items = Arrays.asList(
                new NavigationItem("General", "⚙️", generalSettings),
                new NavigationItem("Audio", "🎤", audioSettings),
                new NavigationItem("Keyboard shortcuts", "⌨️", keyboardShortcuts),
                new NavigationItem("Speech model", "🔊", speechModelSettings)
        );

        // Reference to the currently selected menu item
        final ToggleGroup toggleGroup = new ToggleGroup();

        // Create and add menu items to the panel
        for (NavigationItem item : items) {
            ToggleButton menuItem = createMenuItem(item.icon, item.name);
            menuItem.setToggleGroup(toggleGroup);
            menuItem.setUserData(item.contentPane);

            menuItems.getChildren().add(menuItem);

            // Set action when menu item is selected
            menuItem.setOnAction(e -> {
                // Update content area with selected panel
                BorderPane mainLayout = (BorderPane) menuItem.getScene().getRoot();
                mainLayout.setCenter(item.contentPane);
            });
        }

        // Select first item by default
        if (!items.isEmpty() && !menuItems.getChildren().isEmpty()) {
            ToggleButton firstItem = (ToggleButton) menuItems.getChildren().get(0);
            firstItem.setSelected(true);
        }

        // Add title and menu items to the navigation panel
        navigationPanel.getChildren().addAll(titleBox, menuItems);

        // Add spacer at the bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        navigationPanel.getChildren().add(spacer);

        // Add version info at bottom
        Label versionLabel = new Label("Syris AI v1.2.4");
        versionLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 12px; -fx-padding: 15;");
        navigationPanel.getChildren().add(versionLabel);

        return navigationPanel;
    }

    private ToggleButton createMenuItem(String icon, String text) {
        ToggleButton menuItem = new ToggleButton(icon + " " + text);

        menuItem.setPrefWidth(240);
        menuItem.setPrefHeight(50);
        menuItem.setAlignment(Pos.CENTER_LEFT);
        menuItem.setPadding(new Insets(0, 0, 0, 15));

        // Default style
        menuItem.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: white;" +
                        "-fx-font-size: 14px;" +
                        "-fx-cursor: hand;" +
                        "-fx-border-width: 0 0 0 0;" +
                        "-fx-background-radius: 0;"
        );

        // Selected style
        menuItem.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                menuItem.setStyle(
                        "-fx-background-color: " + SIDEBAR_SELECTED_COLOR + ";" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 14px;" +
                                "-fx-font-weight: bold;" +
                                "-fx-border-width: 0 0 0 5;" +
                                "-fx-border-color: white;" +
                                "-fx-background-radius: 0;"
                );
            } else {
                menuItem.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 14px;" +
                                "-fx-border-width: 0;" +
                                "-fx-background-radius: 0;"
                );
            }
        });

        // Hover style
        menuItem.setOnMouseEntered(e -> {
            if (!menuItem.isSelected()) {
                menuItem.setStyle(
                        "-fx-background-color: " + SIDEBAR_HOVER_COLOR + ";" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 14px;" +
                                "-fx-border-width: 0;" +
                                "-fx-background-radius: 0;"
                );
            }
        });

        menuItem.setOnMouseExited(e -> {
            if (!menuItem.isSelected()) {
                menuItem.setStyle(
                        "-fx-background-color: transparent;" +
                                "-fx-text-fill: white;" +
                                "-fx-font-size: 14px;" +
                                "-fx-border-width: 0;" +
                                "-fx-background-radius: 0;"
                );
            }
        });

        return menuItem;
    }

    private static class NavigationItem {
        String name;
        String icon;
        Pane contentPane;

        public NavigationItem(String name, String icon, Pane contentPane) {
            this.name = name;
            this.icon = icon;
            this.contentPane = contentPane;
        }
    }

    private ListView<String> createStyledSettingsList() {
        ListView<String> settingsList = new ListView<>();
        settingsList.getItems().addAll("General", "Audio", "Keyboard shortcuts", "Speech model");
        settingsList.setPrefWidth(220);
        settingsList.setStyle("-fx-background-color: #f8f9fa; -fx-font-size: 14px;");

        // Select first item by default
        settingsList.getSelectionModel().select(0);

        return settingsList;
    }

    private void setupEnhancedGeneralSettings(VBox generalSettings, Button saveButton) {
        // Section title
        Label sectionTitle = new Label("General Settings");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        sectionTitle.setStyle("-fx-text-fill: #2c3e50;");

        // Widget position section
        Label positionLabel = new Label("Widget Position");
        positionLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        positionLabel.setStyle("-fx-text-fill: #2c3e50; -fx-padding: 0 0 5 0;");

        CheckBox rememberPosition = new CheckBox("Remember my widget's last position");
        rememberPosition.setStyle("-fx-font-size: 14px;");
        rememberPosition.setSelected(configDTO.getGeneralSettings().isRememberPosition());

        // Add description for remember position
        Label positionDescription = new Label("When enabled, Syris will remember where you placed the widget and restore it next time you open the app.");
        positionDescription.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px; -fx-wrap-text: true;");
        positionDescription.setMaxWidth(550);

        VBox positionSection = new VBox(8);
        positionSection.getChildren().addAll(positionLabel, rememberPosition, positionDescription);
        positionSection.setStyle("-fx-padding: 15 0 15 0;");

        // Widget size section
        Label widgetSizeLabel = new Label("Widget Size");
        widgetSizeLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        widgetSizeLabel.setStyle("-fx-text-fill: #2c3e50; -fx-padding: 10 0 5 0;");

        ToggleGroup widgetSizeGroup = new ToggleGroup();

        // Create size preview panes
        HBox sizeOptions = new HBox(20);
        sizeOptions.setAlignment(Pos.CENTER_LEFT);

        // Large size option
        VBox largeOption = createSizeOptionBox("Large", 120, 80, widgetSizeGroup);

        // Compact size option
        VBox compactOption = createSizeOptionBox("Compact", 100, 60, widgetSizeGroup);

        sizeOptions.getChildren().addAll(largeOption, compactOption);

        // Add description for widget size
        Label sizeDescription = new Label("Choose how the Syris widget appears on your screen. Large is more visible, while compact takes up less space.");
        sizeDescription.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px; -fx-wrap-text: true;");
        sizeDescription.setMaxWidth(550);

        VBox sizeSection = new VBox(10);
        sizeSection.getChildren().addAll(widgetSizeLabel, sizeOptions, sizeDescription);

        // Set the selected option based on config
        if (configDTO.getGeneralSettings().getWidgetSize().equals("Large")) {
            ((RadioButton)largeOption.getChildren().get(1)).setSelected(true);
        } else {
            ((RadioButton)compactOption.getChildren().get(1)).setSelected(true);
        }

        // Add separator
        Separator separator = new Separator();
        separator.setStyle("-fx-padding: 10 0 5 0;");

        // Add all sections to general settings
        generalSettings.getChildren().addAll(
                sectionTitle,
                new Separator(),
                positionSection,
                sizeSection
        );

        // Add listeners for changes
        ((RadioButton)largeOption.getChildren().get(1)).selectedProperty().addListener(createChangeListener(() ->
                configDTO.getGeneralSettings().setWidgetSize("Large")));

        ((RadioButton)compactOption.getChildren().get(1)).selectedProperty().addListener(createChangeListener(() ->
                configDTO.getGeneralSettings().setWidgetSize("Compact")));

        rememberPosition.selectedProperty().addListener(createChangeListener(() ->
                configDTO.getGeneralSettings().setRememberPosition(rememberPosition.isSelected())));
    }

    private VBox createSizeOptionBox(String size, int width, int height, ToggleGroup group) {
        // Create preview rectangle
        Rectangle preview = new Rectangle(width, height);
        preview.setFill(Color.web("#ecf0f1"));
        preview.setStroke(Color.web("#bdc3c7"));
        preview.setArcWidth(10);
        preview.setArcHeight(10);

        // Create radio button
        RadioButton option = new RadioButton(size);
        option.setToggleGroup(group);
        option.setStyle("-fx-font-size: 14px;");

        // Container for preview and label
        VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);
        container.getChildren().addAll(preview, option);

        return container;
    }

    private void setupAudioSettings(VBox audioSettings, Button saveButton) {
        // Section title
        Label sectionTitle = new Label("Audio Settings");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        sectionTitle.setStyle("-fx-text-fill: #2c3e50;");

        // Microphone section
        Label micSectionLabel = new Label("Microphone Settings");
        micSectionLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        micSectionLabel.setStyle("-fx-text-fill: #2c3e50; -fx-padding: 10 0 5 0;");

        Label micInUseLabel = new Label("Select Microphone:");
        micInUseLabel.setStyle("-fx-font-size: 14px;");

        ComboBox<String> micComboBox = new ComboBox<>();
        micComboBox.setPrefWidth(400);
        micComboBox.setStyle("-fx-font-size: 14px;");

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

        micComboBox.setValue("---SELECT MIC---");

        // Input level section
        Label micInputLabel = new Label("Microphone Input Level:");
        micInputLabel.setStyle("-fx-font-size: 14px; -fx-padding: 15 0 5 0;");

        // Style the mic progress bar
//        micInputProgress.setPrefWidth(400);
//        micInputProgress.setStyle("-fx-accent: #2ecc71;");
        micInputProgress.setVisible(true);

        // Add a help text
        Label micLevelHelp = new Label("Speak into your microphone to test the input level");
        micLevelHelp.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        micComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals("---SELECT MIC---")) {
                configDTO.getAudioSettings().setSelectedMicrophone(newVal);
                startMicMonitoring(newVal, micInputProgress, mixers);
            }
        });

        audioSettings.getChildren().addAll(
                sectionTitle,
                new Separator(),
                micSectionLabel,
                micInUseLabel,
                micComboBox,
                micInputLabel,
                micInputProgress,
                micLevelHelp
        );
    }

    private void setupKeyboardShortcuts(VBox keyboardShortcuts, Button saveButton) {
        // Section title
        Label sectionTitle = new Label("Keyboard Shortcuts");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        sectionTitle.setStyle("-fx-text-fill: #2c3e50;");

        // Shortcuts header
        Label shortcutsHeader = new Label("Configure your shortcuts");
        shortcutsHeader.setFont(Font.font("System", FontWeight.BOLD, 16));
        shortcutsHeader.setStyle("-fx-text-fill: #2c3e50; -fx-padding: 10 0 15 0;");

        // Microphone shortcut
        Label micToggleShortcutLabel = new Label("Microphone Toggle:");
        micToggleShortcutLabel.setStyle("-fx-font-size: 14px;");

        TextField micToggleShortcut = new TextField();
        micToggleShortcut.setPrefWidth(150);
        micToggleShortcut.setStyle("-fx-font-size: 14px;");
        micToggleShortcut.setText(configDTO.getKeyboardShortcuts().getMicToggleShortcut());

        // Description
        Label shortcutDescription = new Label("Press this key to quickly toggle your microphone on/off");
        shortcutDescription.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px;");

        // Create grid layout for shortcuts
        GridPane shortcutsGrid = new GridPane();
        shortcutsGrid.setHgap(15);
        shortcutsGrid.setVgap(10);
        shortcutsGrid.add(micToggleShortcutLabel, 0, 0);
        shortcutsGrid.add(micToggleShortcut, 1, 0);
        shortcutsGrid.add(shortcutDescription, 1, 1);

        // Add a listener to detect changes and update the DTO
        micToggleShortcut.textProperty().addListener(createChangeListener(() ->
                configDTO.getKeyboardShortcuts().setMicToggleShortcut(micToggleShortcut.getText())));

        // Add everything to the VBox
        keyboardShortcuts.getChildren().addAll(
                sectionTitle,
                new Separator(),
                shortcutsHeader,
                shortcutsGrid
        );
    }

    private void setupSpeechModelSettings(VBox speechModelSettings, Button saveButton) {
        // Section title
        Label sectionTitle = new Label("Speech Model Settings");
        sectionTitle.setFont(Font.font("System", FontWeight.BOLD, 22));
        sectionTitle.setStyle("-fx-text-fill: #2c3e50;");

        // Model settings header
        Label modelHeader = new Label("AI Recognition Preferences");
        modelHeader.setFont(Font.font("System", FontWeight.BOLD, 16));
        modelHeader.setStyle("-fx-text-fill: #2c3e50; -fx-padding: 10 0 15 0;");

        // Create grid for settings
        GridPane modelGrid = new GridPane();
        modelGrid.setHgap(15);
        modelGrid.setVgap(20);

        // Specialty setting
        Label specialtyLabel = new Label("Medical specialty:");
        specialtyLabel.setStyle("-fx-font-size: 14px;");

        ComboBox<String> specialtyComboBox = new ComboBox<>();
        specialtyComboBox.getItems().addAll("Radiology");
        specialtyComboBox.setValue("Radiology");
        specialtyComboBox.setPrefWidth(200);
        specialtyComboBox.setStyle("-fx-font-size: 14px;");

        // Add to grid
        modelGrid.add(specialtyLabel, 0, 0);
        modelGrid.add(specialtyComboBox, 1, 0);

        // Add description
        Label modelDescription = new Label("These settings help Syris AI understand your speech more accurately based on your specialty and accent preferences.");
        modelDescription.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12px; -fx-wrap-text: true;");
        modelDescription.setMaxWidth(550);

        specialtyComboBox.valueProperty().addListener(createChangeListener(() ->
                configDTO.getSpeechModelSettings().setSpecialty(specialtyComboBox.getValue())));

        speechModelSettings.getChildren().addAll(
                sectionTitle,
                new Separator(),
                modelHeader,
                modelGrid,
                new VBox(10, modelDescription)
        );
    }

    private void styleButton(Button button, String normalColor, String hoverColor) {
        button.setStyle(
                "-fx-background-color: " + normalColor + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 14px;" +
                        "-fx-padding: 8 20;" +
                        "-fx-background-radius: 4;"
        );

        button.setOnMouseEntered(e ->
                button.setStyle(
                        "-fx-background-color: " + hoverColor + ";" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 14px;" +
                                "-fx-padding: 8 20;" +
                                "-fx-background-radius: 4;"
                )
        );

        button.setOnMouseExited(e ->
                button.setStyle(
                        "-fx-background-color: " + normalColor + ";" +
                                "-fx-text-fill: white;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 14px;" +
                                "-fx-padding: 8 20;" +
                                "-fx-background-radius: 4;"
                )
        );
    }

    private void saveSettings(Button saveButton, Label confirmationLabel) throws IOException {
        // Collect form values and call the API
        System.out.println("Settings saved!");

        configDTO.getGeneralSettings().commit();
        configDTO.getAudioSettings().commit();
        configDTO.getKeyboardShortcuts().commit();
        configDTO.getSpeechModelSettings().commit();

        toggleSaveButton();

        saveUserConfigDetails(configDTO);

        // Show the confirmation message with slide in/out animation
        confirmationLabel.setVisible(true);

        // Automatically hide the confirmation message after 3 seconds
        Timeline hideConfirmation = new Timeline(
                new KeyFrame(Duration.seconds(3), e -> confirmationLabel.setVisible(false))
        );
        hideConfirmation.play();
    }

    private Config saveUserConfigDetails(ConfigDTO configDTO) throws IOException {
        Config entity = ConfigMapper.toEntity(configDTO, UserPersistence.getCurrentLoginUser());
        String url = "http://localhost:8080/api/user-settings/" + UserPersistence.getCurrentLoginUser().getId();
        OkHttpClient client = new OkHttpClient();
        ObjectMapper objectMapper = new ObjectMapper(); // Jackson for JSON conversion

        // Convert Config object to JSON
        String jsonConfig = objectMapper.writeValueAsString(entity);

        // Create request body
        RequestBody body = RequestBody.create(MediaType.get("application/json"), jsonConfig);

        // Build request
        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .addHeader("Authorization", "Bearer " + UserPersistence.loadUser())
                .addHeader("Content-Type", "application/json")
                .build();

        // Execute request
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response: " + response);
            }

            // Parse and return the updated Config object
            return objectMapper.readValue(response.body().string(), Config.class);
        }
    }

    private void toggleSaveButton() {
        saveButton.setVisible(configDTO.hasChanges());
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
        System.out.println(selectedMic);
        RecordingState.getInstance().changeMicrophone(selectedMic);
    }

    public static void updateProgressBar(double rms) {
        micInputProgress.setProgress(rms);
    }

}
