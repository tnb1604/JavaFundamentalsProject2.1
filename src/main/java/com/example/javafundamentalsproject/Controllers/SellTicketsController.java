package com.example.javafundamentalsproject.Controllers;

import com.example.javafundamentalsproject.Model.Database;
import com.example.javafundamentalsproject.Model.SalesRecord;
import com.example.javafundamentalsproject.Model.Showing;
import com.example.javafundamentalsproject.Model.Ticket;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;

public class SellTicketsController {
    @FXML private Button selectSeatsButton;
    @FXML private TableView<Showing> sellTicketTableView;
    @FXML private AnchorPane sellTicketsPane;
    @FXML private AnchorPane selectSeatsPane;
    @FXML private TableColumn<Showing, LocalDateTime> startColumn;
    @FXML private TableColumn<Showing, LocalDateTime> endColumn;
    @FXML private TableColumn<Showing, String> titleColumn;
    @FXML private TableColumn<Showing, String> seatsLeftColumn;
    @FXML private Label showingSelectedLabel;
    @FXML private Label showingSelectedLabelAtSeats;
    @FXML private GridPane seatGrid;
    @FXML private ListView<String> selectedSeatsListView;
    @FXML private TextField customerNameTextField;
    @FXML private Label errorLabelTicketSelling;
    @FXML private Button confirmSellingTickets;

    private Button[][] seatButtons = new Button[6][12]; // 2D array for seats
    private List<String> selectedSeats = new ArrayList<>(); // List to track selected seats
    private final int ROWS = 6;
    private final int COLUMNS = 12;

    private ManageShowingsController manageShowingsController = new ManageShowingsController();
    private ObservableList<Showing> showings = FXCollections.observableArrayList();
    private Database database; // Database instance to hold the reference.
    private final String DATABASE_FILE = "src/main/resources/movie_theater_db.ser";

