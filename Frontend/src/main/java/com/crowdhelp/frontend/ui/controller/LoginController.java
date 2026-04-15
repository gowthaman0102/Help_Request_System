package com.crowdhelp.frontend.ui.controller;

import com.crowdhelp.SceneManager;
import com.crowdhelp.backend.model.User;
import com.crowdhelp.backend.service.AuthService;
import com.crowdhelp.backend.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class LoginController {

    @FXML private ToggleButton loginTab, registerTab;
    @FXML private ToggleGroup tabGroup;
    @FXML private VBox loginPane, registerPane;
    @FXML private Label messageLabel;

    // Login fields
    @FXML private TextField loginEmail;
    @FXML private PasswordField loginPassword;

    // Register fields
    @FXML private TextField regName, regEmail, regLocation, regSkills;
    @FXML private PasswordField regPassword;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        loginTab.setOnAction(e -> switchTab(true));
        registerTab.setOnAction(e -> switchTab(false));

        // Apply toggle button styles
        styleToggle(loginTab, true);
        styleToggle(registerTab, false);
    }

    private void switchTab(boolean showLogin) {
        loginPane.setVisible(showLogin);
        loginPane.setManaged(showLogin);
        registerPane.setVisible(!showLogin);
        registerPane.setManaged(!showLogin);
        messageLabel.setVisible(false);
        messageLabel.setManaged(false);
        styleToggle(loginTab, showLogin);
        styleToggle(registerTab, !showLogin);
    }

    private void styleToggle(ToggleButton btn, boolean active) {
        btn.getStyleClass().removeAll("btn-primary", "btn-secondary");
        if (active) {
            btn.getStyleClass().add("btn-primary");
        } else {
            btn.getStyleClass().add("btn-secondary");
        }
    }

    @FXML
    private void handleLogin() {
        String email = loginEmail.getText().trim();
        String pass = loginPassword.getText();

        if (email.isEmpty() || pass.isEmpty()) {
            showMessage("Please enter email and password.", false);
            return;
        }

        User user = authService.login(email, pass);
        if (user != null) {
            SessionManager.getInstance().setCurrentUser(user);
            SceneManager.showDashboard();
        } else {
            showMessage("Invalid email or password. Try: @example.com / password123", false);
        }
    }

    @FXML
    private void handleRegister() {
        String name = regName.getText().trim();
        String email = regEmail.getText().trim();
        String pass = regPassword.getText();
        String location = regLocation.getText().trim();
        String skills = regSkills.getText().trim();

        String result = authService.register(name, email, pass, location, skills);
        if ("SUCCESS".equals(result)) {
            showMessage("Account created! Please sign in.", true);
            switchTab(true);
            loginEmail.setText(email);
        } else {
            showMessage(result, false);
        }
    }

    private void showMessage(String msg, boolean success) {
        messageLabel.setText(msg);
        messageLabel.getStyleClass().removeAll("error-label", "success-label");
        messageLabel.getStyleClass().add(success ? "success-label" : "error-label");
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }
}


