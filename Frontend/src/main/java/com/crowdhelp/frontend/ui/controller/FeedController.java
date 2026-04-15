package com.crowdhelp.frontend.ui.controller;

import com.crowdhelp.SceneManager;
import com.crowdhelp.backend.dao.RequestDAO;
import com.crowdhelp.backend.dao.ResponseDAO;
import com.crowdhelp.backend.model.HelpRequest;
import com.crowdhelp.backend.model.Response;
import com.crowdhelp.backend.model.User;
import com.crowdhelp.backend.util.SessionManager;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.util.List;

public class FeedController {

    @FXML private VBox feedContainer;
    @FXML private HBox filterBar;
    @FXML private ScrollPane scrollPane;
    @FXML private Label countLabel;

    private final RequestDAO requestDAO = new RequestDAO();
    private final ResponseDAO responseDAO = new ResponseDAO();
    private String activeFilter = "All";

    // Notification banner shown at the top of the feed
    private Label notificationBanner;

    private static final String[] FILTERS = {"All", "Blood", "Emergency", "Transport", "Notes", "Food", "Medical"};
    private static final String[] AVATAR_COLORS = {
        "#6C63FF", "#FF6584", "#43B97F", "#F9A825", "#00BCD4", "#E91E63", "#FF8A50"
    };

    @FXML
    public void initialize() {
        buildFilterBar();
        loadFeed("All");
    }

    private void buildFilterBar() {
        filterBar.setSpacing(8);
        filterBar.setPadding(new Insets(12, 24, 12, 24));
        filterBar.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; -fx-border-width: 0 0 1 0;");

        for (String filter : FILTERS) {
            Button chip = new Button(getFilterIcon(filter) + " " + filter);
            chip.getStyleClass().add("filter-chip");
            if (filter.equals(activeFilter)) chip.getStyleClass().add("filter-chip-active");
            chip.setOnAction(e -> {
                filterBar.getChildren().forEach(n -> {
                    if (n instanceof Button b) {
                        b.getStyleClass().remove("filter-chip-active");
                    }
                });
                chip.getStyleClass().add("filter-chip-active");
                activeFilter = filter;
                loadFeed(filter);
            });
            filterBar.getChildren().add(chip);
        }
    }

    private String getFilterIcon(String filter) {
        return switch (filter) {
            case "Blood" -> "ðŸ©¸";
            case "Emergency" -> "ðŸš¨";
            case "Transport" -> "ðŸš—";
            case "Notes" -> "ðŸ“š";
            case "Food" -> "ðŸ±";
            case "Medical" -> "ðŸ¥";
            default -> "ðŸŒ";
        };
    }

    private void loadFeed(String filter) {
        feedContainer.getChildren().clear();
        feedContainer.setSpacing(14);
        feedContainer.setPadding(new Insets(20, 24, 24, 24));

        List<HelpRequest> requests;
        if ("All".equals(filter)) {
            requests = requestDAO.getAllRequests();
        } else {
            requests = requestDAO.getRequestsByType(filter);
        }

        if (countLabel != null) {
            countLabel.setText(requests.size() + " request" + (requests.size() != 1 ? "s" : ""));
        }

        if (requests.isEmpty()) {
            addEmptyState();
            return;
        }

        for (HelpRequest req : requests) {
            feedContainer.getChildren().add(buildRequestCard(req));
        }
    }

