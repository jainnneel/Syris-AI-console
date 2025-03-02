package org.example.myapp;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.control.*;

import javax.sound.sampled.*;
import java.util.List;

public class BackgroundIndicatorWidget extends Application {

    private double offsetX;
    private double offsetY;

    @Override
    public void start(Stage primaryStage) throws LineUnavailableException {
        initializeApplication(primaryStage);
    }

    private void loadWidget(Stage primaryStage) {
        // Create a rounded background pane
        StackPane background = new StackPane();
        background.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-background-radius: 15; -fx-padding: 10px;");

        // Indicator label
        Label indicatorLabel = new Label("● App Running");
        indicatorLabel.setStyle("-fx-text-fill: #00cc44; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Recording Button
        Button recordButton = new Button("Start Recording");
        recordButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: black; -fx-font-size: 12px; " +
                "-fx-padding: 7px 14px; -fx-background-radius: 20;");
//        recordButton.setOnMouseEntered(e -> recordButton.setStyle("-fx-background-color: #ffaa00; -fx-text-fill: black;"));
//        recordButton.setOnMouseExited(e -> recordButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: black;"));
        recordButton.setOnAction(e -> toggleRecording(recordButton, indicatorLabel));

        // Settings Icon (instead of text menu)
        Circle settingsButton = new Circle(10, Color.LIGHTGRAY);
        settingsButton.setOnMouseClicked(e -> showContextMenu(settingsButton, e, primaryStage));

        HBox container = new HBox(10, indicatorLabel, recordButton, settingsButton);
        container.setStyle("-fx-alignment: center-left;");

        background.getChildren().add(container);

        Scene scene = new Scene(background, 270, 60);
        scene.setFill(Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(scene);

        makeDraggable(primaryStage, background);
        primaryStage.show();

    }

    private void initializeApplication(Stage primaryStage) throws LineUnavailableException {

        // GET call for user details
        loadWidget(primaryStage);

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

    private void showContextMenu(Circle settingsButton, MouseEvent event, Stage stage) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem settingsItem = new MenuItem("Settings");
        settingsItem.setOnAction(e -> new SettingApp1().showConsole());

        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(event1 -> {
            UserPersistence.clearUser();  // Clear saved user data
            stage.close();

            new LoginScreen().loadLoginScreen(new Stage());  // Redirect to login screen
        });

        MenuItem quitItem = new MenuItem("Quit");
        quitItem.setOnAction(e -> System.exit(0));

        contextMenu.getItems().addAll(settingsItem, logoutItem, quitItem);
        contextMenu.show(settingsButton, event.getScreenX(), event.getScreenY());
    }

    /**
     * Toggles the recording between start and stop.
     *
     * @param recordButton The button that starts/stops recording.
     */
    private void toggleRecording(Button recordButton, Label indicatorLabel) {
        if (RecordingState.getInstance().isRecording()) {
            recordButton.setText("Start Recording");
            recordButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: black; -fx-font-size: 12px; " +
                    "-fx-padding: 7px 14px; -fx-background-radius: 20;");
            indicatorLabel.setText("● Syris-AI");
            indicatorLabel.setStyle("-fx-text-fill: #00cc44;");
        } else {
            recordButton.setText("Stop Recording");
            recordButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: black; -fx-font-size: 12px; " +
                    "-fx-padding: 7px 14px; -fx-background-radius: 20;");
            indicatorLabel.setText("● Recording...");
            indicatorLabel.setStyle("-fx-text-fill: #00cc44;");
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