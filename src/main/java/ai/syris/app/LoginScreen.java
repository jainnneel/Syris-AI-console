package ai.syris.app;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.*;

import javax.sound.sampled.*;


public class LoginScreen extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws LineUnavailableException {
        if (UserPersistence.userExists()) {
            validateStoredUser(primaryStage);
        } else {
            loadLoginScreen(primaryStage);
        }
    }

    private void validateStoredUser(Stage primaryStage) throws LineUnavailableException {
        String email = UserPersistence.loadUser();
        boolean isValid = checkUserValidity(email);

        if (isValid) {
            loadMainApp();
        } else {
            UserPersistence.clearUser();
            loadLoginScreen(primaryStage);
        }
    }

    private boolean checkUserValidity(String email) {
        return "admin".equals(email);
    }

    public void loadLoginScreen(Stage primaryStage) {
        primaryStage.getIcons().add(new Image("logo.png"));
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30; -fx-background-color: #ffffff; -fx-border-radius: 20; -fx-background-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 5);");

        ImageView logo = new ImageView(new Image("logo.png")); // Replace with actual logo path
        logo.setFitWidth(100);
        logo.setFitHeight(100);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        styleTextField(emailField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleTextField(passwordField);

        // Password Toggle Feature
        StackPane passwordContainer = new StackPane();
        passwordContainer.setAlignment(Pos.CENTER_RIGHT);

        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText("Password");
        styleTextField(visiblePasswordField);
        visiblePasswordField.setVisible(false);

        ImageView eyeIcon = new ImageView(new Image("icons8-show-password-24.png")); // Replace with actual image path
        eyeIcon.setFitWidth(24);
        eyeIcon.setFitHeight(24);

        eyeIcon.setOnMouseClicked(event -> {
            boolean isVisible = visiblePasswordField.isVisible();
            visiblePasswordField.setVisible(!isVisible);
            passwordField.setVisible(isVisible);
            if (!isVisible) {
                visiblePasswordField.setText(passwordField.getText());
            } else {
                passwordField.setText(visiblePasswordField.getText());
            }
        });

        passwordContainer.getChildren().addAll(passwordField, visiblePasswordField, eyeIcon);
        Label errorLabel = new Label();
        errorLabel.setTextFill(Color.RED);

        Button loginButton = new Button("Login");
        styleButton(loginButton);
        loginButton.setOnAction(e -> handleLogin(emailField, passwordField, errorLabel, primaryStage));

        // Handle pressing 'Enter' to login
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleLogin(emailField, passwordField, errorLabel, primaryStage);
            }
        });

        root.getChildren().addAll(logo, emailField, passwordContainer, loginButton, errorLabel);

        Scene scene = new Scene(root, 380, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Syris AI");
        primaryStage.show();
    }

    private void handleLogin(TextField emailField, PasswordField passwordField, Label errorLabel, Stage primaryStage) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("⚠ Fields cannot be empty!");
            return;
        }

        LoginDTO loginDTO = new LoginDTO(email, password);
        boolean success = sendLoginRequest(loginDTO);

        if (success) {
            UserPersistence.saveUser(email);
            try {
                primaryStage.close();
                loadMainApp();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            errorLabel.setText("⚠ Invalid email or password.");
        }
    }

    private void styleTextField(TextField textField) {
        textField.setStyle("-fx-font-size: 14px; -fx-padding: 8px; -fx-border-radius: 5; -fx-background-radius: 5; " +
                "-fx-border-color: #ccc; -fx-background-color: white;");
    }

    private void styleButton(Button button) {
        button.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 10px 20px; -fx-background-radius: 5;");
    }

    private boolean sendLoginRequest(LoginDTO loginDTO) {

        return "admin".equals(loginDTO.getEmail()) && "admin".equals(loginDTO.getPassword());
    }

    private void loadMainApp() throws LineUnavailableException {
        new BackgroundIndicatorWidget().start(new Stage());
//        Button logoutButton = new Button("Logout");
//        logoutButton.setOnAction(e -> {
//            UserPersistence.clearUser();
//            loadLoginScreen();
//        });
    }

}
