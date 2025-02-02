package org.example.myapp;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SettingsApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Settings & Tools");

        // Create a sidebar for settings
        ListView<String> settingsList = new ListView<>();
        settingsList.getItems().addAll("General", "Audio", "Keyboard shortcuts", "Speech model");
        settingsList.setPrefWidth(200);

        // General settings pane (from your original code)
        VBox generalSettings = new VBox(10);
        generalSettings.setPadding(new Insets(10));
        generalSettings.getChildren().add(new Label("General Settings Placeholder"));

        // Audio settings pane (from your original code)
        VBox audioSettings = new VBox(10);
        audioSettings.setPadding(new Insets(10));
        audioSettings.getChildren().add(new Label("Audio Settings Placeholder"));

        // Keyboard shortcuts pane
        VBox keyboardShortcuts = new VBox(10);
        keyboardShortcuts.setPadding(new Insets(10));

        Label shortcutsHeader = new Label("Keyboard Shortcuts");
        TableView<Shortcut> shortcutsTable = new TableView<>();
        shortcutsTable.setEditable(true);

        TableColumn<Shortcut, String> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellValueFactory(data -> data.getValue().actionProperty());
        actionColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<Shortcut, String> keysColumn = new TableColumn<>("Keys");
        keysColumn.setCellValueFactory(data -> data.getValue().keysProperty());
        keysColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        shortcutsTable.getColumns().add(actionColumn);
        shortcutsTable.getColumns().add(keysColumn);

        ObservableList<Shortcut> shortcuts = FXCollections.observableArrayList(
                new Shortcut("Mic on/off", "F4"),
                new Shortcut("Open micro editor", "Ctrl + M"),
                new Shortcut("Open smart editor", "F6"),
                new Shortcut("Activate/Deactivate micro editor", "Alt + Shift + E"),
                new Shortcut("Next field", "Ctrl + 9"),
                new Shortcut("Previous field", "Ctrl + 0"),
                new Shortcut("Anchor/Release speech", "F7")
        );

        shortcutsTable.setItems(shortcuts);
        keyboardShortcuts.getChildren().addAll(shortcutsHeader, shortcutsTable);

        // Speech model settings pane (from your original code)
        VBox speechModelSettings = new VBox(10);
        speechModelSettings.setPadding(new Insets(10));
        speechModelSettings.getChildren().add(new Label("Speech Model Settings Placeholder"));

        // Main layout with sidebar and settings pane
        BorderPane mainLayout = new BorderPane();
        mainLayout.setLeft(settingsList);
        mainLayout.setCenter(generalSettings);

        // Change settings pane based on selection
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
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static class Shortcut {
        private final StringProperty action;
        private final StringProperty keys;

        public Shortcut(String action, String keys) {
            this.action = new SimpleStringProperty(action);
            this.keys = new SimpleStringProperty(keys);
        }

        public StringProperty actionProperty() {
            return action;
        }

        public StringProperty keysProperty() {
            return keys;
        }
    }

    // ShortcutEntry Class
    public static class ShortcutEntry {
        private final SimpleStringProperty action;
        private final SimpleStringProperty keys;

        public ShortcutEntry(String action, String keys) {
            this.action = new SimpleStringProperty(action);
            this.keys = new SimpleStringProperty(keys);
        }

        public String getAction() {
            return action.get();
        }

        public void setAction(String action) {
            this.action.set(action);
        }

        public String getKeys() {
            return keys.get();
        }

        public void setKeys(String keys) {
            this.keys.set(keys);
        }
    }
}