    /** Shows a green notification banner at the TOP of the feed container. */
    private void showNotification(String message) {
        // Remove any previous banner
        if (notificationBanner != null) {
            feedContainer.getChildren().remove(notificationBanner);
        }

        notificationBanner = new Label("âœ…  " + message);
        notificationBanner.setMaxWidth(Double.MAX_VALUE);
        notificationBanner.setWrapText(true);
        notificationBanner.getStyleClass().add("notification-banner");
        notificationBanner.setOpacity(0);

        feedContainer.getChildren().add(0, notificationBanner);

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), notificationBanner);
        fadeIn.setFromValue(0); fadeIn.setToValue(1);
        fadeIn.play();

        // Auto-dismiss after 5 seconds
        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(ev -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(400), notificationBanner);
            fadeOut.setFromValue(1); fadeOut.setToValue(0);
            fadeOut.setOnFinished(ev2 -> feedContainer.getChildren().remove(notificationBanner));
            fadeOut.play();
        });
        pause.play();
    }

    private VBox buildRequestCard(HelpRequest req) {
        VBox card = new VBox();
        card.getStyleClass().add("feed-card");
        card.setCursor(javafx.scene.Cursor.HAND);

        if ("Emergency".equals(req.getUrgency()) || "Critical".equals(req.getUrgency())) {
            card.setStyle("-fx-border-color: #FF4444; -fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12; -fx-background-color: rgba(255, 68, 68, 0.03);");
        } else if ("Urgent".equals(req.getUrgency()) || "High".equals(req.getUrgency())) {
            card.setStyle("-fx-border-color: #FF8C00; -fx-border-width: 1; -fx-border-radius: 12; -fx-background-radius: 12;");
        }

        // Color accent bar based on urgency
        String accentColor = req.getUrgencyColor();
        HBox accentBar = new HBox();
        accentBar.setMinHeight(4);
        accentBar.setMaxHeight(4);
        accentBar.setStyle("-fx-background-color: " + accentColor + "; -fx-background-radius: 14 14 0 0;");

        VBox content = new VBox(12);
        content.setPadding(new Insets(16, 20, 16, 20));

        // --- Top row: avatar + user info + badges ---
        HBox topRow = new HBox(12);
        topRow.setAlignment(Pos.CENTER_LEFT);

        // Avatar
        StackPane avatar = createAvatar(req.getUserName(), req.getUserId());

        VBox userInfo = new VBox(2);
        userInfo.setAlignment(Pos.CENTER_LEFT);
        Label nameLabel = new Label(req.getUserName() != null ? req.getUserName() : "Unknown");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #2B3674;");
        Label locationLabel = new Label("ðŸ“ " + (req.getLocation() != null ? req.getLocation() : ""));
        locationLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #707EAE;");
        userInfo.getChildren().addAll(nameLabel, locationLabel);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Type and urgency badges
        VBox badges = new VBox(4);
        badges.setAlignment(Pos.CENTER_RIGHT);
        Label typeBadge = new Label(req.getTypeIcon() + " " + req.getType());
        typeBadge.getStyleClass().addAll("badge", "badge-" + req.getType().toLowerCase());
        Label urgencyBadge = new Label("âš¡ " + req.getUrgency());
        urgencyBadge.getStyleClass().add("badge");
        urgencyBadge.setStyle("-fx-background-color: " + req.getUrgencyBgColor() + "; -fx-text-fill: " + req.getUrgencyTextColor() + ";");
        badges.getChildren().addAll(typeBadge, urgencyBadge);

        topRow.getChildren().addAll(avatar, userInfo, spacer, badges);

        // --- Description ---
        Label descLabel = new Label(req.getDescription());
        descLabel.setWrapText(true);
        descLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #707EAE; -fx-line-spacing: 2;");
        descLabel.setMaxWidth(Double.MAX_VALUE);

        // --- Skills required ---
        HBox skillsRow = new HBox(6);
        skillsRow.setAlignment(Pos.CENTER_LEFT);
        if (req.getRequiredSkills() != null && !req.getRequiredSkills().trim().isEmpty()) {
            Label skillIcon = new Label("ðŸ› ï¸");
            skillIcon.setStyle("-fx-font-size: 12px;");
            skillsRow.getChildren().add(skillIcon);
            for (String skill : req.getRequiredSkills().split(",")) {
                Label skillBadge = new Label(skill.trim());
                skillBadge.setStyle("-fx-background-color: rgba(67, 24, 255, 0.08); -fx-text-fill: #4318FF; " +
                        "-fx-padding: 2 8; -fx-background-radius: 10; -fx-font-size: 11px;");
                skillsRow.getChildren().add(skillBadge);
            }
        }

        // --- Bottom row: time + status + actions ---
        HBox bottomRow = new HBox(10);
        bottomRow.setAlignment(Pos.CENTER_LEFT);
        bottomRow.setStyle("-fx-padding: 8 0 0 0; -fx-border-color: #E2E8F0; -fx-border-width: 1 0 0 0;");

        Label dateLabel = new Label("ðŸ• " + (req.getDateTime() != null ? req.getDateTime() : ""));
        dateLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #A3AED0;");

        String statusText = req.getStatus();
        if (req.getHelperCount() > 0 && "Pending".equals(statusText)) {
            statusText = "Help Received";
        }
        Label statusBadge = new Label(statusText);
        statusBadge.getStyleClass().addAll("badge", "status-" + statusText.toLowerCase().replace(" ", "-"));

        HBox badgesBox = new HBox(8);
        badgesBox.setAlignment(Pos.CENTER_LEFT);
        badgesBox.getChildren().add(statusBadge);

        if (req.getHelperCount() > 0) {
            Label helperBadge = new Label("ðŸ‘¥ " + req.getHelperCount() + " Helper" + (req.getHelperCount() > 1 ? "s" : ""));
            helperBadge.setStyle("-fx-background-color: rgba(67, 185, 127, 0.15); -fx-text-fill: #2E7D32; " +
                    "-fx-padding: 3 8; -fx-background-radius: 10; -fx-font-size: 11px; -fx-font-weight: bold;");
            badgesBox.getChildren().add(helperBadge);
        }

        Region bottomSpacer = new Region();
        HBox.setHgrow(bottomSpacer, Priority.ALWAYS);

        // Current user info
        User currentUser = SessionManager.getInstance().getCurrentUser();
        boolean isRequester = currentUser != null && currentUser.getId() == req.getUserId();

        // Like button
        boolean hasLiked = currentUser != null && requestDAO.hasLiked(req.getId(), currentUser.getId());
        Button likeBtn = new Button((hasLiked ? "â¤ï¸" : "ðŸ¤") + " " + req.getLikeCount());
        likeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " +
            (hasLiked ? "#E53935" : "#A3AED0") + "; -fx-cursor: hand; -fx-font-size: 12px; " +
            "-fx-padding: 4 8; -fx-background-radius: 6;");

        likeBtn.setOnAction(e -> {
            if (currentUser == null) return;
            boolean nowLiked = requestDAO.toggleLike(req.getId(), currentUser.getId());
            HelpRequest updated = requestDAO.getRequestById(req.getId());
            if (updated != null) {
                likeBtn.setText((nowLiked ? "â¤ï¸" : "ðŸ¤") + " " + updated.getLikeCount());
                likeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " +
                    (nowLiked ? "#E53935" : "#A3AED0") + "; -fx-cursor: hand; -fx-font-size: 12px; " +
                    "-fx-padding: 4 8; -fx-background-radius: 6;");
            }
        });

        // â”€â”€ Help Done Button (for accepted helpers who are NOT the requester) â”€â”€
        HBox actionButtons = new HBox(8);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        if (!isRequester && currentUser != null && !"Completed".equals(req.getStatus())) {
            Response myResponse = responseDAO.getResponse(req.getId(), currentUser.getId());
            if (myResponse != null) {
                if ("Accepted".equals(myResponse.getStatus())) {
                    // Show active Help Done button
                    Button helpDoneBtn = new Button("âœ…  Help Done");
                    helpDoneBtn.getStyleClass().add("btn-help-done");
                    helpDoneBtn.setOnAction(e -> {
                        responseDAO.updateStatus(myResponse.getId(), "Completed");
                        requestDAO.updateStatus(req.getId(), "Completed");
                        helpDoneBtn.setText("ðŸŽ‰  Done!");
                        helpDoneBtn.getStyleClass().clear();
                        helpDoneBtn.getStyleClass().add("btn-help-done-done");
                        helpDoneBtn.setDisable(true);
                        // Notify all other users in feed
                        String helperName = currentUser.getName() != null ? currentUser.getName() : "A helper";
                        showNotification(helperName + " marked the help as done for \"" 
                                + req.getType() + " request\" by " + req.getUserName() 
                                + ". This request is now resolved! ðŸŽ‰");
                        loadFeed(activeFilter);
                    });
                    actionButtons.getChildren().add(helpDoneBtn);
                } else if ("Completed".equals(myResponse.getStatus())) {
                    // Show completed badge (disabled)
                    Button doneLabel = new Button("ðŸŽ‰  Help Done");
                    doneLabel.getStyleClass().add("btn-help-done-done");
                    doneLabel.setDisable(true);
                    actionButtons.getChildren().add(doneLabel);
                } else if ("Pending".equals(myResponse.getStatus())) {
                    // Offered but not yet accepted â€” show awaiting badge
                    Label awaitingLabel = new Label("â³ Awaiting Acceptance");
                    awaitingLabel.setStyle("-fx-background-color: rgba(255,193,7,0.12); -fx-text-fill: #B8860B; " +
                            "-fx-padding: 5 12; -fx-background-radius: 8; -fx-font-size: 11px; -fx-font-weight: bold;");
                    actionButtons.getChildren().add(awaitingLabel);
                }
            }
        }

        // Show completed ribbon if request is done
        if ("Completed".equals(req.getStatus())) {
            Label completedRibbon = new Label("ðŸŽ‰ Completed");
            completedRibbon.setStyle("-fx-background-color: rgba(0, 200, 83, 0.12); -fx-text-fill: #2E7D32; " +
                    "-fx-padding: 4 12; -fx-background-radius: 8; -fx-font-size: 11px; -fx-font-weight: bold; " +
                    "-fx-border-color: rgba(0,200,83,0.3); -fx-border-radius: 8; -fx-border-width: 1;");
            actionButtons.getChildren().add(0, completedRibbon);
        }

        // View details button
        Button viewBtn = new Button("View Details â†’");
        viewBtn.getStyleClass().add("btn-primary");
        viewBtn.setStyle("-fx-font-size: 11px; -fx-padding: 6 14;");
        viewBtn.setOnAction(e -> SceneManager.showRequestDetail(req));
        actionButtons.getChildren().add(viewBtn);

        bottomRow.getChildren().addAll(dateLabel, badgesBox, bottomSpacer, likeBtn, actionButtons);

        content.getChildren().addAll(topRow, descLabel);
        if (!skillsRow.getChildren().isEmpty()) content.getChildren().add(skillsRow);
        content.getChildren().add(bottomRow);

        card.getChildren().addAll(accentBar, content);
        return card;
    }

    private StackPane createAvatar(String name, int userId) {
        StackPane pane = new StackPane();
        pane.setMinSize(40, 40);
        pane.setMaxSize(40, 40);
        String color = AVATAR_COLORS[Math.abs(userId) % AVATAR_COLORS.length];
        pane.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 50;");

        String initials = "?";
        if (name != null && !name.isEmpty()) {
            String[] parts = name.trim().split("\\s+");
            if (parts.length >= 2) initials = String.valueOf(parts[0].charAt(0)).toUpperCase() + String.valueOf(parts[1].charAt(0)).toUpperCase();
            else initials = String.valueOf(parts[0].charAt(0)).toUpperCase();
        }
        Label lbl = new Label(initials);
        lbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");
        pane.getChildren().add(lbl);
        return pane;
    }

    private void addEmptyState() {
        VBox empty = new VBox(12);
        empty.setAlignment(Pos.CENTER);
        empty.setPadding(new Insets(80, 0, 0, 0));
        Label icon = new Label("ðŸ•Šï¸");
        icon.setStyle("-fx-font-size: 52px;");
        Label text = new Label("No requests found");
        text.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2B3674;");
        Label sub = new Label("Be the first to post a help request!");
        sub.setStyle("-fx-font-size: 13px; -fx-text-fill: #A3AED0;");
        empty.getChildren().addAll(icon, text, sub);
        feedContainer.getChildren().add(empty);
    }
}


