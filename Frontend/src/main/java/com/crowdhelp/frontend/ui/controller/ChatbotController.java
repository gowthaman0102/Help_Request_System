package com.crowdhelp.frontend.ui.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ChatbotController {

    @FXML private VBox chatContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private TextField inputField;

    @FXML
    public void initialize() {
        // Scroll to bottom when new messages arrive
        chatContainer.heightProperty().addListener((obs, oldVal, newVal) -> scrollPane.setVvalue(1.0));
        
        // Initial greeting
        addBotMessage("Hi there! ðŸ¤– I'm the CrowdHelp assistant. You can ask me about features like 'Social Feed', 'Post Request', 'Matches', 'Messages', or 'Profile'. How can I help you today?");
    }

    @FXML
    public void handleSend() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        // User message
        addUserMessage(text);
        inputField.clear();

        // Bot response logically derived from checking keywords
        String lowerText = text.toLowerCase();
        String response = getBotResponse(lowerText);
        
        // Simulate minor delay if we wanted to, but synchronous is fine
        addBotMessage(response);
    }

    private String getBotResponse(String q) {
        if (q.contains("feed") || q.contains("social") || q.contains("home")) {
            return "The Social Feed shows all ongoing help requests. You can filter them by category (like Blood, Transport) or click 'Offer Help' to assist someone matching your skills!";
        } else if (q.contains("post") || q.contains("create") || q.contains("request") || q.contains("need")) {
            return "To get help, go to the 'Post Request' section from the sidebar. Just fill out the category, description, your location, and urgency. Our AI will notify matching helpers!";
        } else if (q.contains("match") || q.contains("recommend") || q.contains("ai")) {
            return "Our intelligent matching system calculates a score based on distance, required skills, and user reputation to find the perfect helper. You can see recommended helpers on your request details page.";
        } else if (q.contains("chat") || q.contains("message") || q.contains("dm")) {
            return "Once a request is accepted by a helper, a direct chat channel opens up inside 'Messages'. You can coordinate details securely without sharing personal phone numbers immediately.";
        } else if (q.contains("profile") || q.contains("reputation") || q.contains("rating") || q.contains("skill")) {
            return "Your Profile displays your current location, skills, and reputation score. When you help others or receive help, you get rated. Higher reputation boosts your visibility!";
        } else if (q.contains("already helped") || q.contains("multiple") || q.contains("accept")) {
            return "Even if someone has already offered help, the request remains active! Others can still offer help, and we display a 'X Helpers' badge directly on the feed card.";
        } else {
            return "I'm not exactly sure about that. Try asking me about 'Posting Requests', 'Social Feed', 'Matching', 'Profiles', or 'Chatting'.";
        }
    }

    private void addUserMessage(String text) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPadding(new Insets(5, 10, 5, 40));

        Label lbl = new Label(text);
        lbl.setWrapText(true);
        lbl.setStyle("-fx-background-color: linear-gradient(to right, #4318FF, #6641FF); -fx-text-fill: white; " +
                     "-fx-padding: 8 14; -fx-background-radius: 14 14 0 14; -fx-font-size: 13px;");
        
        box.getChildren().add(lbl);
        chatContainer.getChildren().add(box);
    }

    private void addBotMessage(String text) {
        HBox box = new HBox();
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(5, 40, 5, 10));

        Label lbl = new Label(text);
        lbl.setWrapText(true);
        lbl.setStyle("-fx-background-color: #E2E8F0; -fx-text-fill: #2B3674; " +
                     "-fx-padding: 8 14; -fx-background-radius: 14 14 14 0; -fx-font-size: 13px;");

        box.getChildren().add(lbl);
        chatContainer.getChildren().add(box);
    }
}

