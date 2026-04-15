package com.crowdhelp.backend.dao;

import com.crowdhelp.backend.model.Message;
import com.crowdhelp.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    public Message sendMessage(Message message) {
        String sql = "INSERT INTO messages (request_id, sender_id, receiver_id, content) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, message.getRequestId());
            pstmt.setInt(2, message.getSenderId());
            pstmt.setInt(3, message.getReceiverId());
            pstmt.setString(4, message.getContent());
            pstmt.executeUpdate();
            ResultSet keys = pstmt.getGeneratedKeys();
            if (keys.next()) message.setId(keys.getInt(1));
            return message;
        } catch (SQLException e) {
            System.err.println("Error sending message: " + e.getMessage());
            return null;
        }
    }

    public List<Message> getMessagesBetweenUsers(int requestId, int user1Id, int user2Id) {
        List<Message> list = new ArrayList<>();
        String sql = """
            SELECT m.*, u.name AS sender_name FROM messages m
            JOIN users u ON m.sender_id = u.id
            WHERE m.request_id = ?
              AND ((m.sender_id = ? AND m.receiver_id = ?)
                OR (m.sender_id = ? AND m.receiver_id = ?))
            ORDER BY m.timestamp ASC
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, requestId);
            pstmt.setInt(2, user1Id);
            pstmt.setInt(3, user2Id);
            pstmt.setInt(4, user2Id);
            pstmt.setInt(5, user1Id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Message m = new Message();
                m.setId(rs.getInt("id"));
                m.setRequestId(rs.getInt("request_id"));
                m.setSenderId(rs.getInt("sender_id"));
                m.setReceiverId(rs.getInt("receiver_id"));
                m.setSenderName(rs.getString("sender_name"));
                m.setContent(rs.getString("content"));
                m.setTimestamp(rs.getString("timestamp"));
                list.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error getting messages: " + e.getMessage());
        }
        return list;
    }
}

