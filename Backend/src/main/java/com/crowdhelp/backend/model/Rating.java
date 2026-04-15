package com.crowdhelp.backend.model;

public class Rating {
    private int id;
    private int requestId;
    private int raterId;
    private int rateeId;
    private int score; // 1-5
    private String comment;
    private String createdAt;

    public Rating() {}

    public Rating(int requestId, int raterId, int rateeId, int score, String comment) {
        this.requestId = requestId;
        this.raterId = raterId;
        this.rateeId = rateeId;
        this.score = score;
        this.comment = comment;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public int getRaterId() { return raterId; }
    public void setRaterId(int raterId) { this.raterId = raterId; }

    public int getRateeId() { return rateeId; }
    public void setRateeId(int rateeId) { this.rateeId = rateeId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getStars() {
        return "â˜…".repeat(score) + "â˜†".repeat(5 - score);
    }
}

