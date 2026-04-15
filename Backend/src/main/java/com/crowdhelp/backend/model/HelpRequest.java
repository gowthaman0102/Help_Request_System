package com.crowdhelp.backend.model;

public class HelpRequest {
    private int id;
    private int userId;
    private String userName; // denormalized for display
    private String userLocation;
    private String type; // Blood, Notes, Transport, Emergency
    private String description;
    private String location;
    private String requiredSkills;
    private String urgency; // Low, Medium, High, Critical
    private String dateTime;
    private String status; // Pending, Accepted, In Progress, Completed
    private int likeCount;
    private int helperCount;

    public HelpRequest() {}

    public HelpRequest(int userId, String type, String description, String location,
                       String requiredSkills, String urgency, String dateTime) {
        this.userId = userId;
        this.type = type;
        this.description = description;
        this.location = location;
        this.requiredSkills = requiredSkills;
        this.urgency = urgency;
        this.dateTime = dateTime;
        this.status = "Pending";
        this.likeCount = 0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getUserLocation() { return userLocation; }
    public void setUserLocation(String userLocation) { this.userLocation = userLocation; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getHelperCount() { return helperCount; }
    public void setHelperCount(int helperCount) { this.helperCount = helperCount; }

    public String getTypeIcon() {
        return switch (type) {
            case "Blood" -> "ðŸ©¸";
            case "Notes" -> "ðŸ“š";
            case "Transport" -> "ðŸš—";
            case "Emergency" -> "ðŸš¨";
            case "Food" -> "ðŸ±";
            case "Medical" -> "ðŸ¥";
            default -> "ðŸ¤";
        };
    }

    public String getUrgencyColor() {
        return switch (urgency) {
            case "Emergency", "Critical" -> "#FF4444";
            case "Urgent", "High" -> "#FF8C00";
            case "Normal", "Medium", "Low" -> "#4CAF50";
            default -> "#4CAF50";
        };
    }

    public String getUrgencyBgColor() {
        return switch (urgency) {
            case "Emergency", "Critical" -> "rgba(244,67,54,0.15)";
            case "Urgent", "High" -> "rgba(255,152,0,0.15)";
            case "Medium" -> "rgba(255,235,59,0.3)";
            case "Normal", "Low" -> "rgba(76,175,80,0.15)";
            default -> "rgba(76,175,80,0.15)";
        };
    }

    public String getUrgencyTextColor() {
        return switch (urgency) {
            case "Emergency", "Critical" -> "#E53935";
            case "Urgent", "High" -> "#F57C00";
            case "Medium" -> "#FBC02D";
            case "Normal", "Low" -> "#43A047";
            default -> "#43A047";
        };
    }
}

