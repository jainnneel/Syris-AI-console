package ai.syris.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class BackgroundIndicatorWidget extends Application {

    private double offsetX;
    private double offsetY;
    private String username = UserPersistence.getCurrentLoginUser().getUsername();

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
    Stage settingStage = new Stage();

    private Config setCurrentUserConfig() {
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
            return objectMapper.readValue(response.body().string(), Config.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleKeyPress(KeyEvent event, String shortcut) {
        // Parse shortcut string and check if it matches the pressed key combination
        if (matchesShortcut(event, config.getMicToggleShortcut())) {
            System.out.println("Shortcut " + shortcut + " pressed! Executing function...");
            performAction();
        }
    }

    private boolean matchesShortcut(KeyEvent event, String shortcut) {
        String[] keys = shortcut.split("\\+");
        boolean ctrl = false, alt = false, shift = false;
        KeyCode mainKey = null;

        for (String key : keys) {
            key = key.trim();
            switch (key) {
                case "Ctrl":
                    ctrl = true;
                    break;
                case "Alt":
                    alt = true;
                    break;
                case "Shift":
                    shift = true;
                    break;
                default:
                    mainKey = KeyCode.valueOf(key.toUpperCase()); // Convert to KeyCode
            }
        }

        // Check if the required modifiers are pressed
        boolean ctrlPressed = event.isControlDown();
        boolean altPressed = event.isAltDown();
        boolean shiftPressed = event.isShiftDown();

        // Compare with the actual pressed key
        return (ctrl == ctrlPressed) && (alt == altPressed) && (shift == shiftPressed) && (event.getCode() == mainKey);
    }

    private void performAction() {
        BackgroundIndicatorWidget.toggleRecording();
        System.out.println("Function executed successfully!");
    }


    public static Position loadUser() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            FileInputStream fileInputStream = new FileInputStream(new File("position.json"));

            if (fileInputStream.available() > 0) {
                return mapper.readValue(fileInputStream, Position.class);
            }
        } catch (IOException e) {
//            e.printStackTrace();
        }
        return null;
    }

    private static Config config = null;

    public static void setConfig(Config configs) {
        config = configs;
    }

    private void loadWidget(Stage primaryStage) throws FileNotFoundException {

        config = setCurrentUserConfig();

        if (config.isWidgetPosition())
        {
            Position position = loadUser();
            if (position != null){
                primaryStage.setX(position.getX());
                primaryStage.setY(position.getY());
            }
        }

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

        scene.setOnKeyPressed(event -> handleKeyPress(event, config.getMicToggleShortcut()));

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
    }

    private void showContextMenu(Button settingsButton, MouseEvent event, Stage stage) {
        contextMenu.getItems().clear();
        contextMenu.hide();

        // Create custom-styled menu items
        MenuItem settingsItem = new MenuItem("Settings");
        settingsItem.setStyle(" -fx-font-weight: bold;");
        settingsItem.setOnAction(e -> {
            if (settingStage.isShowing()) {
                settingStage.toFront();
            } else {
                new SettingScreen().showConsole(settingStage);
            }
        });

        MenuItem logoutItem = new MenuItem("Logout");
        logoutItem.setOnAction(event1 -> {
            UserPersistence.clearUser();  // Clear saved user data
            stage.close();
            settingStage.close();
            new LoginScreen().loadLoginScreen(new Stage());  // Redirect to login screen
        });

        MenuItem quitItem = new MenuItem("Quit");
        quitItem.setStyle("-fx-text-fill: #8B0000;");
        quitItem.setOnAction(e -> {
            savePosition(stage.getX(), stage.getY());
            System.exit(0);
        });

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

    public static void savePosition(double x, double y) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Position position = new Position(x, y);
            mapper.writeValue(new File("position.json"), position);
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void updateProgressBarWidget(double rms) {
        Platform.runLater(() -> progressBar.setPrefWidth(rms));
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