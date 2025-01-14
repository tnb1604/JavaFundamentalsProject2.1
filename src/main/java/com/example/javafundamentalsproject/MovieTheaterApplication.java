package com.example.javafundamentalsproject;

import com.example.javafundamentalsproject.Controllers.LoginController;
import com.example.javafundamentalsproject.Model.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class MovieTheaterApplication extends Application {

    private final String DATABASE_FILE = "src/main/resources/movie_theater_db.ser";
    private Database database;

    @Override
    public void start(Stage primaryStage) {
        initializeDatabase(); // Load or create database

        try {
            // Load the Login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load(); // Load the FXML

            // Get the controller from the FXML loader and set the database
            LoginController controller = loader.getController();
            controller.setDatabase(database); // Pass the database instance to the controller

            primaryStage.setTitle("Movie Theater Login");
            primaryStage.setScene(new Scene(root, 320, 140)); // Set the scene size
            primaryStage.show(); // Show the primary stage
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            showError("Failed to load the login screen: " + e.getMessage()); // Show a user-friendly error message
        }
    }



    private void initializeDatabase() {
        File dbFile = new File(DATABASE_FILE);
        if (dbFile.exists()) {
            loadDatabase();
        } else {
            database = new Database(); // Initialize a new database if the file doesn't exist
        }
    }

    private void loadDatabase() {
        try {
            database = Database.loadDatabase(DATABASE_FILE);
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            database = new Database(); // Create new if deserialization fails
        }
    }

    private void showError(String message) {
        // Show an error dialog to the user (you might want to implement this method)
        // You can use a JavaFX Alert to display the error
    }

    @Override
    public void stop() {
        saveDatabase(); // Save the database when application closes
    }

    private void saveDatabase() {
        try {
            Database.saveDatabase(database, DATABASE_FILE);
        } catch (Exception e) {
            e.printStackTrace(); // Handle any exceptions during saving
        }
    }

    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
