package com.crowdhelp.backend.util;

import com.crowdhelp.backend.dao.RequestDAO;
import com.crowdhelp.backend.dao.UserDAO;
import com.crowdhelp.backend.model.HelpRequest;
import com.crowdhelp.backend.model.User;

public class SampleDataSeeder {
    public static void seed() {
        UserDAO userDAO = new UserDAO();
        RequestDAO requestDAO = new RequestDAO();

        // Only seed if no requests exist
        if (!requestDAO.getAllRequests().isEmpty()) return;

        // Create sample users
        String[][] users = {
            {"Gowshick", "gowshick@example.com", "Chennai", "Blood Donation, First Aid, Medical"},
            {"Ilamaran", "maran@example.com", "Chennai", "Driving, Transport, Logistics"},
            {"Hari Prakash", "haru@example.com", "Coimbatore", "Teaching, Notes, Tutoring"},
            {"Jaisurya", "jai@example.com", "Chennai", "Emergency Response, CPR, Medical"},
            {"Naveen", "naveen@example.com", "Madurai", "Food, Cooking, Blood Donation"},
            {"Pranav", "pranav@example.com", "Chennai", "Transport, Driving, Emergency"}
        };

        java.security.MessageDigest md;
        try {
            md = java.security.MessageDigest.getInstance("SHA-256");
        } catch (Exception e) { return; }

        int[] userIds = new int[users.length];
        double[] reputations = {4.8, 4.2, 4.6, 4.9, 3.8, 4.1};

        for (int i = 0; i < users.length; i++) {
            String[] u = users[i];
            
            User existing = userDAO.findByEmail(u[1].toLowerCase());
            if (existing != null) {
                userIds[i] = existing.getId();
            } else {
                byte[] hash = md.digest("password123".getBytes(java.nio.charset.StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                for (byte b : hash) sb.append(String.format("%02x", b));

                User user = new User();
                user.setName(u[0]);
                user.setEmail(u[1].toLowerCase());
                user.setPasswordHash(sb.toString());
                user.setLocation(u[2]);
                user.setSkills(u[3]);
                user.setReputationScore(reputations[i]);
                User created = userDAO.createUser(user);
                if (created != null) {
                    userIds[i] = created.getId();
                    userDAO.updateReputationScore(created.getId(), reputations[i]);
                }
            }
        }

        // Create sample requests
        if (userIds[0] > 0) {
            HelpRequest r1 = new HelpRequest(userIds[0], "Blood", "Urgently need O+ blood donors for my father's surgery at Apollo Hospital. Any donors please contact immediately.", "Chennai", "Blood Donation", "Emergency", "2026-03-20 08:00");
            requestDAO.createRequest(r1);

            HelpRequest r2 = new HelpRequest(userIds[2], "Notes", "Need lecture notes for Data Structures and Algorithms (Unit 3 & 4). Exam is next week and I missed classes due to illness.", "Coimbatore", "Teaching, Notes", "Urgent", "2026-03-21 10:00");
            requestDAO.createRequest(r2);

            HelpRequest r3 = new HelpRequest(userIds[3], "Emergency", "Senior citizen fell near T.Nagar. Need immediate assistance and transport to a hospital. Please help!", "Chennai", "Emergency Response, First Aid", "Emergency", "2026-03-19 15:30");
            requestDAO.createRequest(r3);

            HelpRequest r4 = new HelpRequest(userIds[1], "Transport", "Need a ride from Tambaram to Airport tomorrow morning at 5 AM. Can share fuel cost.", "Chennai", "Driving", "Normal", "2026-03-20 05:00");
            requestDAO.createRequest(r4);

            HelpRequest r5 = new HelpRequest(userIds[4], "Food", "Distributing free meals to flood victims in Madurai. Need volunteers to help with distribution.", "Madurai", "Food, Cooking", "Urgent", "2026-03-22 12:00");
            requestDAO.createRequest(r5);

            HelpRequest r6 = new HelpRequest(userIds[5], "Medical", "Looking for a nurse or doctor who can do a home visit for an elderly patient in Adyar.", "Chennai", "Medical, Nursing", "Urgent", "2026-03-21 09:00");
            requestDAO.createRequest(r6);
        }

        System.out.println("Sample data seeded successfully.");
    }
}


