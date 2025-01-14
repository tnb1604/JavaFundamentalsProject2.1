package com.example.javafundamentalsproject.Controllers;

import com.example.javafundamentalsproject.Model.Database;
import com.example.javafundamentalsproject.Model.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.application.Platform;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private TextField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorMessageLabel;
    private String userRole;

    private Database database; // Database instance

    public void setDatabase(Database database) {
        this.database = database;
        System.out.println("Database has been initialized in the controller: " + database); // Debug line
    }

    public void initialize() {
        Platform.runLater(() -> {
            usernameField.requestFocus();
        });
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = database.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("Login successful for user: " + username);

            this.userRole = user.getRole();

            //close login and open main window
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.close();
            openMainWindow(user);
        } else {
            errorMessageLabel.setText("Invalid username/password combination");
        }
    }

    private void openMainWindow(User user) {
        try {
            // Load the main view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafundamentalsproject/main-view.fxml"));
            Parent root = loader.load();

            // Get the controller for the main view
            MainController mainController = loader.getController();

            // Pass the user and database to the main controller
            mainController.setCurrentUser(user);
            mainController.setDatabase(database);

            // Create a new stage for the main window
            Stage mainStage = new Stage();
            mainStage.setTitle("Movie Theater Management");
            mainStage.setScene(new Scene(root, 735, 533)); // Set the size of the main window
            mainStage.show(); // Show the main window

        } catch (Exception e) {
            e.printStackTrace();
            // Show an error message if the main window fails to load
            errorMessageLabel.setText("Failed to load the main window.");
        }
    }
}