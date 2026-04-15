package com.crowdhelp.backend.dao;

import com.crowdhelp.backend.model.Response;
import com.crowdhelp.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResponseDAO {

    public Response createResponse(Response response) {
        String sql = "INSERT INTO responses (request_id, helper_id, status) VALUES (?, ?, 'Pending')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, response.getRequestId());
            pstmt.setInt(2, response.getHelperId());
            pstmt.executeUpdate();
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) response.setId(keys.getInt(1));
            return response;
        } catch (SQLException e) {
            System.err.println("Error creating response: " + e.getMessage());
            return null;
        }
    }

    public boolean hasResponded(int requestId, int helperId) {
        String sql = "SELECT COUNT(*) FROM responses WHERE request_id = ? AND helper_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            pstmt.setInt(2, helperId);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    public Response getResponse(int requestId, int helperId) {
        String sql = """
            SELECT res.*, u.name AS helper_name, u.location AS helper_location,
                   u.skills AS helper_skills, u.reputation_score AS helper_rating
            FROM responses res
            JOIN users u ON res.helper_id = u.id
            WHERE res.request_id = ? AND res.helper_id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            pstmt.setInt(2, helperId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Response r = new Response();
                r.setId(rs.getInt("id"));
                r.setRequestId(rs.getInt("request_id"));
                r.setHelperId(rs.getInt("helper_id"));
                r.setHelperName(rs.getString("helper_name"));
                r.setHelperLocation(rs.getString("helper_location"));
                r.setHelperSkills(rs.getString("helper_skills"));
                r.setHelperRating(rs.getDouble("helper_rating"));
                r.setStatus(rs.getString("status"));
                r.setCreatedAt(rs.getString("created_at"));
                return r;
            }
        } catch (SQLException e) {
            System.err.println("Error getting response: " + e.getMessage());
        }
        return null;
    }

    public List<Response> getResponsesForRequest(int requestId) {
        List<Response> list = new ArrayList<>();
        String sql = """
            SELECT res.*, u.name AS helper_name, u.location AS helper_location,
                   u.skills AS helper_skills, u.reputation_score AS helper_rating
            FROM responses res
            JOIN users u ON res.helper_id = u.id
            WHERE res.request_id = ?
            ORDER BY res.created_at DESC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Response r = new Response();
                r.setId(rs.getInt("id"));
                r.setRequestId(rs.getInt("request_id"));
                r.setHelperId(rs.getInt("helper_id"));
                r.setHelperName(rs.getString("helper_name"));
                r.setHelperLocation(rs.getString("helper_location"));
                r.setHelperSkills(rs.getString("helper_skills"));
                r.setHelperRating(rs.getDouble("helper_rating"));
                r.setStatus(rs.getString("status"));
                r.setCreatedAt(rs.getString("created_at"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error getting responses: " + e.getMessage());
        }
        return list;
    }

    public void updateStatus(int responseId, String newStatus) {
        String sql = "UPDATE responses SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, responseId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating response status: " + e.getMessage());
        }
    }

    public List<Response> getResponsesByHelper(int helperId) {
        List<Response> list = new ArrayList<>();
        String sql = "SELECT * FROM responses WHERE helper_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, helperId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Response r = new Response();
                r.setId(rs.getInt("id"));
                r.setRequestId(rs.getInt("request_id"));
                r.setHelperId(rs.getInt("helper_id"));
                r.setStatus(rs.getString("status"));
                r.setCreatedAt(rs.getString("created_at"));
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error getting helper responses: " + e.getMessage());
        }
        return list;
    }
}

