package ai.syris.app;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class LoginScreen extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws LineUnavailableException {
        var listView = new ListView<String>();

        ModuleLayer.boot()
                .modules()
                .stream()
                .map(Module::getName)
                .sorted()
                .forEach(listView.getItems()::add);

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

    private boolean checkUserValidity(String token) {
        ObjectMapper objectMapper = new ObjectMapper();
        String urlString = "http://localhost:8080/api/users/current";

        try {
            // Open connection
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Configure request
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + token);

            // Handle response
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println("Request failed: " + responseCode);
                return false;
            }

            System.out.println("Response Code: " + responseCode);

            // Read response
            StringBuilder responseJson = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    responseJson.append(line.trim());
                }
            }

            System.out.println(responseJson);

            // Convert JSON to User object
            User user = objectMapper.readValue(responseJson.toString(), User.class);
            UserPersistence.setCurrentLoginUser(user);

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
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
        try {
            // URL and Connection Setup
            URL url = new URL("http://localhost:8080/auth/login");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Configure Connection
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setDoOutput(true);

            // JSON Request Body
            String json = "{\"username\":\"" + loginDTO.getEmail() + "\", \"password\":\"" + loginDTO.getPassword() + "\"}";

            // Write Request Body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Read Response
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read response body
                StringBuilder responseJson = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        responseJson.append(line.trim());
                    }
                }

                System.out.println(responseJson);

                // Convert response JSON to AuthResponse
                AuthResponse authResponse = new ObjectMapper().readValue(responseJson.toString(), AuthResponse.class);

                UserPersistence.saveUser(authResponse.getToken());
                checkUserValidity(authResponse.getToken());

                return true;
            } else {
                System.out.println("Request failed: " + responseCode);
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void loadMainApp() throws LineUnavailableException {
        new BackgroundIndicatorWidget().start(new Stage());
    }

}
