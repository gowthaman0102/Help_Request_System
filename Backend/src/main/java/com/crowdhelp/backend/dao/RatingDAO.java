package com.crowdhelp.backend.dao;

import com.crowdhelp.backend.model.Rating;
import com.crowdhelp.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RatingDAO {

    public boolean hasRated(int requestId, int raterId) {
        String sql = "SELECT COUNT(*) FROM ratings WHERE request_id = ? AND rater_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            pstmt.setInt(2, raterId);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public Rating createRating(Rating rating) {
        String sql = "INSERT INTO ratings (request_id, rater_id, ratee_id, score, comment) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, rating.getRequestId());
            pstmt.setInt(2, rating.getRaterId());
            pstmt.setInt(3, rating.getRateeId());
            pstmt.setInt(4, rating.getScore());
            pstmt.setString(5, rating.getComment());
            pstmt.executeUpdate();
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) rating.setId(keys.getInt(1));
            return rating;
        } catch (SQLException e) {
            System.err.println("Error creating rating: " + e.getMessage());
            return null;
        }
    }

    public double getAverageRating(int userId) {
        String sql = "SELECT AVG(score) FROM ratings WHERE ratee_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.getDouble(1);
        } catch (SQLException e) {
            return 0.0;
        }
    }

    public List<Rating> getRatingsForUser(int userId) {
        List<Rating> list = new ArrayList<>();
        String sql = """
            SELECT r.*, u.name AS rater_name FROM ratings r
            JOIN users u ON r.rater_id = u.id
            WHERE r.ratee_id = ?
            ORDER BY r.created_at DESC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Rating r = new Rating();
                r.setId(rs.getInt("id"));
                r.setRequestId(rs.getInt("request_id"));
                r.setRaterId(rs.getInt("rater_id"));
                r.setRateeId(rs.getInt("ratee_id"));
                r.setScore(rs.getInt("score"));
                r.setComment(rs.getString("comment"));
                r.setCreatedAt(rs.getString("created_at"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error getting ratings: " + e.getMessage());
        }
        return list;
    }
}

