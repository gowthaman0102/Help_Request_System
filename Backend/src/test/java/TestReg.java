import com.crowdhelp.backend.dao.UserDAO;
import com.crowdhelp.backend.model.User;
import com.crowdhelp.database.DatabaseConnection;

public class TestReg {
    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();
        UserDAO dao = new UserDAO();
        User u = new User();
        u.setName("Test");
        u.setEmail("test" + System.currentTimeMillis() + "@test.com");
        u.setPasswordHash("pass");
        u.setLocation("Loc");
        u.setSkills("Skill");
        u.setReputationScore(0.0);
        
        System.out.println("Trying to create user...");
        User res = dao.createUser(u);
        System.out.println("Result: " + res);
    }
}

