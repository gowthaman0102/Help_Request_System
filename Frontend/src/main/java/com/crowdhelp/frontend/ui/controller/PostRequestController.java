package com.crowdhelp.frontend.ui.controller;

import com.crowdhelp.backend.dao.RequestDAO;
import com.crowdhelp.backend.model.HelpRequest;
import com.crowdhelp.backend.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class PostRequestController {

    @FXML private ComboBox<String> typeCombo, urgencyCombo;
    @FXML private TextArea descArea;
    @FXML private TextField locationField, skillsField, dateTimeField;
    @FXML private Label messageLabel;
    @FXML private Button submitBtn;

    private final RequestDAO requestDAO = new RequestDAO();

    @FXML
    public void initialize() {
        typeCombo.getItems().addAll("Blood", "Notes", "Transport", "Emergency", "Food", "Medical", "Other");
        urgencyCombo.getItems().addAll("Normal", "Urgent", "Emergency");

        // Default date/time
        dateTimeField.setText(LocalDateTime.now().plusHours(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    @FXML
    private void handleSubmit() {
        String type = typeCombo.getValue();
        String desc = descArea.getText().trim();
        String location = locationField.getText().trim();
        String skills = skillsField.getText().trim();
        String urgency = urgencyCombo.getValue();
        String dateTime = dateTimeField.getText().trim();

        if (type == null || type.isEmpty()) { showMsg("Please select a help type.", false); return; }
        if (desc.isEmpty()) { showMsg("Please provide a description.", false); return; }
        if (location.isEmpty()) { showMsg("Please enter a location.", false); return; }
        if (urgency == null || urgency.isEmpty()) { showMsg("Please select urgency level.", false); return; }

        HelpRequest req = new HelpRequest(
            SessionManager.getInstance().getCurrentUser().getId(),
            type, desc, location, skills, urgency, dateTime
        );

        HelpRequest created = requestDAO.createRequest(req);
        if (created != null) {
            showMsg("âœ… Request posted successfully! The community has been notified.", true);
            clearForm();
        } else {
            showMsg("Failed to post request. Please try again.", false);
        }
    }

    private void clearForm() {
        typeCombo.setValue(null);
        urgencyCombo.setValue(null);
        descArea.clear();
        locationField.clear();
        skillsField.clear();
        dateTimeField.setText(LocalDateTime.now().plusHours(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    private void showMsg(String msg, boolean success) {
        messageLabel.setText(msg);
        messageLabel.getStyleClass().removeAll("error-label", "success-label");
        messageLabel.getStyleClass().add(success ? "success-label" : "error-label");
        messageLabel.setVisible(true);
        messageLabel.setManaged(true);
    }
}


