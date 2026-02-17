package main;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

// This is where most of the UI is handled
public class UserInterface extends Application {

    private static UserInterface instance;
    private Stage primaryStage;

    public UserInterface() {
        instance = this;
    }
    public static UserInterface getInstance() {
        return instance;
    }

    public void updateStatus(String message) {
        System.out.println("Update Status: " + message);
    }

    @Override
    public void start(Stage primaryStage) {
        System.out.println("This is JavaFX UI thread");

        this.primaryStage = primaryStage;

        showMainUI(false);
    }

    public void showMainUI(boolean loggedIn) {
        Platform.runLater(() -> {
            String fxmlFile = loggedIn ? "main.fxml" : "onboarding.fxml";
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/" + fxmlFile));
                Parent root = loader.load();

                Stage stageToUse = (instance != null && instance.primaryStage != null) ? instance.primaryStage : primaryStage;

                if (stageToUse != null) {
                    Scene scene = new Scene(root);
                    stageToUse.setScene(scene);
                    stageToUse.setTitle("Paged - " + (loggedIn ? "Chat" : "Onboarding"));
                    stageToUse.show();
                }
            } catch (IOException e) {
                System.err.println("Failed to load FXML: " + fxmlFile + e.getMessage());
                e.printStackTrace();
            }
        });
    }


}
