package com.crowdhelp.backend.model;

public class Response {
    private int id;
    private int requestId;
    private int helperId;
    private String helperName;
    private String helperLocation;
    private String helperSkills;
    private double helperRating;
    private String status; // Pending, Accepted, Rejected
    private String createdAt;

    public Response() {}

    public Response(int requestId, int helperId) {
        this.requestId = requestId;
        this.helperId = helperId;
        this.status = "Pending";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getHelperId() { return helperId; }
    public void setHelperId(int helperId) { this.helperId = helperId; }

    public String getHelperName() { return helperName; }
    public void setHelperName(String helperName) { this.helperName = helperName; }

    public String getHelperLocation() { return helperLocation; }
    public void setHelperLocation(String helperLocation) { this.helperLocation = helperLocation; }

    public String getHelperSkills() { return helperSkills; }
    public void setHelperSkills(String helperSkills) { this.helperSkills = helperSkills; }

    public double getHelperRating() { return helperRating; }
    public void setHelperRating(double helperRating) { this.helperRating = helperRating; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}

