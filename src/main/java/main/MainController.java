package main;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private VBox LoginForm;
    @FXML
    private VBox SignupForm;

    @FXML
    public void handleOnboardingClick() {
        System.out.println("Button was clicked");
        UserInterface.getInstance().showMainUI(true);
    }

    @FXML
    public void handleShowSignup() {
        System.out.println("Showing Signup Form");
        LoginForm.setVisible(false);
        LoginForm.setManaged(false);
        SignupForm.setVisible(true);
        SignupForm.setManaged(true);
    }

    @FXML
    public void handleShowLogin() {
        System.out.println("Showing Login Form");
        SignupForm.setVisible(false);
        SignupForm.setManaged(false);
        LoginForm.setVisible(true);
        LoginForm.setManaged(true);
    }
}
