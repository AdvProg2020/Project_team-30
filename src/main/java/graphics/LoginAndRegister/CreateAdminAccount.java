package graphics.LoginAndRegister;

import Controller.LoginAndRegisterManager;
import graphics.AlertBox;
import graphics.App;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;

public class CreateAdminAccount {
    public Button confirmButton;
    public TextField usernameField;
    public TextField firstNameField;
    public TextField lastNameField;
    public TextField emailField;
    public TextField phoneNumberField;
    public PasswordField passwordField;

    private static String parentMenu;

    private LoginAndRegisterManager loginAndRegisterManager;

    public void initialize() {
        this.loginAndRegisterManager = new LoginAndRegisterManager();
    }

    public void confirm(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String phoneNumber = phoneNumberField.getText();
        boolean confirmButtonInability = username.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phoneNumber.isEmpty();
        if (!(confirmButtonInability)) {
            try {
                loginAndRegisterManager.registerAdmin(username, password, firstName, lastName, email, phoneNumber);
                AlertBox.showMessage("Admin Register", "Admin Successfully registered");
            } catch (IllegalArgumentException e) {
                AlertBox.showMessage("Failed to Register", e.getMessage());
            }
            try {
                App.setRoot(parentMenu);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void setParentMenu(String parentMenu) {
        CreateAdminAccount.parentMenu = parentMenu;
    }
}