package com.crowdhelp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String APP_DIR = "C:" + java.io.File.separator + "Help_1" + java.io.File.separator + "DataBase";
    private static final String DB_URL = "jdbc:sqlite:" + APP_DIR + java.io.File.separator + "crowdhelp.db";
    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            java.io.File dir = new java.io.File(APP_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            connection = DriverManager.getConnection(DB_URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Users table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password_hash TEXT NOT NULL,
                    location TEXT NOT NULL,
                    skills TEXT DEFAULT '',
                    reputation_score REAL DEFAULT 0.0,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Add new columns to existing tables (migrate)
            try { stmt.execute("ALTER TABLE users ADD COLUMN reputation_score REAL DEFAULT 0.0"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE requests ADD COLUMN location TEXT NOT NULL DEFAULT ''"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE requests ADD COLUMN required_skills TEXT DEFAULT ''"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE requests ADD COLUMN urgency TEXT NOT NULL DEFAULT 'Medium'"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE requests ADD COLUMN date_time TEXT NOT NULL DEFAULT ''"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE requests ADD COLUMN status TEXT DEFAULT 'Pending'"); } catch (SQLException ignored) {}
            try { stmt.execute("ALTER TABLE requests ADD COLUMN like_count INTEGER DEFAULT 0"); } catch (SQLException ignored) {}

            // Requests table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS requests (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    type TEXT NOT NULL,
                    description TEXT NOT NULL,
                    location TEXT NOT NULL,
                    required_skills TEXT DEFAULT '',
                    urgency TEXT NOT NULL,
                    date_time TEXT NOT NULL,
                    status TEXT DEFAULT 'Pending',
                    like_count INTEGER DEFAULT 0,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            // Responses table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS responses (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    request_id INTEGER NOT NULL,
                    helper_id INTEGER NOT NULL,
                    status TEXT DEFAULT 'Pending',
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (request_id) REFERENCES requests(id),
                    FOREIGN KEY (helper_id) REFERENCES users(id)
                )
            """);

            // Messages table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS messages (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    request_id INTEGER NOT NULL,
                    sender_id INTEGER NOT NULL,
                    receiver_id INTEGER NOT NULL,
                    content TEXT NOT NULL,
                    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (request_id) REFERENCES requests(id),
                    FOREIGN KEY (sender_id) REFERENCES users(id),
                    FOREIGN KEY (receiver_id) REFERENCES users(id)
                )
            """);

            // Ratings table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS ratings (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    request_id INTEGER NOT NULL,
                    rater_id INTEGER NOT NULL,
                    ratee_id INTEGER NOT NULL,
                    score INTEGER NOT NULL,
                    comment TEXT DEFAULT '',
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (request_id) REFERENCES requests(id),
                    FOREIGN KEY (rater_id) REFERENCES users(id),
                    FOREIGN KEY (ratee_id) REFERENCES users(id)
                )
            """);

            // Likes table
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS likes (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    request_id INTEGER NOT NULL,
                    user_id INTEGER NOT NULL,
                    UNIQUE(request_id, user_id),
                    FOREIGN KEY (request_id) REFERENCES requests(id),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}
