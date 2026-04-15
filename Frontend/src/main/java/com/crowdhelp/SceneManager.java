package com.crowdhelp;

import com.crowdhelp.backend.model.HelpRequest;
import com.crowdhelp.backend.model.User;
import com.crowdhelp.frontend.ui.controller.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {
    private static Stage primaryStage;

    public static void initialize(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("CrowdHelp â€” Community Help Platform");
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(720);
        primaryStage.setWidth(1200);
        primaryStage.setHeight(760);
    }

    public static void showLogin() {
        loadScene("/com/crowdhelp/frontend/ui/fxml/login.fxml", null, null);
    }

    public static void showDashboard() {
        loadScene("/com/crowdhelp/frontend/ui/fxml/dashboard.fxml", null, null);
    }

    public static void showRequestDetail(HelpRequest request) {
        loadScene("/com/crowdhelp/frontend/ui/fxml/request_detail.fxml", null, request);
    }

    public static void showChat(HelpRequest request, User targetUser) {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource("/com/crowdhelp/frontend/ui/fxml/chat.fxml"));
        try {
            Parent root = loader.load();
            ChatController ctrl = loader.getController();
            ctrl.initData(request, targetUser);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(SceneManager.class.getResource("/com/crowdhelp/frontend/ui/css/styles.css").toExternalForm());
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadScene(String fxmlPath, Object controller, Object data) {
        try {
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            if (controller != null) loader.setController(controller);
            Parent root = loader.load();

            if (data instanceof HelpRequest && loader.getController() instanceof RequestDetailController) {
                ((RequestDetailController) loader.getController()).initData((HelpRequest) data);
            }

            Scene scene = new Scene(root);
            String cssPath = SceneManager.class.getResource("/com/crowdhelp/frontend/ui/css/styles.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Failed to load scene: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}


