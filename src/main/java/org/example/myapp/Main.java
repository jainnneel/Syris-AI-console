package org.example.myapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        // TabPane for navigation
        TabPane tabPane = new TabPane();

        // Create tabs
        Tab generalTab = new Tab("General");
        Tab audioTab = new Tab("Audio");
        Tab keyboardShortcutsTab = new Tab("Keyboard Shortcuts");
        Tab speechModelTab = new Tab("Speech Model");

        // Set up content for each tab
        generalTab.setContent(createGeneralTab());
        audioTab.setContent(createAudioTab());
        keyboardShortcutsTab.setContent(createKeyboardShortcutsTab());
        speechModelTab.setContent(createSpeechModelTab());

        // Add tabs to the TabPane
        tabPane.getTabs().addAll(generalTab, audioTab, keyboardShortcutsTab, speechModelTab);
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        // Main layout
        BorderPane root = new BorderPane();
        root.setCenter(tabPane);

        // Scene and Stage
        Scene scene = new Scene(root, 800, 600);

        ClassLoader classLoader = Main.class.getClassLoader();
        URL resourceURL = classLoader.getResource("styles.css");


        scene.getStylesheets().add(resourceURL.toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Settings & Tools");
        primaryStage.show();
    }

    // Create General Tab Content
    private VBox createGeneralTab() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        content.getChildren().add(new Label("General Settings"));
        return content;
    }

    // Create Audio Tab Content
    private VBox createAudioTab() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label micLabel = new Label("Microphone Input Level");
        Slider micSlider = new Slider();

        Label micInUse = new Label("Microphone in Use");
        ComboBox<String> micDropdown = new ComboBox<>();
        micDropdown.getItems().addAll("Stereo Mix (Realtek High Definition Audio)");

        CheckBox backgroundSuppression = new CheckBox("Background Voice Suppression");
        Label suppressionNote = new Label("Note: Voice suppression level resets to off at 5 AM daily");

        content.getChildren().addAll(micLabel, micSlider, micInUse, micDropdown, backgroundSuppression, suppressionNote);
        return content;
    }

    // Create Keyboard Shortcuts Tab Content
    private VBox createKeyboardShortcutsTab() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        grid.add(new Label("Action"), 0, 0);
        grid.add(new Label("Keys"), 1, 0);

        grid.add(new Label("Mic on/off"), 0, 1);
        grid.add(new Label("F4"), 1, 1);

        grid.add(new Label("Open micro editor"), 0, 2);
        grid.add(new Label("Ctrl + M"), 1, 2);

        grid.add(new Label("Anchor/Release speech"), 0, 3);
        grid.add(new Label("F7"), 1, 3);

        content.getChildren().add(grid);
        return content;
    }

    // Create Speech Model Tab Content
    private VBox createSpeechModelTab() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));

        Label specialtyLabel = new Label("Medical Specialty");
        ComboBox<String> specialtyDropdown = new ComboBox<>();
        specialtyDropdown.getItems().addAll(
                "Radiology",
                "Discharge Summary",
                "General Medicine",
                "Gynecology",
                "Surgery"
        );

        content.getChildren().addAll(specialtyLabel, specialtyDropdown);
        return content;
    }

    public static void main(String[] args) {
        launch(args);
    }
}