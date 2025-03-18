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
import javafx.scene.layout.VBox;
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
    private String username = "Neel"; // Default username, can be updated from settings

    @Override
    public void start(Stage primaryStage) throws LineUnavailableException {
        try {
            initializeApplication(primaryStage);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static ImageView userIconView = new ImageView();
    static Label usernameLabel = new Label();
    static Label micStatusLabel = new Label();
    static ContextMenu contextMenu = new ContextMenu();
    static Separator progressBar = new Separator();

    private void loadWidget(Stage primaryStage) throws FileNotFoundException {
        // Create a rounded background pane
        StackPane background = new StackPane();
        background.setStyle("-fx-background-color: rgba(64, 64, 64, 0.9); -fx-background-radius: 25; -fx-padding: 10px;");

        // User icon (logo)
        userIconView.setImage(new Image("logo.png", 60, 60, true, true));

        // Username label
        usernameLabel.setText(username);
        usernameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Mic status label
        micStatusLabel.setText("Tap to switch Mic On");
        micStatusLabel.setStyle("-fx-text-fill: #48D1CC; -fx-font-size: 14px;");

        // Progress bar (as separator line)
        progressBar.setPrefWidth(200);
        progressBar.setStyle("-fx-background: #48D1CC;");

        // Create a vertical box for the username, mic status, and progress bar
        VBox userInfoBox = new VBox(5);
        userInfoBox.getChildren().addAll(usernameLabel, progressBar, micStatusLabel);
        userInfoBox.setAlignment(Pos.CENTER_LEFT);

        // Settings/Menu button with hamburger menu styling in a cyan rectangle
        Button menuButton = new Button("");
        ImageView menuIcon = new ImageView(new Image("hamburger-menu.png", 24, 24, true, true));
        menuButton.setGraphic(menuIcon);
        menuButton.setStyle(" -fx-background-radius: 5; -fx-padding: 5;");
        menuButton.setOnMouseClicked(e -> showContextMenu(menuButton, e, primaryStage));

        // Apply custom CSS to style the context menu
        String contextMenuCss =
                ".context-menu {" +
                        "    -fx-background-color: white;" +
                        "    -fx-background-radius: 0;" +
                        "    -fx-border-color: #CCCCCC;" +
                        "    -fx-border-width: 0.5;" +
                        "    -fx-padding: 0;" +
                        "}" +
                        ".menu-item {" +
                        "    -fx-padding: 8 16 8 16;" +
                        "}" +
                        ".menu-item .label {" +
                        "    -fx-text-fill: black;" +
                        "}";

        contextMenu.getStyleClass().add("custom-context-menu");

        // Main container
        HBox container = new HBox(15);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(5, 10, 5, 10));

        // Add components to container
        container.getChildren().addAll(userIconView, userInfoBox);

        // Add spacer to push menu button to the right
        Region spacer = new Region();
        spacer.setPrefWidth(50);
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        container.getChildren().addAll(spacer, menuButton);

        // Make the user icon/area clickable to toggle recording
        userIconView.setOnMouseClicked(e -> toggleRecording());

        background.getChildren().add(container);

        Scene scene = new Scene(background);
        scene.getStylesheets().add(createStyleSheet(contextMenuCss));
        scene.setFill(Color.TRANSPARENT);

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.setScene(scene);
        primaryStage.setWidth(350);

        makeDraggable(primaryStage, background);
        primaryStage.show();
    }

    private String createStyleSheet(String css) {
        try {
            java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("context-menu-style", ".css");
            java.nio.file.Files.write(tempFile, css.getBytes());
            return tempFile.toUri().toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void initializeApplication(Stage primaryStage) throws LineUnavailableException, FileNotFoundException {
        loadWidget(primaryStage);

        // Commented out the original microphone setup code
        // This would be restored when implementing the actual functionality
        /*
        TargetDataLine targetDataLine = null;
        List<Mixer> aLlAvailableMics = RecordingUtils.getALlAvailableMics();
        if(!aLlAvailableMics.isEmpty()) {
            targetDataLine = (TargetDataLine) aLlAvailableMics.get(0).getLine(new DataLine.Info(TargetDataLine.class, new AudioFormat(44100.0f, 16, 1, true, true)));
        } else {
            // No mics available
        }

        RecordingState.getInstance().changeMicrophone(targetDataLine);
        */
    }

    private void showContextMenu(Button settingsButton, MouseEvent event, Stage stage) {
        contextMenu.getItems().clear();
        contextMenu.hide();
        Stage settingStage = new Stage();

        // Create custom-styled menu items
        MenuItem settingsItem = new MenuItem("Settings");
        settingsItem.setStyle(" -fx-font-weight: bold;");
        settingsItem.setOnAction(e -> new SettingScreen().showConsole(settingStage));

        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(event1 -> {
            UserPersistence.clearUser();  // Clear saved user data
            stage.close();
            settingStage.close();
            new LoginScreen().loadLoginScreen(new Stage());  // Redirect to login screen
        });

        MenuItem quitItem = new MenuItem("Quit");
        quitItem.setStyle("-fx-text-fill: #8B0000;");
        quitItem.setOnAction(e -> System.exit(0));

        // Add separators for visual distinction
        SeparatorMenuItem separator1 = new SeparatorMenuItem();
        SeparatorMenuItem separator2 = new SeparatorMenuItem();

        contextMenu.getItems().addAll(settingsItem, separator1, logoutItem, separator2, quitItem);

        // Style the context menu
        contextMenu.setStyle("-fx-background-color: white; -fx-background-radius: 0; -fx-border-color: #CCCCCC; -fx-border-width: 0.5;");

        // Show the menu below the button
        double x = settingsButton.localToScreen(settingsButton.getBoundsInLocal()).getMinX();
        double y = settingsButton.localToScreen(settingsButton.getBoundsInLocal()).getMaxY();
        contextMenu.show(settingsButton, x, y);
    }

    /**
     * Toggles the recording between start and stop.
     */
    public static void toggleRecording() {
        if (RecordingState.getInstance().isRecording()) {
            micStatusLabel.setText("Tap to switch Mic On");
            userIconView.setOpacity(1.0);
            progressBar.setVisible(true);
            progressBar.setStyle("-fx-background: #48D1CC;");
            RecordingState.getInstance().toggleRecording();
        } else {
            if (Objects.isNull(RecordingState.getInstance().getTargetDataLine())) {
                alertDialog();
            } else {
                micStatusLabel.setText("Recording... Tap to stop");
                userIconView.setOpacity(0.7); // Visual indication of active recording
                progressBar.setVisible(true);
                progressBar.setStyle("-fx-background: #00CC44;"); // Active recording color
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
     * Stops recording and updates UI.
     */
    public static void stopRecoding() {
        micStatusLabel.setText("Tap to switch Mic On");
        userIconView.setOpacity(1.0);
        progressBar.setStyle("-fx-background: #48D1CC;");
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