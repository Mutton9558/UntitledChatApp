package main;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

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
        this.primaryStage.setMinHeight(400);
        this.primaryStage.setMinWidth(600);

        showMainUI(false);
    }

    public void showMainUI(boolean loggedIn) {
        Platform.runLater(() -> {
            String fxmlFile = loggedIn ? "main.fxml" : "onboarding.fxml";
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/" + fxmlFile));
                Parent root = loader.load();
                double scaleFactor = 1.0; // Adjust this value to scale the UI

                Scale scale = new Scale(scaleFactor, scaleFactor);
                scale.setPivotX(0);
                scale.setPivotY(0);
                root.getTransforms().add(scale);

                primaryStage.setWidth(primaryStage.getWidth() * scaleFactor);
                primaryStage.setHeight(primaryStage.getHeight() * scaleFactor);

                Stage stageToUse = (instance != null && instance.primaryStage != null) ? instance.primaryStage : primaryStage;

                // If the stage is not initialized yet, create a new one
                if (stageToUse != null) {
                    Scene scene = new Scene(root);
                    stageToUse.setScene(scene);

                    // Load CSS
                    String cssPath = Objects.requireNonNull(getClass().getResource("/main/onboarding.css")).toExternalForm();
                    scene.getStylesheets().add(cssPath);

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
