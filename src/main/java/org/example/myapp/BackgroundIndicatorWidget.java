package org.example.myapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.sound.sampled.*;
import java.util.Arrays;
import java.util.List;

public class BackgroundIndicatorWidget extends Application {

    private double offsetX;
    private double offsetY;

    @Override
    public void start(Stage primaryStage) throws LineUnavailableException {

        intializeApplication();

        // Create a label for the indicator
        Label indicatorLabel = new Label("App Running");
        indicatorLabel.setStyle("-fx-background-color: #00cc44; -fx-text-fill: white; -fx-padding: 10px; " +
                "-fx-font-size: 14px; -fx-background-radius: 10; -fx-cursor: hand;");

        // Create the start/stop recording button inside the indicator
        Button recordButton = new Button("Start Recording");
        recordButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: white; -fx-font-size: 12px; " +
                "-fx-padding: 5px; -fx-background-radius: 5;");

        // Create the context menu with two items
        ContextMenu contextMenu = new ContextMenu();

        // Option 1: Open Scene
        MenuItem openSceneItem = new MenuItem("Settings");
        openSceneItem.setOnAction(event -> new SettingApp1().showConsole());

        // Option 2: Close Application
        MenuItem closeAppItem = new MenuItem("quit");
        closeAppItem.setOnAction(event -> System.exit(0));

        // Add items to the context menu
        contextMenu.getItems().addAll(openSceneItem, closeAppItem);

        // Show the context menu on right-click
        indicatorLabel.setOnContextMenuRequested(event -> contextMenu.show(indicatorLabel, event.getScreenX(), event.getScreenY()));

        // Handle record button action (Start and Stop recording)
        recordButton.setOnAction(event -> toggleRecording(recordButton));

        // Create a HBox to hold the label and button
        HBox container = new HBox(indicatorLabel, recordButton);
        container.setSpacing(5);
        container.setStyle("-fx-alignment: center;");

        StackPane root = new StackPane(container);
        root.setStyle("-fx-background-color: transparent;");

        // Create a transparent scene
        Scene scene = new Scene(root, 200, 50);
        scene.setFill(Color.TRANSPARENT);

        // Configure the stage
        primaryStage.initStyle(StageStyle.TRANSPARENT); // Transparent window
        primaryStage.setAlwaysOnTop(true); // Always on top
        primaryStage.setScene(scene);

        // Make the widget draggable
        makeDraggable(primaryStage, root);

        // Position the widget at the top-right corner
        positionStage(primaryStage);

        // Show the widget
        primaryStage.show();
    }

    private void intializeApplication() throws LineUnavailableException {
        //Get user customize data using API call

        // if mic available then use existing if not then use first available

        TargetDataLine targetDataLine = null;
        List<Mixer> aLlAvailableMics = RecordingUtils.getALlAvailableMics();
        if(!aLlAvailableMics.isEmpty()) {
            targetDataLine = (TargetDataLine) aLlAvailableMics.get(0).getLine(new DataLine.Info(TargetDataLine.class, new AudioFormat(44100.0f, 16, 1, true, true)));
        } else {
            // No mics available
        }

        RecordingState.getInstance().changeMicrophone(targetDataLine);
    }

    /**
     * Toggles the recording between start and stop.
     *
     * @param recordButton The button that starts/stops recording.
     */
    private void toggleRecording(Button recordButton) {
        if (RecordingState.getInstance().isRecording()) {
            recordButton.setText("Start Recording");
        } else {
            recordButton.setText("Stop Recording");
        }

        RecordingState.getInstance().toggleRecording();
    }

    /**
     * Positions the stage at the top-right corner of the screen.
     *
     * @param stage The stage to position.
     */
    private void positionStage(Stage stage) {
        stage.setX(javafx.stage.Screen.getPrimary().getBounds().getMaxX() - 160); // X offset
        stage.setY(10); // Y offset
    }

    /**
     * Makes the widget draggable by tracking mouse press and drag events.
     *
     * @param stage The stage to make draggable.
     * @param root  The root node of the scene.
     */
    private void makeDraggable(Stage stage, StackPane root) {
        root.setOnMousePressed(event -> {
            offsetX = event.getSceneX();
            offsetY = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - offsetX);
            stage.setY(event.getScreenY() - offsetY);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}