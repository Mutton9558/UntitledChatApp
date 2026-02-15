import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

// This is where most of the UI is handled
public class UserInterface extends Application {
    private Label statusLabel;
    private static UserInterface instance;

    public UserInterface() {
        instance = this;
    }

    public static UserInterface getInstance() {
        return instance;
    }

    public void updateStatus(String message) {
        Platform.runLater(() -> statusLabel.setText(message));
    }
    @Override
    public void start(Stage primaryStage){
        System.out.println("This is JavaFX UI thread");
        // Simple Setup
        statusLabel = new Label("Loading User...");

        // Check if background thread finish before userInterface even open
        if (ClassGlobalVariables.userFetched.get()) {
            statusLabel.setText("Loaded immediately!");
            // Go straight to next screen
        } else {
            statusLabel.setText("Loading...");
        }

        // Continue with the setup
        StackPane root = new StackPane(statusLabel);
        Scene scene = new Scene(root, 400, 300);

        primaryStage.setTitle("Chat App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
