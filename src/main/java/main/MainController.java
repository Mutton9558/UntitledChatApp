package main;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class MainController {
    @FXML
    private TextField usernameField;
    private TextField passwordField;

    @FXML
    public void handleOnboardingClick() {
        System.out.println("Button was clicked");
        UserInterface.getInstance().showMainUI(true);
    }
}
