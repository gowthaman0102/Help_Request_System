package com.crowdhelp.backend.service;

import com.crowdhelp.backend.dao.UserDAO;
import com.crowdhelp.backend.model.HelpRequest;
import com.crowdhelp.backend.model.User;

import java.util.*;

public class MatchingService {
    private final UserDAO userDAO = new UserDAO();

    public static class HelperScore {
        public User user;
        public double score;
        public String matchReason;

        public HelperScore(User user, double score, String matchReason) {
            this.user = user;
            this.score = score;
            this.matchReason = matchReason;
        }
    }

    /**
     * Finds the best helpers for a given request.
     * Scoring: Location match (+30), Skill match (+20 per skill), Reputation (+reputation*2)
     */
    public List<HelperScore> findBestHelpers(HelpRequest request, int requesterId) {
        List<User> allUsers = userDAO.getAllUsers();
        List<HelperScore> scores = new ArrayList<>();

        for (User user : allUsers) {
            if (user.getId() == requesterId) continue; // skip requester

            double score = 0;
            List<String> reasons = new ArrayList<>();

            // 1. Location match
            if (user.getLocation() != null && request.getLocation() != null
                    && user.getLocation().equalsIgnoreCase(request.getLocation().trim())) {
                score += 30;
                reasons.add("ðŸ“ Same location");
            } else if (user.getLocation() != null && request.getLocation() != null
                    && user.getLocation().toLowerCase().contains(request.getLocation().toLowerCase().split(",")[0].trim())) {
                score += 15;
                reasons.add("ðŸ“ Nearby location");
            }

            // 2. Skill match
            if (request.getRequiredSkills() != null && !request.getRequiredSkills().trim().isEmpty()
                    && user.getSkills() != null && !user.getSkills().trim().isEmpty()) {
                String[] reqSkills = request.getRequiredSkills().toLowerCase().split(",");
                String[] userSkills = user.getSkills().toLowerCase().split(",");
                int matched = 0;
                for (String reqSkill : reqSkills) {
                    for (String userSkill : userSkills) {
                        if (userSkill.trim().contains(reqSkill.trim()) || reqSkill.trim().contains(userSkill.trim())) {
                            matched++;
                            break;
                        }
                    }
                }
                if (matched > 0) {
                    score += matched * 20;
                    reasons.add("ðŸ› ï¸ " + matched + " skill match(es)");
                }
            }

            // 3. Reputation
            score += user.getReputationScore() * 2;
            if (user.getReputationScore() > 0) {
                reasons.add("â­ Rating " + String.format("%.1f", user.getReputationScore()));
            }

            // 4. Match help type bonus
            if (user.getSkills() != null) {
                String typeLower = request.getType().toLowerCase();
                String skillsLower = user.getSkills().toLowerCase();
                if ((typeLower.equals("blood") && skillsLower.contains("blood"))
                        || (typeLower.equals("medical") && (skillsLower.contains("medical") || skillsLower.contains("nurse") || skillsLower.contains("doctor")))
                        || (typeLower.equals("transport") && (skillsLower.contains("driv") || skillsLower.contains("transport")))
                        || (typeLower.equals("notes") && (skillsLower.contains("teach") || skillsLower.contains("tutor") || skillsLower.contains("educat")))) {
                    score += 10;
                    reasons.add("âœ… Type specialist");
                }
            }

            if (score > 0) {
                scores.add(new HelperScore(user, score, String.join(" Â· ", reasons)));
            }
        }

        // Sort by score descending
        scores.sort((a, b) -> Double.compare(b.score, a.score));
        return scores.size() > 5 ? scores.subList(0, 5) : scores;
    }
}

