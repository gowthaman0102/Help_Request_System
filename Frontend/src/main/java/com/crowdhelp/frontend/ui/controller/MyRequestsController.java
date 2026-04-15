package com.crowdhelp.frontend.ui.controller;

import com.crowdhelp.SceneManager;
import com.crowdhelp.backend.dao.RequestDAO;
import com.crowdhelp.backend.dao.ResponseDAO;
import com.crowdhelp.backend.model.HelpRequest;
import com.crowdhelp.backend.model.Response;
import com.crowdhelp.backend.util.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class MyRequestsController {

    @FXML private VBox requestsContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private Label headerCount;

    private final RequestDAO requestDAO = new RequestDAO();
    private final ResponseDAO responseDAO = new ResponseDAO();

    @FXML
    public void initialize() {
        loadMyRequests();
    }

    private void loadMyRequests() {
        requestsContainer.getChildren().clear();
        requestsContainer.setSpacing(16);
        requestsContainer.setPadding(new Insets(24));

        int userId = SessionManager.getInstance().getCurrentUser().getId();
        List<HelpRequest> myRequests = requestDAO.getRequestsByUser(userId);

        if (headerCount != null) {
            headerCount.setText(myRequests.size() + " request" + (myRequests.size() != 1 ? "s" : ""));
        }

        if (myRequests.isEmpty()) {
            VBox empty = new VBox(14);
            empty.setAlignment(Pos.CENTER);
            empty.setPadding(new Insets(80, 0, 0, 0));
            Label icon = new Label("ðŸ“‹");
            icon.setStyle("-fx-font-size: 52px;");
            Label text = new Label("You haven't posted any requests yet");
            text.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2B3674;");
            Label sub = new Label("Click \"Post Request\" from the sidebar to get started!");
            sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #707EAE;");
            empty.getChildren().addAll(icon, text, sub);
            requestsContainer.getChildren().add(empty);
            return;
        }

        for (HelpRequest req : myRequests) {
            requestsContainer.getChildren().add(buildMyRequestCard(req));
        }
    }

    private VBox buildMyRequestCard(HelpRequest req) {
        VBox card = new VBox();
        card.setStyle("-fx-background-color: #FFFFFF; " +
                "-fx-border-color: #E2E8F0; -fx-border-radius: 12; -fx-background-radius: 12; " +
                "-fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(112,144,176,0.12), 10, 0, 0, 2);");

        // Accent bar
        HBox accent = new HBox();
        accent.setMinHeight(4); accent.setMaxHeight(4);
        accent.setStyle("-fx-background-color: " + req.getUrgencyColor() + "; -fx-background-radius: 12 12 0 0;");

        VBox inner = new VBox(12);
        inner.setPadding(new Insets(16, 20, 16, 20));

        // Top row
        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        Label typeIcon = new Label(req.getTypeIcon());
        typeIcon.setStyle("-fx-font-size: 24px;");

        VBox info = new VBox(4);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label titleLbl = new Label(req.getType() + " Request â€” " + req.getLocation());
        titleLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2B3674;");
        Label dateLbl = new Label("ðŸ• " + req.getDateTime());
        dateLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #6B7080;");
        info.getChildren().addAll(titleLbl, dateLbl);

        Label statusBadge = new Label(req.getStatus());
        String statusStyle = switch (req.getStatus()) {
            case "Pending" -> "rgba(255,193,7,0.15); -fx-text-fill: #B8860B;";
            case "Accepted" -> "rgba(76,175,80,0.15); -fx-text-fill: #2E7D32;";
            case "In Progress" -> "rgba(33,150,243,0.15); -fx-text-fill: #1565C0;";
            case "Completed" -> "rgba(0,188,212,0.15); -fx-text-fill: #006064;";
            default -> "rgba(158,158,158,0.15); -fx-text-fill: #757575;";
        };
        statusBadge.setStyle("-fx-background-color: " + statusStyle + " -fx-padding: 4 12; " +
                "-fx-background-radius: 20; -fx-font-weight: bold; -fx-font-size: 12px;");

        topRow.getChildren().addAll(typeIcon, info, statusBadge);

        // Description
        Label desc = new Label(req.getDescription());
        desc.setWrapText(true);
        desc.setStyle("-fx-font-size: 13px; -fx-text-fill: #707EAE;");

        // Responses info
        List<Response> responses = responseDAO.getResponsesForRequest(req.getId());
        long acceptedCount = responses.stream().filter(r -> "Accepted".equals(r.getStatus())).count();

        HBox statsRow = new HBox(16);
        statsRow.setStyle("-fx-padding: 8 0 0 0; -fx-border-color: #E2E8F0; -fx-border-width: 1 0 0 0;");
        statsRow.setAlignment(Pos.CENTER_LEFT);

        Label responsesLbl = new Label("ðŸ‘¥ " + responses.size() + " helper" + (responses.size() != 1 ? "s" : "") + " responded");
        responsesLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #707EAE;");
        Label acceptedLbl = new Label("âœ… " + acceptedCount + " accepted");
        acceptedLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #2E7D32;");
        Label urgencyLbl = new Label("âš¡ " + req.getUrgency());
        urgencyLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: " + req.getUrgencyColor() + ";");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Action buttons
        HBox actions = new HBox(8);
        Button viewBtn = new Button("View Details");
        viewBtn.setStyle("-fx-background-color: rgba(67,24,255,0.08); -fx-text-fill: #4318FF; " +
                "-fx-padding: 6 14; -fx-cursor: hand; -fx-background-radius: 6; " +
                "-fx-border-color: rgba(67,24,255,0.25); -fx-border-radius: 6; -fx-border-width: 1; -fx-font-size: 12px;");
        viewBtn.setOnAction(e -> SceneManager.showRequestDetail(req));

        // Status progression buttons
        if ("Pending".equals(req.getStatus()) && acceptedCount > 0) {
            Button progressBtn = new Button("â–¶ Mark In Progress");
            progressBtn.setStyle("-fx-background-color: rgba(33,150,243,0.12); -fx-text-fill: #1565C0; " +
                    "-fx-padding: 6 14; -fx-cursor: hand; -fx-background-radius: 6; -fx-font-size: 12px;");
            progressBtn.setOnAction(e -> {
                requestDAO.updateStatus(req.getId(), "In Progress");
                loadMyRequests();
            });
            actions.getChildren().add(progressBtn);
        }

        if ("In Progress".equals(req.getStatus())) {
            Button completeBtn = new Button("âœ… Mark Completed");
            completeBtn.setStyle("-fx-background-color: rgba(76,175,80,0.12); -fx-text-fill: #2E7D32; " +
                    "-fx-padding: 6 14; -fx-cursor: hand; -fx-background-radius: 6; -fx-font-size: 12px;");
            completeBtn.setOnAction(e -> {
                requestDAO.updateStatus(req.getId(), "Completed");
                loadMyRequests();
            });
            actions.getChildren().add(completeBtn);
        }

        actions.getChildren().add(viewBtn);
        statsRow.getChildren().addAll(responsesLbl, acceptedLbl, urgencyLbl, spacer, actions);

        inner.getChildren().addAll(topRow, desc, statsRow);
        card.getChildren().addAll(accent, inner);
        return card;
    }
}


