package com.crowdhelp.frontend.ui.controller;

import com.crowdhelp.SceneManager;
import com.crowdhelp.backend.dao.RequestDAO;
import com.crowdhelp.backend.dao.ResponseDAO;
import com.crowdhelp.backend.dao.UserDAO;
import com.crowdhelp.backend.model.HelpRequest;
import com.crowdhelp.backend.model.Response;
import com.crowdhelp.backend.model.User;
import com.crowdhelp.backend.service.MatchingService;
import com.crowdhelp.backend.util.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class RequestDetailController {

    @FXML private VBox mainContainer;
    @FXML private ScrollPane scrollPane;

    private final RequestDAO requestDAO = new RequestDAO();
    private final ResponseDAO responseDAO = new ResponseDAO();
    private final UserDAO userDAO = new UserDAO();
    private final MatchingService matchingService = new MatchingService();

    private HelpRequest request;

    public void initData(HelpRequest req) {
        this.request = req;
        buildUI();
    }

    private void buildUI() {
        mainContainer.getChildren().clear();
        mainContainer.setSpacing(20);
        mainContainer.setPadding(new Insets(24));

        User current = SessionManager.getInstance().getCurrentUser();

        // â”€â”€ Request Info Card â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        VBox reqCard = new VBox(14);
        reqCard.setStyle("-fx-background-color: #FFFFFF; " +
                "-fx-border-color: #E2E8F0; -fx-border-radius: 14; -fx-background-radius: 14; " +
                "-fx-border-width: 1; -fx-padding: 0;");

        HBox accentBar = new HBox();
        accentBar.setMinHeight(5); accentBar.setMaxHeight(5);
        accentBar.setStyle("-fx-background-color: " + request.getUrgencyColor() + "; -fx-background-radius: 14 14 0 0;");

        VBox cardInner = new VBox(14);
        cardInner.setPadding(new Insets(20));

        // Header
        HBox header = new HBox(14);
        header.setAlignment(Pos.CENTER_LEFT);
        Label typeIcon = new Label(request.getTypeIcon());
        typeIcon.setStyle("-fx-font-size: 32px;");

        VBox titleBox = new VBox(6);
        HBox.setHgrow(titleBox, Priority.ALWAYS);
        Label titleLbl = new Label(request.getType() + " Help Request");
        titleLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2B3674;");
        HBox metaRow = new HBox(14);
        Label locLbl = new Label("ðŸ“ " + request.getLocation());
        locLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #888DA0;");
        Label dateLbl = new Label("ðŸ• " + request.getDateTime());
        dateLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #888DA0;");
        Label byLbl = new Label("ðŸ‘¤ " + request.getUserName());
        byLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #888DA0;");
        metaRow.getChildren().addAll(locLbl, dateLbl, byLbl);
        titleBox.getChildren().addAll(titleLbl, metaRow);

        VBox badges = new VBox(6);
        badges.setAlignment(Pos.CENTER_RIGHT);
        
        String statusText = request.getStatus();
        if (request.getHelperCount() > 0 && "Pending".equals(statusText)) {
            statusText = "Help Received";
        }
        
        Label statusBadge = new Label(statusText);
        statusBadge.setStyle("-fx-background-color: rgba(255,193,7,0.15); -fx-text-fill: #B8860B; " +
                "-fx-padding: 4 12; -fx-background-radius: 20; -fx-font-weight: bold; -fx-font-size: 12px;");
        Label urgLbl = new Label("âš¡ " + request.getUrgency());
        urgLbl.setStyle("-fx-background-color: rgba(255,140,0,0.15); -fx-text-fill: " + request.getUrgencyColor() +
                "; -fx-padding: 4 12; -fx-background-radius: 20; -fx-font-weight: bold; -fx-font-size: 12px;");
        badges.getChildren().addAll(statusBadge, urgLbl);
        if (request.getHelperCount() > 0) {
            Label helperBadge = new Label("ðŸ‘¥ " + request.getHelperCount() + " Helper" + (request.getHelperCount() > 1 ? "s" : ""));
            helperBadge.setStyle("-fx-background-color: rgba(67, 185, 127, 0.2); -fx-text-fill: #43B97F; -fx-padding: 4 12; -fx-background-radius: 20; -fx-font-weight: bold; -fx-font-size: 12px;");
            badges.getChildren().add(0, helperBadge);
        }
        header.getChildren().addAll(typeIcon, titleBox, badges);

        // Description
        Label descLbl = new Label(request.getDescription());
        descLbl.setWrapText(true);
        descLbl.setStyle("-fx-font-size: 14px; -fx-text-fill: #707EAE; -fx-line-spacing: 3;");

        // Skills
        HBox skillsRow = new HBox(8);
        skillsRow.setAlignment(Pos.CENTER_LEFT);
        if (request.getRequiredSkills() != null && !request.getRequiredSkills().isEmpty()) {
            Label skillsTitle = new Label("ðŸ› ï¸ Skills Needed:");
            skillsTitle.setStyle("-fx-font-size: 12px; -fx-text-fill: #888DA0; -fx-font-weight: bold;");
            skillsRow.getChildren().add(skillsTitle);
            for (String s : request.getRequiredSkills().split(",")) {
                Label b = new Label(s.trim());
                b.setStyle("-fx-background-color: rgba(67, 24, 255, 0.08); -fx-text-fill: #4318FF; " +
                        "-fx-padding: 3 10; -fx-background-radius: 10; -fx-font-size: 12px;");
                skillsRow.getChildren().add(b);
            }
        }

        // Action buttons
        HBox actionRow = new HBox(10);
        actionRow.setPadding(new Insets(10, 0, 0, 0));
        actionRow.setStyle("-fx-border-color: #E2E8F0; -fx-border-width: 1 0 0 0;");

        if (current != null && current.getId() != request.getUserId()) {
            Response currentResponse = responseDAO.getResponse(request.getId(), current.getId());
            boolean hasResponded = currentResponse != null;
            Button offerBtn = new Button(hasResponded ? "âœ… Already Offered Help" : "ðŸ™‹ Offer My Help");
            offerBtn.setStyle("-fx-background-color: " + (hasResponded ? "rgba(76,175,80,0.15)" : "linear-gradient(to right, #6C63FF, #8B7DFF)") +
                    "; -fx-text-fill: " + (hasResponded ? "#81C784" : "white") +
                    "; -fx-padding: 10 22; -fx-cursor: hand; -fx-background-radius: 8; -fx-font-weight: bold; -fx-font-size: 13px;");
            offerBtn.setDisable(hasResponded);
            offerBtn.setOnAction(e -> {
                Response r = new Response(request.getId(), current.getId());
                responseDAO.createResponse(r);
                offerBtn.setText("âœ… Offer Sent!");
                offerBtn.setDisable(true);
                buildUI(); // refresh
            });

            // Chat with requester
            if (hasResponded) {
                User requester = userDAO.findById(request.getUserId());
                Button chatBtn = new Button("ðŸ’¬ Chat with Requester");
                chatBtn.setStyle("-fx-background-color: rgba(0,188,212,0.1); -fx-text-fill: #00838F; " +
                        "-fx-padding: 10 22; -fx-cursor: hand; -fx-background-radius: 8; -fx-font-size: 13px; " +
                        "-fx-border-color: rgba(0,188,212,0.3); -fx-border-radius: 8; -fx-border-width: 1;");
                chatBtn.setOnAction(e -> SceneManager.showChat(request, requester));
                actionRow.getChildren().add(chatBtn);

                if ("Accepted".equals(currentResponse.getStatus())) {
                    Button doneBtn = new Button("âœ… Help Done");
                    doneBtn.setStyle("-fx-background-color: linear-gradient(to right, #4CAF50, #81C784); " +
                            "-fx-text-fill: white; -fx-padding: 10 22; -fx-cursor: hand; -fx-background-radius: 8; -fx-font-size: 13px; -fx-font-weight: bold;");
                    doneBtn.setOnAction(e -> {
                        responseDAO.updateStatus(currentResponse.getId(), "Completed");
                        requestDAO.updateStatus(request.getId(), "Completed");
                        buildUI(); // refresh
                    });
                    actionRow.getChildren().add(doneBtn);
                } else if ("Completed".equals(currentResponse.getStatus())) {
                    Button doneBtn = new Button("ðŸŽ‰ Help Completed");
                    doneBtn.setStyle("-fx-background-color: rgba(76,175,80,0.12); -fx-text-fill: #2E7D32; " +
                            "-fx-padding: 10 22; -fx-background-radius: 8; -fx-font-size: 13px; -fx-font-weight: bold;");
                    doneBtn.setDisable(true);
                    actionRow.getChildren().add(doneBtn);
                }
            }

            actionRow.getChildren().add(0, offerBtn);
        }

        Button backBtn = new Button("â† Back to Feed");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #888DA0; " +
                "-fx-cursor: hand; -fx-padding: 10 16; -fx-font-size: 12px;");
        backBtn.setOnAction(e -> SceneManager.showDashboard());
        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);
        actionRow.getChildren().addAll(sp, backBtn);

        cardInner.getChildren().addAll(header, descLbl, skillsRow, actionRow);
        reqCard.getChildren().addAll(accentBar, cardInner);
        mainContainer.getChildren().add(reqCard);

        // â”€â”€ Responses Section â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        if (current != null && current.getId() == request.getUserId()) {
            List<Response> responses = responseDAO.getResponsesForRequest(request.getId());
            VBox responsesSection = buildSection("ðŸ‘¥ Helper Responses (" + responses.size() + ")", buildResponsesList(responses, current));
            mainContainer.getChildren().add(responsesSection);
        }

        // â”€â”€ Recommended Helpers Section â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        List<MatchingService.HelperScore> recommended = matchingService.findBestHelpers(request, request.getUserId());
        if (!recommended.isEmpty()) {
            VBox recSection = buildSection("ðŸ¤– Recommended Helpers", buildRecommendedList(recommended));
            mainContainer.getChildren().add(recSection);
        }
    }

    private VBox buildSection(String title, VBox content) {
        VBox section = new VBox(12);
        Label sectionTitle = new Label(title);
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2B3674;");
        section.getChildren().addAll(sectionTitle, content);
        return section;
    }

    private VBox buildResponsesList(List<Response> responses, User current) {
        VBox list = new VBox(10);
        if (responses.isEmpty()) {
            Label noResp = new Label("No helpers have responded yet. Check back soon!");
            noResp.setStyle("-fx-text-fill: #A3AED0; -fx-font-size: 13px; -fx-padding: 12 0;");
            list.getChildren().add(noResp);
            return list;
        }
        for (Response resp : responses) {
            HBox card = new HBox(12);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle("-fx-background-color: #F4F7FE; -fx-border-color: #E2E8F0; " +
                    "-fx-border-radius: 10; -fx-background-radius: 10; -fx-padding: 12 16;");

            StackPane avatar = createAvatar(resp.getHelperName(), resp.getHelperId());

            VBox info = new VBox(4);
            HBox.setHgrow(info, Priority.ALWAYS);
            Label name = new Label(resp.getHelperName());
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2B3674;");
            Label detail = new Label("ðŸ“ " + resp.getHelperLocation() + "  â­ " +
                    String.format("%.1f", resp.getHelperRating()));
            detail.setStyle("-fx-font-size: 11px; -fx-text-fill: #888DA0;");
            Label skills = new Label(resp.getHelperSkills() != null ? "ðŸ› ï¸ " + resp.getHelperSkills() : "");
            skills.setStyle("-fx-font-size: 11px; -fx-text-fill: #707EAE;");
            info.getChildren().addAll(name, detail, skills);

            VBox actions = new VBox(6);
            actions.setAlignment(Pos.CENTER_RIGHT);

            Label statusLbl = new Label(resp.getStatus());
            statusLbl.setStyle("-fx-background-color: rgba(255,193,7,0.15); -fx-text-fill: #B8860B; " +
                    "-fx-padding: 3 10; -fx-background-radius: 10; -fx-font-size: 11px;");

            if ("Pending".equals(resp.getStatus())) {
                Button acceptBtn = new Button("âœ… Accept");
                acceptBtn.setStyle("-fx-background-color: rgba(76,175,80,0.2); -fx-text-fill: #81C784; " +
                        "-fx-padding: 5 12; -fx-cursor: hand; -fx-background-radius: 6; -fx-font-size: 12px;");
                acceptBtn.setOnAction(e -> {
                    responseDAO.updateStatus(resp.getId(), "Accepted");
                    requestDAO.updateStatus(request.getId(), "Accepted");
                    buildUI();
                });

                Button rejectBtn = new Button("âœ— Reject");
                rejectBtn.setStyle("-fx-background-color: rgba(255,68,68,0.15); -fx-text-fill: #FF6B6B; " +
                        "-fx-padding: 5 12; -fx-cursor: hand; -fx-background-radius: 6; -fx-font-size: 12px;");
                rejectBtn.setOnAction(e -> {
                    responseDAO.updateStatus(resp.getId(), "Rejected");
                    buildUI();
                });
                actions.getChildren().addAll(acceptBtn, rejectBtn);
            }

            if ("Accepted".equals(resp.getStatus())) {
                User helper = userDAO.findById(resp.getHelperId());
                Button chatBtn = new Button("ðŸ’¬ Chat");
                chatBtn.setStyle("-fx-background-color: linear-gradient(to right, #4318FF, #6641FF); " +
                        "-fx-text-fill: white; -fx-padding: 6 14; -fx-cursor: hand; -fx-background-radius: 6; -fx-font-size: 12px;");
                chatBtn.setOnAction(e -> SceneManager.showChat(request, helper));
                statusLbl.setStyle("-fx-background-color: rgba(76,175,80,0.15); -fx-text-fill: #2E7D32; " +
                        "-fx-padding: 3 10; -fx-background-radius: 10; -fx-font-size: 11px;");
                actions.getChildren().addAll(statusLbl, chatBtn);
            } else {
                actions.getChildren().add(0, statusLbl);
            }

            card.getChildren().addAll(avatar, info, actions);
            list.getChildren().add(card);
        }
        return list;
    }

    private VBox buildRecommendedList(List<MatchingService.HelperScore> helpers) {
        VBox list = new VBox(10);
        for (MatchingService.HelperScore hs : helpers) {
            HBox card = new HBox(12);
            card.setAlignment(Pos.CENTER_LEFT);
            card.setStyle("-fx-background-color: #F4F7FE; " +
                    "-fx-border-color: #E2E8F0; -fx-border-radius: 10; -fx-background-radius: 10; " +
                    "-fx-border-width: 1; -fx-padding: 12 16;");

            StackPane avatar = createAvatar(hs.user.getName(), hs.user.getId());

            VBox info = new VBox(4);
            HBox.setHgrow(info, Priority.ALWAYS);
            Label name = new Label(hs.user.getName());
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2B3674;");
            Label loc = new Label("ðŸ“ " + hs.user.getLocation() + "   â­ " +
                    String.format("%.1f", hs.user.getReputationScore()));
            loc.setStyle("-fx-font-size: 11px; -fx-text-fill: #888DA0;");
            Label reason = new Label(hs.matchReason);
            reason.setStyle("-fx-font-size: 11px; -fx-text-fill: #6C63FF;");
            info.getChildren().addAll(name, loc, reason);

            Label score = new Label(String.format("%.0f pts", hs.score));
            score.setStyle("-fx-background-color: rgba(108,99,255,0.2); -fx-text-fill: #6C63FF; " +
                    "-fx-font-weight: bold; -fx-padding: 4 10; -fx-background-radius: 8; -fx-font-size: 12px;");

            card.getChildren().addAll(avatar, info, score);
            list.getChildren().add(card);
        }
        return list;
    }

    private StackPane createAvatar(String name, int id) {
        String[] colors = {"#6C63FF", "#FF6584", "#43B97F", "#F9A825", "#00BCD4", "#E91E63"};
        StackPane p = new StackPane();
        p.setMinSize(40, 40); p.setMaxSize(40, 40);
        p.setStyle("-fx-background-color: " + colors[Math.abs(id) % colors.length] + "; -fx-background-radius: 50;");
        String initials = "?";
        if (name != null && !name.isEmpty()) {
            String[] parts = name.trim().split("\\s+");
            if (parts.length >= 2) initials = "" + parts[0].charAt(0) + parts[1].charAt(0);
            else initials = String.valueOf(parts[0].charAt(0)).toUpperCase();
        }
        Label lbl = new Label(initials.toUpperCase());
        lbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        p.getChildren().add(lbl);
        return p;
    }
}


