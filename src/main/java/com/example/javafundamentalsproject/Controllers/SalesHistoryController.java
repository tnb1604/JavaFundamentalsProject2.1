package com.example.javafundamentalsproject.Controllers;

import com.example.javafundamentalsproject.Model.Database;
import com.example.javafundamentalsproject.Model.SalesRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import javafx.scene.control.TableCell;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SalesHistoryController {

    @FXML private TableView<SalesRecord> salesRecord; // Use SalesRecord as the data model
    @FXML private TableColumn<SalesRecord, LocalDateTime> purchaseDateTimeColumn; // For the date/time
    @FXML private TableColumn<SalesRecord, Integer> ticketCountColumn; // For the ticket count
    @FXML private TableColumn<SalesRecord, String> customerColumn; // For the customer name
    @FXML private TableColumn<SalesRecord, String> showingColumn; // For the showing info

    private ObservableList<SalesRecord> salesHistoryData = FXCollections.observableArrayList();
    private Database database; // Assuming Database has a method to load sales records
    private final String DATABASE_FILE = "src/main/resources/movie_theater_db.ser";

    public void initialize() {
        // Set up the columns with the corresponding properties
        purchaseDateTimeColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseDateTime"));
        ticketCountColumn.setCellValueFactory(new PropertyValueFactory<>("ticketCount"));
        customerColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        showingColumn.setCellValueFactory(new PropertyValueFactory<>("showingInfo"));

        // Set custom cell factory for purchaseDateTimeColumn to format the date/time
        purchaseDateTimeColumn.setCellFactory(new Callback<TableColumn<SalesRecord, LocalDateTime>, TableCell<SalesRecord, LocalDateTime>>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

            @Override
            public TableCell<SalesRecord, LocalDateTime> call(TableColumn<SalesRecord, LocalDateTime> column) {
                return new TableCell<SalesRecord, LocalDateTime>() {
                    @Override
                    protected void updateItem(LocalDateTime item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(item.format(formatter));
                        }
                    }
                };
            }
        });

        loadDatabase();
        loadSalesHistory();
    }

    private void loadSalesHistory() {
        if (database != null) { // Check if database is initialized
            List<SalesRecord> salesRecords = database.loadSalesRecords(); // Load sales records directly

            // Clear previous data and populate sales history data
            salesHistoryData.clear(); // Clear existing data before adding new data

            // Add each SalesRecord to the observable list
            salesHistoryData.addAll(salesRecords);

            salesRecord.setItems(salesHistoryData); // Set the data to the TableView
        } else {
            System.err.println("Database is not initialized!"); // Debug message
        }
    }

    private void loadDatabase() {
        try {
            database = Database.loadDatabase(DATABASE_FILE);
        } catch (IOException e) {
            System.out.println("Error loading database file: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found during database load: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}