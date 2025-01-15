package com.example.javafundamentalsproject.Controllers;
import com.example.javafundamentalsproject.Model.Database;
import com.example.javafundamentalsproject.Model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class MainController {
    @FXML private Button sellTicketsButton;
    @FXML private Button manageShowingsButton;
    @FXML private Button viewSalesHistoryButton;
    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private Label dateTimeLabel;
    @FXML private AnchorPane contentPane;  // The placeholder where content will be loaded
    private boolean isManageShowingsVisible = false; // Flag to track visibility
    private boolean isSellTicketsVisible = false;
    private boolean isViewSalesHistoryVisible = false;
    private Database database;
    private User currentUser;


    // Set up the database and user for the main window
    public void setDatabase(Database database) {
        this.database = database;
        updateLabels();
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
        updateLabels();
        setupButtonsForRole(); // Adjust button visibility based on role
    }

    private void updateLabels() {
        if (currentUser != null) {
            welcomeLabel.setText("Welcome " + currentUser.getUsername() + "!");
            roleLabel.setText("You are logged in as " + currentUser.getRole() + ".");
        }

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        dateTimeLabel.setText("The current date and time is " + now.format(formatter));
    }

    private void setupButtonsForRole() {
        String role = currentUser.getRole();
        if (role.equals("Sales")) {
            manageShowingsButton.setVisible(false); // Hide for Sales role
            viewSalesHistoryButton.setVisible(false); // Hide sales history for Sales role
        } else if (role.equals("Management")) {
            sellTicketsButton.setVisible(false); // Hide for Management role
        }
    }

    // Action handler for the "Manage Showings" button
    @FXML
    private void handleShowingManagement() {
        if (isManageShowingsVisible) {
            // If currently visible, remove the content
            contentPane.getChildren().clear();
        } else {
            // If currently not visible, load and display the content
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafundamentalsproject/manage-showings-view.fxml"));
                AnchorPane manageShowingsPane = loader.load();
                contentPane.getChildren().clear();
                contentPane.getChildren().add(manageShowingsPane);
            } catch (IOException e) {
                e.printStackTrace();  // Handle any file loading issues
            }
        }

        // Toggle the flag
        isManageShowingsVisible = !isManageShowingsVisible;
    }

    @FXML
    private void handleTicketSales() {
        if (isSellTicketsVisible) {
            // If currently visible, remove the content
            contentPane.getChildren().clear();
        } else {
            // If currently not visible, load and display the content
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafundamentalsproject/sell-tickets-view.fxml"));
                AnchorPane sellTicketsPane = loader.load();
                contentPane.getChildren().clear();
                contentPane.getChildren().add(sellTicketsPane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Toggle the flag
        isSellTicketsVisible = !isSellTicketsVisible;
    }

    @FXML
    private void handleSalesHistory() {
        System.out.println("View Sales History button clicked."); // Debug statement
        if (isViewSalesHistoryVisible) {
            contentPane.getChildren().clear();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/javafundamentalsproject/sales-history-view.fxml"));
                AnchorPane salesHistoryPane = loader.load();
                contentPane.getChildren().clear();
                contentPane.getChildren().add(salesHistoryPane);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        isViewSalesHistoryVisible = !isViewSalesHistoryVisible;
    }

}
