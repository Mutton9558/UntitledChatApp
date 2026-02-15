package main;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

// This is where most of the UI is handled
public class UserInterface extends Application {
    @FXML
    private Label statusLabel;
    private static UserInterface instance;

    public UserInterface() {
        instance = this;
    }

    public static UserInterface getInstance() {
        return instance;
    }

    @FXML
    public void initialize() {
        if (ClassGlobalVariables.userFetched.get()) {
            statusLabel.setText("Loaded immediately!");
        } else {
            statusLabel.setText("Loading...");
        }
    }

    public void updateStatus(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }
    @Override
    public void start(Stage primaryStage) throws IOException {
        System.out.println("This is JavaFX UI thread");

        // Continue with the setup
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));

        primaryStage.setTitle("Chat App");
        primaryStage.setScene(new Scene(root));
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    @FXML
    public void handleOnboardingClick(ActionEvent event) {
        System.out.println("Button was clicked");
        updateStatus("Button Clicked");
    }
}
