package ai.syris.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class KeyShortcutExample extends Application {

    // Shortcut key stored as a variable
    private final String shortcut = "F4"; // Change this dynamically if needed

    @Override
    public void start(Stage primaryStage) {
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 400, 300);

        // Set key event handler

        primaryStage.setTitle("JavaFX Key Shortcut Detection");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


//    public static void main(String[] args) {
//        launch(args);
//    }
}