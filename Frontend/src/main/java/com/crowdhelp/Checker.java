package com.crowdhelp;
import com.crowdhelp.backend.dao.RequestDAO;
import java.io.FileWriter;
public class Checker {
    public static void main(String[] args) throws Exception {
        try (FileWriter fw = new FileWriter("c:/Help_1/CrowdHelpSystem/test-results.txt")) {
            fw.write("Total requests in DB: " + new RequestDAO().getAllRequests().size());
        }
    }
}

