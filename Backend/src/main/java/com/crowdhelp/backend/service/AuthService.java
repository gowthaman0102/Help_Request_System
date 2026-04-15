package com.crowdhelp.backend.service;

import com.crowdhelp.backend.dao.UserDAO;
import com.crowdhelp.backend.model.User;

public class AuthService {
    private final UserDAO userDAO = new UserDAO();

    public String register(String name, String email, String password, String location, String skills) {
        if (name == null || name.trim().isEmpty()) return "Name is required.";
        if (email == null || !email.contains("@")) return "Valid email is required.";
        if (password == null || password.length() < 6) return "Password must be at least 6 characters.";
        if (location == null || location.trim().isEmpty()) return "Location is required.";
        if (userDAO.emailExists(email.trim().toLowerCase())) return "Email already registered.";

        User user = new User();
        user.setName(name.trim());
        user.setEmail(email.trim().toLowerCase());
        // Simple hash: in production use BCrypt
        user.setPasswordHash(hashPassword(password));
        user.setLocation(location.trim());
        user.setSkills(skills == null ? "" : skills.trim());
        user.setReputationScore(0.0);

        User created = userDAO.createUser(user);
        return created != null ? "SUCCESS" : "Registration failed. Please try again.";
    }

    public User login(String email, String password) {
        if (email == null || password == null) return null;
        User user = userDAO.findByEmail(email.trim().toLowerCase());
        if (user == null) return null;
        if (!checkPassword(password, user.getPasswordHash())) return null;
        return user;
    }

    private String hashPassword(String password) {
        // Simple hash for desktop app demo (not production)
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            return password; // fallback
        }
    }

    private boolean checkPassword(String password, String hash) {
        return hashPassword(password).equals(hash);
    }
}

