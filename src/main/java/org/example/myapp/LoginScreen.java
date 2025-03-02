package org.example.myapp;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.util.Arrays;
import java.util.List;


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
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 30; -fx-background-color: #f4f4f4; -fx-border-radius: 10; -fx-background-radius: 10;");

        Label titleLabel = new Label("Welcome Back!");
        titleLabel.setFont(new Font("Arial", 20));
        titleLabel.setTextFill(Color.web("#333"));

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        styleTextField(emailField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleTextField(passwordField);

        // Password Toggle Feature
        HBox passwordContainer = new HBox();
        passwordContainer.setAlignment(Pos.CENTER_LEFT);
        passwordContainer.setSpacing(5);

        ImageView eyeIcon = new ImageView(new Image("file:eye.png")); // Replace with actual image path
        eyeIcon.setFitWidth(20);
        eyeIcon.setFitHeight(20);
        eyeIcon.setOnMouseClicked(event -> togglePasswordVisibility(passwordField));

        passwordContainer.getChildren().addAll(passwordField);

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

        root.getChildren().addAll(titleLabel, emailField, passwordContainer, loginButton, errorLabel);

        Scene scene = new Scene(root, 350, 300);
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
//        textField.setOnMouseEntered(e -> textField.setStyle("-fx-border-color: #0078D7; -fx-background-color: #fafafa;"));
//        textField.setOnMouseExited(e -> textField.setStyle("-fx-border-color: #ccc; -fx-background-color: white;"));
    }

    private void styleButton(Button button) {
        button.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white; -fx-font-size: 14px; " +
                "-fx-padding: 10px 20px; -fx-background-radius: 5;");
//        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #005fa3; -fx-text-fill: white;"));
//        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: #0078D7; -fx-text-fill: white;"));
    }

    private void togglePasswordVisibility(PasswordField passwordField) {
        TextInputDialog dialog = new TextInputDialog(passwordField.getText());
        dialog.setTitle("Show Password");
        dialog.setHeaderText("Your password is:");
        dialog.setContentText(passwordField.getText());
        dialog.showAndWait();
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
