package com.crowdhelp;

import com.crowdhelp.database.DatabaseConnection;
import com.crowdhelp.backend.util.SampleDataSeeder;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseConnection.initializeDatabase();
        SampleDataSeeder.seed();

        SceneManager.initialize(primaryStage);
        SceneManager.showLogin();
    }

    @Override
    public void stop() {
        DatabaseConnection.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


