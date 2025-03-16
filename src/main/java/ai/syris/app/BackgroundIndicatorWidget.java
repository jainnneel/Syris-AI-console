package ai.syris.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.sound.sampled.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Objects;

public class BackgroundIndicatorWidget extends Application {

    private double offsetX;
    private double offsetY;

    @Override
    public void start(Stage primaryStage) throws LineUnavailableException {
        try {
            initializeApplication(primaryStage);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    static ImageView imageView = new ImageView();
    static ImageView logo = new ImageView();

    static Button recordButton = new Button("", imageView);
    static Label indicatorLabel = new Label("Syris AI", logo);
    static ContextMenu contextMenu = new ContextMenu();

    private void loadWidget(Stage primaryStage) throws FileNotFoundException {
        // Create a rounded background pane
        StackPane background = new StackPane();
        background.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-background-radius: 15; -fx-padding: 10px;");

        logo.setImage(new Image("logo.png", 40,40,true,true));

        imageView.setImage(new Image("icons8-play-record-30.png"));
        // Indicator label
        indicatorLabel.setStyle("-fx-text-fill: #00cc44; -fx-font-size: 14px; -fx-font-weight: bold;");

        // Recording Button
        recordButton.setStyle("-fx-background-color: #ffcc00; -fx-text-fill: black; -fx-font-size: 12px; " +
                "-fx-background-radius: 20;");
        recordButton.setOnAction(e -> toggleRecording());

        // Settings Icon (instead of text menu)
        Image menuImage = new Image("icons8-menu-vertical-20.png");
        ImageView imageView = new ImageView(menuImage);
        Button menuButton = new Button("", imageView);
        menuButton.setMaxWidth(5);
        menuButton.setOnMouseClicked(e -> showContextMenu(menuButton, e, primaryStage));

        HBox container = new HBox(10, indicatorLabel, recordButton, menuButton);
        container.setAlignment(Pos.CENTER_LEFT);

        HBox.setMargin(indicatorLabel, new Insets(5, 10, 5, 3));
        HBox.setMargin(recordButton, new Insets(5, 10, 5, 10));
        HBox.setMargin(menuButton, new Insets(5, 0, 5, 10));

        background.getChildren().add(container);

        Scene scene = new Scene(background);
        scene.setFill(Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(scene);

        makeDraggable(primaryStage, background);
        primaryStage.show();

    }

    private void initializeApplication(Stage primaryStage) throws LineUnavailableException, FileNotFoundException {

        loadWidget(primaryStage);

//        TargetDataLine targetDataLine = null;
//        List<Mixer> aLlAvailableMics = RecordingUtils.getALlAvailableMics();
//        if(!aLlAvailableMics.isEmpty()) {
//            targetDataLine = (TargetDataLine) aLlAvailableMics.get(0).getLine(new DataLine.Info(TargetDataLine.class, new AudioFormat(44100.0f, 16, 1, true, true)));
//        } else {
//            // No mics available
//        }
//
//        RecordingState.getInstance().changeMicrophone(targetDataLine);
    }

    private void showContextMenu(Button settingsButton, MouseEvent event, Stage stage) {
        contextMenu.getItems().clear();
        contextMenu.hide();
        Stage settingStage = new Stage();
        MenuItem settingsItem = new MenuItem("Settings");
        settingsItem.setOnAction(e -> new SettingScreen().showConsole(settingStage));

        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(event1 -> {
            UserPersistence.clearUser();  // Clear saved user data
            stage.close();
            settingStage.close();
            new LoginScreen().loadLoginScreen(new Stage());  // Redirect to login screen
        });

        MenuItem quitItem = new MenuItem("Quit");
        quitItem.setOnAction(e -> System.exit(0));

        contextMenu.getItems().addAll(settingsItem, logoutItem, quitItem);
        contextMenu.show(settingsButton, event.getScreenX(), event.getScreenY());
    }

    /**
     * Toggles the recording between start and stop.
     */
    public static void toggleRecording() {
        if (RecordingState.getInstance().isRecording()) {
            imageView.setImage(new Image("icons8-play-record-30.png"));
            indicatorLabel.setStyle("-fx-text-fill: #00cc44; -fx-font-size: 14px; -fx-font-weight: bold;");
            RecordingState.getInstance().toggleRecording();
        } else {
            if (Objects.isNull(RecordingState.getInstance().getTargetDataLine())) {
                alertDialog();
            } else {
                imageView.setImage(new Image("icons8-block-microphone-30.png"));
                indicatorLabel.setStyle("-fx-text-fill: #00cc44; -fx-font-size: 14px; -fx-font-weight: bold;");
                RecordingState.getInstance().toggleRecording();
            }
        }
    }

    public static void alertDialog(){
        // Create an Alert dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Warning");
        alert.setHeaderText(null);

        // Customize the content with red-colored text
        Label label = new Label("First select the Audio set from setting!");
        label.setStyle("-fx-text-fill: red; -fx-font-size: 14px;");

        // Set the custom label inside the alert
        alert.getDialogPane().setContent(label);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE); // Adjust size

        // Show the alert
        alert.showAndWait();
    }

    /**
     * Toggles the recording between start and stop.
     */
    public static void stopRecoding() {
        imageView.setImage(new Image("icons8-play-record-30.png"));
        indicatorLabel.setStyle("-fx-text-fill: #00cc44; -fx-font-size: 14px; -fx-font-weight: bold;");
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