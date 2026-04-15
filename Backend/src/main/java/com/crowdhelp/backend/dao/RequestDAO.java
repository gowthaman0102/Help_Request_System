package com.crowdhelp.backend.dao;

import com.crowdhelp.backend.model.HelpRequest;
import com.crowdhelp.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

    public HelpRequest createRequest(HelpRequest request) {
        String sql = "INSERT INTO requests (user_id, type, description, location, required_skills, urgency, date_time, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, request.getUserId());
            pstmt.setString(2, request.getType());
            pstmt.setString(3, request.getDescription());
            pstmt.setString(4, request.getLocation());
            pstmt.setString(5, request.getRequiredSkills());
            pstmt.setString(6, request.getUrgency());
            pstmt.setString(7, request.getDateTime());
            pstmt.setString(8, "Pending");
            pstmt.executeUpdate();
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) request.setId(keys.getInt(1));
            return request;
        } catch (SQLException e) {
            System.err.println("Error creating request: " + e.getMessage());
            return null;
        }
    }

    public List<HelpRequest> getAllRequests() {
        List<HelpRequest> list = new ArrayList<>();
        String sql = """
            SELECT r.*, u.name AS user_name, u.location AS user_location,
                   (SELECT COUNT(*) FROM responses WHERE request_id = r.id AND status != 'Rejected') AS helper_count
            FROM requests r
            JOIN users u ON r.user_id = u.id
            ORDER BY CASE r.urgency WHEN 'Emergency' THEN 1 WHEN 'Critical' THEN 1 WHEN 'Urgent' THEN 2 WHEN 'High' THEN 2 WHEN 'Normal' THEN 3 WHEN 'Medium' THEN 3 WHEN 'Low' THEN 3 ELSE 4 END ASC, r.created_at DESC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(mapRequest(rs));
        } catch (SQLException e) {
            System.err.println("Error getting all requests: " + e.getMessage());
        }
        return list;
    }

    public List<HelpRequest> getRequestsByType(String type) {
        List<HelpRequest> list = new ArrayList<>();
        String sql = """
            SELECT r.*, u.name AS user_name, u.location AS user_location,
                   (SELECT COUNT(*) FROM responses WHERE request_id = r.id AND status != 'Rejected') AS helper_count
            FROM requests r
            JOIN users u ON r.user_id = u.id
            WHERE r.type = ?
            ORDER BY CASE r.urgency WHEN 'Emergency' THEN 1 WHEN 'Critical' THEN 1 WHEN 'Urgent' THEN 2 WHEN 'High' THEN 2 WHEN 'Normal' THEN 3 WHEN 'Medium' THEN 3 WHEN 'Low' THEN 3 ELSE 4 END ASC, r.created_at DESC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, type);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapRequest(rs));
        } catch (SQLException e) {
            System.err.println("Error getting requests by type: " + e.getMessage());
        }
        return list;
    }

    public List<HelpRequest> getRequestsByUser(int userId) {
        List<HelpRequest> list = new ArrayList<>();
        String sql = """
            SELECT r.*, u.name AS user_name, u.location AS user_location,
                   (SELECT COUNT(*) FROM responses WHERE request_id = r.id AND status != 'Rejected') AS helper_count
            FROM requests r
            JOIN users u ON r.user_id = u.id
            WHERE r.user_id = ?
            ORDER BY CASE r.urgency WHEN 'Emergency' THEN 1 WHEN 'Critical' THEN 1 WHEN 'Urgent' THEN 2 WHEN 'High' THEN 2 WHEN 'Normal' THEN 3 WHEN 'Medium' THEN 3 WHEN 'Low' THEN 3 ELSE 4 END ASC, r.created_at DESC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) list.add(mapRequest(rs));
        } catch (SQLException e) {
            System.err.println("Error getting user requests: " + e.getMessage());
        }
        return list;
    }

    public HelpRequest getRequestById(int id) {
        String sql = """
            SELECT r.*, u.name AS user_name, u.location AS user_location,
                   (SELECT COUNT(*) FROM responses WHERE request_id = r.id AND status != 'Rejected') AS helper_count
            FROM requests r
            JOIN users u ON r.user_id = u.id
            WHERE r.id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return mapRequest(rs);
        } catch (SQLException e) {
            System.err.println("Error getting request by id: " + e.getMessage());
        }
        return null;
    }

    public void updateStatus(int requestId, String newStatus) {
        String sql = "UPDATE requests SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, requestId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating request status: " + e.getMessage());
        }
    }

    public boolean toggleLike(int requestId, int userId) {
        // Check if already liked
        String checkSql = "SELECT COUNT(*) FROM likes WHERE request_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, requestId);
            checkStmt.setInt(2, userId);
            ResultSet rs = checkStmt.executeQuery();
            boolean alreadyLiked = rs.getInt(1) > 0;
            if (alreadyLiked) {
                conn.prepareStatement("DELETE FROM likes WHERE request_id = " + requestId + " AND user_id = " + userId).executeUpdate();
                conn.prepareStatement("UPDATE requests SET like_count = like_count - 1 WHERE id = " + requestId).executeUpdate();
                return false;
            } else {
                conn.prepareStatement("INSERT INTO likes (request_id, user_id) VALUES (" + requestId + ", " + userId + ")").executeUpdate();
                conn.prepareStatement("UPDATE requests SET like_count = like_count + 1 WHERE id = " + requestId).executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Error toggling like: " + e.getMessage());
            return false;
        }
    }

    public boolean hasLiked(int requestId, int userId) {
        String sql = "SELECT COUNT(*) FROM likes WHERE request_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    private HelpRequest mapRequest(ResultSet rs) throws SQLException {
        HelpRequest req = new HelpRequest();
        req.setId(rs.getInt("id"));
        req.setUserId(rs.getInt("user_id"));
        req.setUserName(rs.getString("user_name"));
        req.setUserLocation(rs.getString("user_location"));
        req.setType(rs.getString("type"));
        req.setDescription(rs.getString("description"));
        req.setLocation(rs.getString("location"));
        req.setRequiredSkills(rs.getString("required_skills"));
        req.setUrgency(rs.getString("urgency"));
        req.setDateTime(rs.getString("date_time"));
        req.setStatus(rs.getString("status"));
        req.setLikeCount(rs.getInt("like_count"));
        try {
            req.setHelperCount(rs.getInt("helper_count"));
        } catch (SQLException ignored) {}
        return req;
    }
}

