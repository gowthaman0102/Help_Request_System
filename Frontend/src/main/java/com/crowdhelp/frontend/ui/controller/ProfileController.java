package com.crowdhelp.frontend.ui.controller;

import com.crowdhelp.backend.dao.RatingDAO;
import com.crowdhelp.backend.dao.RequestDAO;
import com.crowdhelp.backend.dao.ResponseDAO;
import com.crowdhelp.backend.dao.UserDAO;
import com.crowdhelp.backend.model.HelpRequest;
import com.crowdhelp.backend.model.Rating;
import com.crowdhelp.backend.model.User;
import com.crowdhelp.backend.util.SessionManager;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class ProfileController {

    @FXML private VBox mainContainer;
    @FXML private ScrollPane scrollPane;

    private final UserDAO userDAO = new UserDAO();
    private final RequestDAO requestDAO = new RequestDAO();
    private final ResponseDAO responseDAO = new ResponseDAO();
    private final RatingDAO ratingDAO = new RatingDAO();

    @FXML
    public void initialize() {
        buildProfile();
    }

    private void buildProfile() {
        mainContainer.getChildren().clear();
        mainContainer.setSpacing(20);
        mainContainer.setPadding(new Insets(24));

        User user = SessionManager.getInstance().getCurrentUser();
        if (user == null) return;

        // Live data
        user = userDAO.findById(user.getId());
        SessionManager.getInstance().setCurrentUser(user);

        String[] colors = {"#6C63FF","#FF6584","#43B97F","#F9A825","#00BCD4","#E91E63"};
        String color = colors[Math.abs(user.getId()) % colors.length];

        // â”€â”€ Profile Header â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        HBox profileHeader = new HBox(20);
        profileHeader.setAlignment(Pos.CENTER_LEFT);
        profileHeader.setPadding(new Insets(24));
        profileHeader.setStyle("-fx-background-color: #FFFFFF; " +
                "-fx-border-radius: 14; -fx-background-radius: 14;");

        // Large Avatar
        StackPane bigAvatar = new StackPane();
        bigAvatar.setMinSize(76, 76); bigAvatar.setMaxSize(76, 76);
        bigAvatar.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 50; " +
                "-fx-effect: dropshadow(gaussian, rgba(112,144,176,0.2), 12, 0, 0, 0);");
        Label initLbl = new Label(user.getInitials());
        initLbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 26px;");
        bigAvatar.getChildren().add(initLbl);

        // Stats
        List<HelpRequest> myReqs = requestDAO.getRequestsByUser(user.getId());
        long completed = myReqs.stream().filter(r -> "Completed".equals(r.getStatus())).count();
        double avgRating = ratingDAO.getAverageRating(user.getId());
        List<Rating> ratings = ratingDAO.getRatingsForUser(user.getId());

        VBox info = new VBox(6);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label nameLbl = new Label(user.getName());
        nameLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2B3674;");
        Label emailLbl = new Label(user.getEmail());
        emailLbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #707EAE;");
        Label locLbl = new Label("ðŸ“ " + user.getLocation());
        locLbl.setStyle("-fx-font-size: 13px; -fx-text-fill: #707EAE;");
        HBox starsHBox = buildStarDisplay(avgRating);
        Label ratingCount = new Label(String.format("(%.1f / 5.0 from %d review%s)", avgRating, ratings.size(), ratings.size() != 1 ? "s" : ""));
        ratingCount.setStyle("-fx-font-size: 12px; -fx-text-fill: #888DA0;");
        HBox ratingRow = new HBox(8, starsHBox, ratingCount);
        ratingRow.setAlignment(Pos.CENTER_LEFT);
        info.getChildren().addAll(nameLbl, emailLbl, locLbl, ratingRow);

        // Stat tiles
        VBox statTiles = new VBox(10);
        statTiles.setAlignment(Pos.CENTER);
        statTiles.getChildren().addAll(
            buildStatTile("ðŸ“‹", String.valueOf(myReqs.size()), "Requests"),
            buildStatTile("âœ…", String.valueOf(completed), "Completed"),
            buildStatTile("â­", String.format("%.1f", avgRating), "Avg Rating")
        );

        profileHeader.getChildren().addAll(bigAvatar, info, statTiles);
        mainContainer.getChildren().add(profileHeader);

        // â”€â”€ Skills Section â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        VBox skillsBox = buildSectionCard("ðŸ› ï¸ Skills");
        FlowPane skillsChips = new FlowPane(8, 8);
        skillsChips.setAlignment(Pos.CENTER_LEFT);
        if (user.getSkills() != null && !user.getSkills().trim().isEmpty()) {
            for (String skill : user.getSkills().split(",")) {
                Label chip = new Label(skill.trim());
                chip.setStyle("-fx-background-color: rgba(67,24,255,0.08); -fx-text-fill: #4318FF; " +
                        "-fx-padding: 5 14; -fx-background-radius: 20; -fx-font-size: 12px; " +
                        "-fx-border-color: rgba(67,24,255,0.2); -fx-border-radius: 20; -fx-border-width: 1;");
                skillsChips.getChildren().add(chip);
            }
        } else {
            Label none = new Label("No skills listed yet.");
            none.setStyle("-fx-text-fill: #555870; -fx-font-size: 13px;");
            skillsChips.getChildren().add(none);
        }
        skillsBox.getChildren().add(skillsChips);
        mainContainer.getChildren().add(skillsBox);

        // â”€â”€ My Recent Requests â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        VBox reqsBox = buildSectionCard("ðŸ“‹ Recent Requests");
        if (myReqs.isEmpty()) {
            Label none = new Label("No requests posted yet.");
            none.setStyle("-fx-text-fill: #707EAE; -fx-font-size: 13px;");
            reqsBox.getChildren().add(none);
        } else {
            for (HelpRequest req : myReqs.subList(0, Math.min(3, myReqs.size()))) {
                HBox row = new HBox(12);
                row.setAlignment(Pos.CENTER_LEFT);
                row.setPadding(new Insets(8));
                row.setStyle("-fx-background-color: #F4F7FE; -fx-border-radius: 8; -fx-background-radius: 8;");
                Label typeIco = new Label(req.getTypeIcon()); typeIco.setStyle("-fx-font-size: 18px;");
                VBox ri = new VBox(2);
                HBox.setHgrow(ri, Priority.ALWAYS);
                Label rt = new Label(req.getType() + " â€” " + req.getLocation());
                rt.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2B3674;");
                Label rd = new Label(req.getDateTime());
                rd.setStyle("-fx-font-size: 11px; -fx-text-fill: #707EAE;");
                ri.getChildren().addAll(rt, rd);
                Label rs = new Label(req.getStatus());
                rs.setStyle("-fx-font-size: 11px; -fx-padding: 3 8; -fx-background-radius: 10; " +
                    "-fx-background-color: rgba(67,24,255,0.08); -fx-text-fill: #4318FF;");
                row.getChildren().addAll(typeIco, ri, rs);
                reqsBox.getChildren().add(row);
            }
        }
        mainContainer.getChildren().add(reqsBox);

        // â”€â”€ Ratings Received â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
        if (!ratings.isEmpty()) {
            VBox ratingsBox = buildSectionCard("â­ Ratings Received");
            for (Rating r : ratings.subList(0, Math.min(4, ratings.size()))) {
                VBox ratingCard = new VBox(4);
                ratingCard.setPadding(new Insets(10));
                ratingCard.setStyle("-fx-background-color: #F4F7FE; -fx-border-radius: 8; -fx-background-radius: 8;");
                HBox starRow = buildStarDisplay(r.getScore());
                Label comment = new Label(r.getComment() != null && !r.getComment().isEmpty() ? "\"" + r.getComment() + "\"" : "(No comment)");
                comment.setStyle("-fx-font-size: 12px; -fx-text-fill: #707EAE; -fx-font-style: italic;");
                Label date = new Label(r.getCreatedAt() != null ? r.getCreatedAt().substring(0, 10) : "");
                date.setStyle("-fx-font-size: 11px; -fx-text-fill: #707EAE;");
                ratingCard.getChildren().addAll(starRow, comment, date);
                ratingsBox.getChildren().add(ratingCard);
            }
            mainContainer.getChildren().add(ratingsBox);
        }
    }

    private HBox buildStarDisplay(double score) {
        HBox row = new HBox(2);
        row.setAlignment(Pos.CENTER_LEFT);
        for (int i = 1; i <= 5; i++) {
            Label star = new Label(i <= score ? "â˜…" : "â˜†");
            star.setStyle("-fx-font-size: 16px; -fx-text-fill: " + (i <= score ? "#FFD700" : "#E2E8F0") + ";");
            row.getChildren().add(star);
        }
        return row;
    }

    private VBox buildSectionCard(String title) {
        VBox box = new VBox(12);
        box.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E2E8F0; " +
                "-fx-border-radius: 12; -fx-background-radius: 12; -fx-border-width: 1; -fx-padding: 18;");
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #2B3674;");
        box.getChildren().add(titleLbl);
        return box;
    }

    private VBox buildStatTile(String icon, String value, String label) {
        VBox tile = new VBox(4);
        tile.setAlignment(Pos.CENTER);
        tile.setPadding(new Insets(10 ,16, 10, 16));
        tile.setStyle("-fx-background-color: rgba(67,24,255,0.05); -fx-border-color: rgba(67,24,255,0.15); " +
                "-fx-border-radius: 10; -fx-background-radius: 10; -fx-border-width: 1;");
        Label iconLbl = new Label(icon); iconLbl.setStyle("-fx-font-size: 18px;");
        Label valLbl = new Label(value); valLbl.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2B3674;");
        Label lblLbl = new Label(label); lblLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #707EAE;");
        tile.getChildren().addAll(iconLbl, valLbl, lblLbl);
        return tile;
    }
}


