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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.util.Arrays;
import java.util.List;


public class LoginScreen extends Application {
    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws LineUnavailableException {
        this.primaryStage = primaryStage;

        if (UserPersistence.userExists()) {
            validateStoredUser();
        } else {
            loadLoginScreen();
        }
    }

    private void validateStoredUser() throws LineUnavailableException {
        String email = UserPersistence.loadUser();
        boolean isValid = checkUserValidity(email);

        if (isValid) {
            loadMainApp();
        } else {
            UserPersistence.clearUser();
            loadLoginScreen();
        }
    }

    private boolean checkUserValidity(String email) {

        return "admin@example.com".equals(email);
    }

    private void loadLoginScreen() {
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        Label titleLabel = new Label("Login");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter password");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> {
            String email = emailField.getText();
            String password = passwordField.getText();

            if (email.isEmpty() || password.isEmpty()) {
                errorLabel.setText("Fields cannot be empty!");
                return;
            }

            LoginDTO loginDTO = new LoginDTO(email, password);
            boolean success = sendLoginRequest(loginDTO);

            if (success) {
                UserPersistence.saveUser(email);
                try {
                    loadMainApp();
                } catch (LineUnavailableException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                errorLabel.setText("Invalid email or password.");
            }
        });

        root.getChildren().addAll(titleLabel, emailField, passwordField, loginButton, errorLabel);
        Scene scene = new Scene(root, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login");
        primaryStage.show();
    }

    private boolean sendLoginRequest(LoginDTO loginDTO) {

        return "admin@example.com".equals(loginDTO.getEmail()) && "password".equals(loginDTO.getPassword());
    }

    private void loadMainApp() throws LineUnavailableException {
        primaryStage.close();
        new BackgroundIndicatorWidget().start(new Stage());
//        Button logoutButton = new Button("Logout");
//        logoutButton.setOnAction(e -> {
//            UserPersistence.clearUser();
//            loadLoginScreen();
//        });
    }

}
