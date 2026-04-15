package com.crowdhelp.backend.model;

public class User {
    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private String location;
    private String skills; // comma-separated
    private double reputationScore;
    private String avatarColor; // for UI avatar

    public User() {}

    public User(int id, String name, String email, String passwordHash, String location, String skills, double reputationScore) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.location = location;
        this.skills = skills;
        this.reputationScore = reputationScore;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public double getReputationScore() { return reputationScore; }
    public void setReputationScore(double reputationScore) { this.reputationScore = reputationScore; }

    public String getAvatarColor() { return avatarColor; }
    public void setAvatarColor(String avatarColor) { this.avatarColor = avatarColor; }

    public String getInitials() {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return String.valueOf(parts[0].charAt(0)).toUpperCase() + String.valueOf(parts[1].charAt(0)).toUpperCase();
        }
        return String.valueOf(parts[0].charAt(0)).toUpperCase();
    }

    @Override
    public String toString() {
        return name + " (" + location + ")";
    }
}