    @FXML
    public void initialize() {
        sellTicketsPane.toFront();
        selectSeatsPane.setVisible(false);
        initializeDatabase();
        initializeTableView();
        initializeSeatGrid();

        if (database != null) {
            loadShowingsFromDatabase();
        }

        // can only select seats if a showing is selected
        BooleanBinding showingSelected = sellTicketTableView.getSelectionModel().selectedItemProperty().isNull();
        selectSeatsButton.disableProperty().bind(showingSelected);

        // Add a listener to update the label when a showing is selected
        sellTicketTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            updateShowingSelectedLabel(newValue);
        });

        confirmSellingTickets.setDisable(true);
        updateSellTicketsButton();
    }

    private void initializeSeatGrid() {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMNS; col++) {
                final int finalRow = row;
                final int finalCol = col;
                Button seatButton = new Button(String.valueOf(col + 1));
                setSeatButtonSize(seatButton);

                // Check if the selected showing has this seat sold
                if (isSeatPurchased(finalRow, finalCol)) {
                    seatButton.setDisable(true);
                    seatButton.setStyle("-fx-background-color: #d52222;"); // Mark as purchased
                } else {
                    seatButton.setOnAction(event -> handleSeatSelection(finalRow, finalCol));
                    seatButton.setStyle(""); // Default style
                }

                seatGrid.add(seatButton, col, row);
                seatButtons[row][col] = seatButton;
            }
        }
    }



    private boolean isSeatPurchased(int row, int col) {
        Showing selectedShowing = sellTicketTableView.getSelectionModel().getSelectedItem();
        if (selectedShowing != null) {
            int seatNumber = row * COLUMNS + (col + 1); // Calculate seat number correctly
            boolean sold = selectedShowing.isSeatSold(seatNumber);
            System.out.println("Checking seat: Row " + (row + 1) + ", Column " + (col + 1) + ", Seat Number: " + seatNumber + ", Sold: " + sold);
            return sold;
        }
        return false; 
    }


    private void setSeatButtonSize(Button seatButton) {
        double buttonSize = 31.0;
        seatButton.setMinWidth(buttonSize);
        seatButton.setMinHeight(buttonSize);
        seatButton.setMaxWidth(buttonSize);
        seatButton.setMaxHeight(buttonSize);
    }

    private void handleSeatSelection(int row, int col) {
        String seatLabel = "Row " + (row + 1) + " / Seat " + (col + 1); // Use 1-based index for display
        Button seatButton = seatButtons[row][col];

        // Toggle seat selection
        if (selectedSeats.contains(seatLabel)) {
            selectedSeats.remove(seatLabel);
            seatButton.setStyle(""); // Deselect (reset button style)
            System.out.println("Deselected " + seatLabel);
        } else {
            // Ensure the seat is not already sold before adding to selected seats
            if (!isSeatPurchased(row, col)) {
                selectedSeats.add(seatLabel);
                seatButton.setStyle("-fx-background-color: lightgreen;"); // Mark seat as selected
                System.out.println("Selected " + seatLabel);
            }
        }

        // Update ListView with selected seats
        selectedSeatsListView.getItems().setAll(selectedSeats);
        updateSellTicketsButton();
    }



    private void updateSellTicketsButton() {
        int ticketCount = selectedSeats.size();
        if (ticketCount > 0) {
            confirmSellingTickets.setText("Sell " + ticketCount + " ticket" + (ticketCount > 1 ? "s" : ""));
            confirmSellingTickets.setDisable(false); // Enable the button if there's at least one seat selected
        } else {
            confirmSellingTickets.setText("Select ticket(s)");
            confirmSellingTickets.setDisable(true); // Disable the button if no seats are selected
        }
    }



    @FXML
    private void sellTicketsButtonClick() {
        Showing selectedShowing = sellTicketTableView.getSelectionModel().getSelectedItem();
        String customerName = customerNameTextField.getText();
        boolean shouldReturn = emptyInputsError(customerName);

        if (shouldReturn) {
            errorLabelTicketSelling.setVisible(true);
            return;
        }

        LocalDateTime purchaseDateTime = createSalesRecord(customerName, selectedShowing);

        // Now sell each ticket and update the showing
        for (String seatLabel : selectedSeats) {
            int seatNumber = extractSeatNumber(seatLabel);
            Ticket ticket = new Ticket(purchaseDateTime, customerName, seatNumber, selectedShowing); // Adjust constructor accordingly

            try {
                selectedShowing.sellTicket(ticket);
                database.saveTicket(ticket);
                database.updateShowing(selectedShowing);
            } catch (IllegalStateException e) {
                errorLabelTicketSelling.setText(e.getMessage());
                return;
            }
        }

        // Refresh the TableView and seat buttons
        loadShowingsFromDatabase();
        updateSeatButtons();
        clearSeatSelectionPane();
    }

    private LocalDateTime createSalesRecord(String customerName, Showing selectedShowing){
        int ticketCount = selectedSeats.size();

        LocalDateTime purchaseDateTime = LocalDateTime.now();
        SalesRecord salesRecord = new SalesRecord(
                purchaseDateTime,
                ticketCount,
                customerName,
                selectedShowing.getTitle(),
                selectedShowing.getStartTime());
        database.saveSalesRecord(salesRecord);
        return purchaseDateTime;
    }

    private boolean emptyInputsError(String customerName) {
        if (customerName.isEmpty()) {
            errorLabelTicketSelling.setVisible(true);
            errorLabelTicketSelling.setText("Customer name cannot be empty.");
            return true;
        }
        else if (selectedSeats.isEmpty()) {
            errorLabelTicketSelling.setVisible(true);
            errorLabelTicketSelling.setText("No seats selected.");
            return true;
        }
        return false;
    }


    private void updateSeatButtons() {
        Showing selectedShowing = sellTicketTableView.getSelectionModel().getSelectedItem();
        if (selectedShowing != null) {
            List<Integer> soldSeatNumbers = selectedShowing.getSoldSeatNumbers(); // Get sold seat numbers
            int totalSeats = selectedShowing.getTotalSeats(); // Get total seats

            for (int row = 0; row < seatButtons.length; row++) {
                for (int col = 0; col < seatButtons[row].length; col++) {
                    Button seatButton = seatButtons[row][col];
                    int seatNumber = row * COLUMNS + (col + 1); // Calculate seat number

                    // Reset the button state to unsold
                    seatButton.setDisable(false);
                    seatButton.setStyle(""); // Default style

                    // Disable and style the sold seats
                    if (soldSeatNumbers.contains(seatNumber)) {
                        seatButton.setDisable(true);
                        seatButton.setStyle("-fx-background-color: gray;"); // Mark as sold
                    }
                }
            }
        } else {
            System.out.println("No showing selected, cannot update seat buttons.");
        }
    }

    private void clearSeatSelectionPane(){
        selectedSeats.clear();
        selectedSeatsListView.getItems().clear();
        customerNameTextField.clear();
        showingSelectedLabel.setText("No showing selected");
        selectSeatsPane.setVisible(false);

        confirmSellingTickets.setText("Select ticket(s)");
        confirmSellingTickets.setDisable(true);
    }

    @FXML
    private void cancelSellingTicketsButtonClick() {
        clearSeatSelectionPane();
    }

    private int extractSeatNumber(String seatLabel) {
        // Assuming seatLabel is in the format "Row X / Seat Y"
        String[] parts = seatLabel.split(" / Seat ");
        int row = Integer.parseInt(parts[0].replace("Row ", "").trim()) - 1; // Convert to zero-based index
        int col = Integer.parseInt(parts[1].trim()) - 1; // Convert to zero-based index

        // Calculate the seat number based on the row and column
        return row * COLUMNS + (col + 1);
    }


    private void initializeTableView() {
        // Set cell value factories
        startColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getStartTime()));
        endColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getEndTime()));
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        seatsLeftColumn.setCellValueFactory(cellData -> {
            Showing showing = cellData.getValue();
            return new SimpleStringProperty(showing.getAvailableSeats() + "/" + showing.getTotalSeats());
        });

        // Set cell factories for formatting
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        setDateTimeCellFactory(startColumn, formatter);
        setDateTimeCellFactory(endColumn, formatter);

        sellTicketTableView.setItems(showings);
    }

    private void setDateTimeCellFactory(TableColumn<Showing, LocalDateTime> column, DateTimeFormatter formatter) {
        column.setCellFactory(cell -> new TableCell<Showing, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(formatter));
            }
        });
    }


    public void initializeDatabase(){
        loadDatabase();
        showings.setAll(database.getShowings());
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


    public void loadShowingsFromDatabase() {
        ObservableList<Showing> retrievedShowings = FXCollections.observableArrayList(database.getShowings());
        showings.setAll(retrievedShowings);
        sellTicketTableView.setItems(showings);
    }

    @FXML
    private void selectSeatsButtonClick() {
        Showing selectedShowing = sellTicketTableView.getSelectionModel().getSelectedItem();
        if (selectedShowing != null) {
            selectSeatsPane.setVisible(true);
        }
    }

    private void updateShowingSelectedLabel(Showing showing) {
        if (showing != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
            String formattedStartTime = showing.getStartTime().format(formatter);

            showingSelectedLabel.setText(showing.getTitle() + " - " + formattedStartTime);
            showingSelectedLabelAtSeats.setText(showing.getTitle() + " - " + formattedStartTime);

            updateSeatButtons();
        } else {
            showingSelectedLabel.setText("No showing selected.");
            showingSelectedLabelAtSeats.setText("No showing selected.");
        }
    }
}
