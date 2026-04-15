package com.crowdhelp.frontend.ui.controller;

import com.crowdhelp.SceneManager;
import com.crowdhelp.backend.dao.RequestDAO;
import com.crowdhelp.backend.model.User;
import com.crowdhelp.backend.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;

public class DashboardController {

    @FXML private StackPane contentPane;
    @FXML private Label userNameLabel, userEmailLabel, avatarLabel, sidebarStats;
    @FXML private VBox avatarBox;
    @FXML private Button navFeed, navPost, navMyReq, navChat, navProfile;
    @FXML private VBox chatbotWindow;
    @FXML private Button chatbotBtn;

    private final RequestDAO requestDAO = new RequestDAO();
    private Button activeNavBtn;

    private static final String[] AVATAR_COLORS = {
        "#6C63FF", "#FF6584", "#43B97F", "#F9A825", "#00BCD4", "#E91E63"
    };

    @FXML
    public void initialize() {
        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            userNameLabel.setText(user.getName());
            userEmailLabel.setText(user.getEmail());
            avatarLabel.setText(user.getInitials());

            // Set avatar color based on user id
            String color = AVATAR_COLORS[user.getId() % AVATAR_COLORS.length];
            avatarBox.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 50;");

            // Sidebar stats
            int totalRequests = requestDAO.getRequestsByUser(user.getId()).size();
            sidebarStats.setText("ðŸ“Š " + totalRequests + " requests posted");
        }

        activeNavBtn = navFeed;
        showFeed();
    }

    @FXML
    private void showFeed() {
        loadContent("/com/crowdhelp/frontend/ui/fxml/feed.fxml");
        setActive(navFeed);
    }

    @FXML
    private void showPostRequest() {
        loadContent("/com/crowdhelp/frontend/ui/fxml/post_request.fxml");
        setActive(navPost);
    }

    @FXML
    private void showMyRequests() {
        loadContent("/com/crowdhelp/frontend/ui/fxml/my_requests.fxml");
        setActive(navMyReq);
    }

    @FXML
    private void showMessages() {
        loadContent("/com/crowdhelp/frontend/ui/fxml/messages.fxml");
        setActive(navChat);
    }

    @FXML
    private void showProfile() {
        loadContent("/com/crowdhelp/frontend/ui/fxml/profile.fxml");
        setActive(navProfile);
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        SceneManager.showLogin();
    }

    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            contentPane.getChildren().setAll(content);
        } catch (IOException e) {
            System.err.println("Error loading content: " + fxmlPath + " - " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setActive(Button btn) {
        if (activeNavBtn != null) {
            activeNavBtn.getStyleClass().remove("nav-btn-active");
        }
        btn.getStyleClass().add("nav-btn-active");
        activeNavBtn = btn;
    }

    @FXML
    public void toggleChatbot() {
        if (chatbotWindow != null) {
            boolean isVis = chatbotWindow.isVisible();
            chatbotWindow.setVisible(!isVis);
            if (!isVis) {
                chatbotBtn.setText("âœ– Close");
                chatbotBtn.setStyle("-fx-background-color: #FF6584; -fx-text-fill: white; -fx-background-radius: 30; -fx-min-width: 60; -fx-min-height: 60; -fx-font-weight: bold; -fx-font-size: 13px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(112,144,176,0.3), 10, 0, 0, 5);");
            } else {
                chatbotBtn.setText("ðŸ’¬ Help");
                chatbotBtn.setStyle("-fx-background-color: #4318FF; -fx-text-fill: white; -fx-background-radius: 30; -fx-min-width: 60; -fx-min-height: 60; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(112,144,176,0.3), 10, 0, 0, 5);");
            }
        }
    }

    public static DashboardController getInstance() {
        return null; // placeholder
    }
}



