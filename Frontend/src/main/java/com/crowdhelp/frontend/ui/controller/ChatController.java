package com.crowdhelp.frontend.ui.controller;

import com.crowdhelp.backend.dao.MessageDAO;
import com.crowdhelp.backend.model.HelpRequest;
import com.crowdhelp.backend.model.Message;
import com.crowdhelp.backend.model.User;
import com.crowdhelp.backend.util.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.List;

public class ChatController {

    @FXML private VBox messagesContainer;
    @FXML private ScrollPane chatScrollPane;
    @FXML private TextField messageInput;
    @FXML private Label chatHeaderTitle, chatHeaderSub;
    @FXML private Button sendBtn;

    private final MessageDAO messageDAO = new MessageDAO();
    private HelpRequest request;
    private User otherUser;
    private Timeline autoRefresh;

    public void initData(HelpRequest request, User otherUser) {
        this.request = request;
        this.otherUser = otherUser;
        chatHeaderTitle.setText("ðŸ’¬ " + (otherUser != null ? otherUser.getName() : "Chat"));
        chatHeaderSub.setText("Re: " + (request != null ? request.getType() + " request â€” " + request.getLocation() : ""));
        loadMessages();

        // Auto-refresh every 3 seconds
        autoRefresh = new Timeline(new KeyFrame(Duration.seconds(3), e -> refreshMessages()));
        autoRefresh.setCycleCount(Timeline.INDEFINITE);
        autoRefresh.play();
    }

    @FXML
    public void initialize() {
        messageInput.setOnAction(e -> handleSend());
    }

    private void loadMessages() {
        messagesContainer.getChildren().clear();
        if (request == null || otherUser == null) return;

        User me = SessionManager.getInstance().getCurrentUser();
        List<Message> messages = messageDAO.getMessagesBetweenUsers(request.getId(), me.getId(), otherUser.getId());

        if (messages.isEmpty()) {
            Label empty = new Label("No messages yet. Say hello! ðŸ‘‹");
            empty.setStyle("-fx-text-fill: #707EAE; -fx-font-size: 13px;");
            VBox emptyBox = new VBox(empty);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(40, 0, 0, 0));
            messagesContainer.getChildren().add(emptyBox);
            return;
        }

        for (Message msg : messages) {
            messagesContainer.getChildren().add(buildMessageBubble(msg, me.getId()));
        }

        // Scroll to bottom
        javafx.application.Platform.runLater(() ->
            chatScrollPane.setVvalue(1.0)
        );
    }

    private void refreshMessages() {
        messagesContainer.getChildren().clear();
        if (request == null || otherUser == null) return;
        User me = SessionManager.getInstance().getCurrentUser();
        List<Message> messages = messageDAO.getMessagesBetweenUsers(request.getId(), me.getId(), otherUser.getId());
        for (Message msg : messages) {
            messagesContainer.getChildren().add(buildMessageBubble(msg, me.getId()));
        }
        chatScrollPane.setVvalue(1.0);
    }

    private Node buildMessageBubble(Message msg, int myId) {
        boolean isMe = msg.getSenderId() == myId;

        HBox wrapper = new HBox();
        wrapper.setPadding(new Insets(4, 12, 4, 12));
        wrapper.setAlignment(isMe ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        VBox bubble = new VBox(4);

        if (!isMe) {
            Label senderName = new Label(msg.getSenderName());
            senderName.setStyle("-fx-font-size: 10px; -fx-text-fill: #707EAE; -fx-font-weight: bold; -fx-padding: 0 0 2 4;");
            bubble.getChildren().add(senderName);
        }

        Label content = new Label(msg.getContent());
        content.setWrapText(true);
        content.setMaxWidth(380);
        content.setStyle("-fx-font-size: 13px; -fx-text-fill: " + (isMe ? "white" : "#2B3674") + ";");

        VBox msgBox = new VBox(content);
        msgBox.setMaxWidth(380);
        msgBox.setStyle((isMe
                ? "-fx-background-color: linear-gradient(to right, #4318FF, #6641FF); -fx-background-radius: 16 4 16 16;"
                : "-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-border-radius: 4 16 16 16; -fx-border-width: 1; -fx-background-radius: 4 16 16 16;") +
                " -fx-padding: 10 14;");

        Label time = new Label(msg.getTimestamp() != null ? msg.getTimestamp().substring(11, 16) : "");
        time.setStyle("-fx-font-size: 10px; -fx-text-fill: #707EAE; -fx-padding: 0 4;");

        bubble.getChildren().addAll(msgBox, time);
        if (isMe) bubble.setAlignment(Pos.CENTER_RIGHT);
        wrapper.getChildren().add(bubble);
        return wrapper;
    }

    @FXML
    private void handleSend() {
        String text = messageInput.getText().trim();
        if (text.isEmpty() || request == null || otherUser == null) return;

        User me = SessionManager.getInstance().getCurrentUser();
        Message msg = new Message(request.getId(), me.getId(), otherUser.getId(), text);
        messageDAO.sendMessage(msg);
        messageInput.clear();
        refreshMessages();
    }

    @FXML
    private void handleBack() {
        if (autoRefresh != null) autoRefresh.stop();
        com.crowdhelp.SceneManager.showDashboard();
    }
}


