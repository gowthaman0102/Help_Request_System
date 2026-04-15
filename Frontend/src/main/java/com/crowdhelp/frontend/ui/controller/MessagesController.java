package com.crowdhelp.frontend.ui.controller;

import com.crowdhelp.SceneManager;
import com.crowdhelp.backend.dao.RequestDAO;
import com.crowdhelp.backend.dao.ResponseDAO;
import com.crowdhelp.backend.dao.UserDAO;
import com.crowdhelp.backend.model.HelpRequest;
import com.crowdhelp.backend.model.Response;
import com.crowdhelp.backend.model.User;
import com.crowdhelp.backend.util.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

public class MessagesController {

    @FXML private VBox conversationsContainer;
    @FXML private Label headerSub;

    private final ResponseDAO responseDAO = new ResponseDAO();
    private final RequestDAO requestDAO = new RequestDAO();
    private final UserDAO userDAO = new UserDAO();

    @FXML
    public void initialize() {
        loadConversations();
    }

    private void loadConversations() {
        conversationsContainer.getChildren().clear();
        conversationsContainer.setSpacing(12);
        conversationsContainer.setPadding(new Insets(20));

        User me = SessionManager.getInstance().getCurrentUser();

        // Get conversations from accepted responses (where I am helper)
        List<Response> myHelperResponses = responseDAO.getResponsesByHelper(me.getId());
        List<HelpRequest> conversations = new ArrayList<>();

        for (Response r : myHelperResponses) {
            if ("Accepted".equals(r.getStatus())) {
                HelpRequest req = requestDAO.getRequestById(r.getRequestId());
                if (req != null) conversations.add(req);
            }
        }

        // Get conversations where I am requester (accepted helpers)
        List<HelpRequest> myRequests = requestDAO.getRequestsByUser(me.getId());
        for (HelpRequest req : myRequests) {
            List<Response> responses = responseDAO.getResponsesForRequest(req.getId());
            boolean hasAccepted = responses.stream().anyMatch(r -> "Accepted".equals(r.getStatus()));
            if (hasAccepted && !conversations.contains(req)) {
                conversations.add(req);
            }
        }

        if (headerSub != null) {
            headerSub.setText(conversations.size() + " active conversation" + (conversations.size() != 1 ? "s" : ""));
        }

        if (conversations.isEmpty()) {
            VBox empty = new VBox(14);
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(80, 0, 0, 0));
            Label icon = new Label("ðŸ’¬");
            icon.setStyle("-fx-font-size: 52px;");
            Label text = new Label("No conversations yet");
            text.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2B3674;");
            Label sub = new Label("Accept a help request to start chatting!");
            sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #707EAE;");
            empty.getChildren().addAll(icon, text, sub);
            conversationsContainer.getChildren().add(empty);
            return;
        }

        for (HelpRequest req : conversations) {
            conversationsContainer.getChildren().add(buildConversationCard(req, me));
        }
    }

    private HBox buildConversationCard(HelpRequest req, User me) {
        HBox card = new HBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(14, 18, 14, 18));
        card.setStyle("-fx-background-color: #FFFFFF; " +
                "-fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-background-radius: 12; " +
                "-fx-border-width: 1; -fx-cursor: hand;");
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: rgba(67, 24, 255, 0.05); " +
                "-fx-border-color: rgba(67, 24, 255, 0.3); -fx-border-radius: 12; -fx-background-radius: 12; " +
                "-fx-border-width: 1; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #FFFFFF; " +
                "-fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-background-radius: 12; " +
                "-fx-border-width: 1; -fx-cursor: hand;"));

        // Type icon
        Label typeIco = new Label(req.getTypeIcon());
        typeIco.setStyle("-fx-font-size: 26px; -fx-background-color: rgba(67, 24, 255, 0.08); " +
                "-fx-padding: 10; -fx-background-radius: 10;");

        VBox info = new VBox(5);
        HBox.setHgrow(info, Priority.ALWAYS);

        // Determine chat partner
        String partnerName;
        User chatPartner = null;
        if (req.getUserId() == me.getId()) {
            // I'm the requester â€” find accepted helper
            List<Response> responses = responseDAO.getResponsesForRequest(req.getId());
            Response accepted = responses.stream().filter(r -> "Accepted".equals(r.getStatus())).findFirst().orElse(null);
            if (accepted != null) {
                chatPartner = userDAO.findById(accepted.getHelperId());
                partnerName = chatPartner != null ? chatPartner.getName() : "Helper";
            } else {
                partnerName = "Helper";
            }
        } else {
            // I'm the helper â€” chat with requester
            chatPartner = userDAO.findById(req.getUserId());
            partnerName = req.getUserName() != null ? req.getUserName() : "Requester";
        }

        Label nameLbl = new Label("ðŸ’¬ " + partnerName);
        nameLbl.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2B3674;");
        Label reqLbl = new Label(req.getTypeIcon() + " " + req.getType() + " â€” " + req.getLocation());
        reqLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #707EAE;");
        info.getChildren().addAll(nameLbl, reqLbl);

        Label statusBadge = new Label(req.getStatus());
        statusBadge.setStyle("-fx-background-color: rgba(76,175,80,0.15); -fx-text-fill: #2E7D32; " +
                "-fx-padding: 3 10; -fx-background-radius: 10; -fx-font-size: 11px;");

        Label openChat = new Label("â†’");
        openChat.setStyle("-fx-font-size: 18px; -fx-text-fill: #4318FF;");

        final User finalChatPartner = chatPartner;
        card.setOnMouseClicked(e -> {
            if (finalChatPartner != null) {
                SceneManager.showChat(req, finalChatPartner);
            }
        });

        card.getChildren().addAll(typeIco, info, statusBadge, openChat);
        return card;
    }
}


