package com.crowdhelp;
import com.crowdhelp.database.DatabaseConnection;
import com.crowdhelp.backend.util.SampleDataSeeder;
public class SeedRunner {
    public static void main(String[] args) {
        DatabaseConnection.initializeDatabase();
        SampleDataSeeder.seed();
        System.out.println("Seeding complete.");
    }
}


