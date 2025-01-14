package com.example.javafundamentalsproject.Controllers;
import com.example.javafundamentalsproject.Model.Database;
import com.example.javafundamentalsproject.Model.Showing;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import javafx.scene.control.TableColumn;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.Button;
import javafx.beans.binding.BooleanBinding;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.TableCell;

public class ManageShowingsController {
    @FXML public AnchorPane manageShowingsPane;
    public Button confirmEditButton;
    @FXML private AnchorPane addNewShowingPane;
    @FXML private AnchorPane editShowingPane;
    @FXML private TableView<Showing> showingTableView;
    @FXML private TextField addTitleField;
    @FXML private TextField addStartTimeField;
    @FXML private TextField addEndTimeField;
    @FXML private TextField editTitleField;
    @FXML private TextField editStartTimeField;
    @FXML private TextField editEndTimeField;
    @FXML private DatePicker addStartDatePicker;
    @FXML private DatePicker addEndDatePicker;
    @FXML private DatePicker editStartDatePicker;
    @FXML private DatePicker editEndDatePicker;
    @FXML private TableColumn<Showing, LocalDateTime> startColumn;
    @FXML private TableColumn<Showing, LocalDateTime> endColumn;
    @FXML private TableColumn<Showing, String> titleColumn;
    @FXML private TableColumn<Showing, String> seatsLeftColumn;
    @FXML private Label editingErrorLabel;
    @FXML private Label addingErrorLabel;
    @FXML private Button editShowingButton;
    @FXML private Button deleteShowingButton;
    @FXML private Label deleteShowingErrorLabel;

    private ObservableList<Showing> showings = FXCollections.observableArrayList();
    private Database database;
    private final String DATABASE_FILE = "src/main/resources/movie_theater_db.ser";


    @FXML
    public void initialize() {
        initializeDatabase();
        manageShowingsPane.toFront();
        addNewShowingPane.setVisible(false);
        editShowingPane.setVisible(false);

        // Bind the buttons to the selection in the TableView
        BooleanBinding showingSelected = showingTableView.getSelectionModel().selectedItemProperty().isNull();
        editShowingButton.disableProperty().bind(showingSelected);
        deleteShowingButton.disableProperty().bind(showingSelected);

        initializeTableView();
        initializeDatePickerListener();
    }




    private void initializeDatePickerListener(){
        // Add listeners for start date pickers to auto-f   ill the end date
        addStartDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) { // Ensure newDate is not null
                LocalDate endDate = addEndDatePicker.getValue();
                if (endDate == null || endDate.isBefore(newDate)) {
                    addEndDatePicker.setValue(newDate); // Set end date if it's null or before start date
                }
            }
        });

        editStartDatePicker.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) { // Ensure newDate is not null
                LocalDate endDate = editEndDatePicker.getValue();
                if (endDate == null || endDate.isBefore(newDate)) {
                    editEndDatePicker.setValue(newDate); // Set end date if it's null or before start date
                }
            }
        });
    }

    private void initializeDatabase(){
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

        showingTableView.setItems(showings);
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

    private void saveDatabase() {
        try {
            Database.saveDatabase(database, DATABASE_FILE);
        } catch (IOException e) {
            // Handle error (e.g., failed to save database)
        }
    }

    @FXML
    private void showAddNewShowingPane() {
        clearErrorMessages();
        clearAddNewShowingFields();
        addNewShowingPane.setVisible(true);
        addTitleField.requestFocus();
    }

    @FXML
    private void addShowing() {
        clearErrorMessages();
        try {
            String title = addTitleField.getText();
            LocalDate startDate = addStartDatePicker.getValue();
            LocalDate endDate = addEndDatePicker.getValue();
            String startTimeText = addStartTimeField.getText();
            String endTimeText = addEndTimeField.getText();

            // Parse the time strings into LocalTime objects
            LocalTime startTime = LocalTime.parse(startTimeText);
            LocalTime endTime = LocalTime.parse(endTimeText);

            if (endDate.isBefore(startDate) || (endDate.isEqual(startDate) && endTime.isBefore(startTime))) {
                addingErrorLabel.setText("End date/time cannot be before start date/time.");
                addingErrorLabel.setVisible(true);
                return;
            }

            // Create a new Showing object and add to database
            Showing newShowing = new Showing(title, LocalDateTime.of(startDate, startTime), LocalDateTime.of(endDate, endTime), 72);
            showings.add(newShowing);
            database.addShowing(newShowing);

            // Clear fields, return to main pane, and save the updated database
            saveDatabase();
            clearAddNewShowingFields();
            addNewShowingPane.setVisible(false);
            manageShowingsPane.setVisible(true);
        } catch (Exception e) {
            addingErrorLabel.setText("Please fill all fields in their correct formats.");
            addingErrorLabel.setVisible(true);
        }
    }

    @FXML
    private void showEditShowingPane() {
        clearErrorMessages();
        Showing selectedShowing = showingTableView.getSelectionModel().getSelectedItem();

        // Pre-fill the fields with the selected showing's information
        editTitleField.setText(selectedShowing.getTitle());
        editStartDatePicker.setValue(selectedShowing.getStartDate());
        editEndDatePicker.setValue(selectedShowing.getEndDate());
        editStartTimeField.setText(selectedShowing.getStartTimeOnly().toString());
        editEndTimeField.setText(selectedShowing.getEndTimeOnly().toString());
        editShowingPane.setVisible(true);
        editTitleField.requestFocus();
    }


    @FXML
    private void editShowing() {
        try {
            Showing selectedShowing = showingTableView.getSelectionModel().getSelectedItem();
            String title = editTitleField.getText();
            LocalDate startDate = editStartDatePicker.getValue();
            LocalDate endDate = editEndDatePicker.getValue();
            LocalTime startTime = LocalTime.parse(editStartTimeField.getText());
            LocalTime endTime = LocalTime.parse(editEndTimeField.getText());

            if (endDate.isBefore(startDate) || (endDate.isEqual(startDate) && endTime.isBefore(startTime))) {
                editingErrorLabel.setText("End date/time cannot be before start date/time.");
                editingErrorLabel.setVisible(true);
                return;
            }

            // Update the selected showing and make changes in database
            selectedShowing.updateShowingDetails(title, LocalDateTime.of(startDate, startTime), LocalDateTime.of(endDate, endTime), selectedShowing.getTotalSeats());
            database.updateShowing(selectedShowing);
            saveDatabase();
            showingTableView.refresh();

            // Clear fields and hide the pane
            clearEditShowingFields();
            editShowingPane.setVisible(false);
            manageShowingsPane.setVisible(true);
        } catch (Exception e) {
            editingErrorLabel.setText("Please fill all fields in their correct formats.");
            editingErrorLabel.setVisible(true);
        }
    }

    @FXML
    private void deleteShowing() {
        Showing selectedShowing = showingTableView.getSelectionModel().getSelectedItem();

        if (selectedShowing == null) {
            return;
        }

        clearErrorMessages();
        if(selectedShowing.hasTicketsSold()){
            System.out.println("Tickets have been sold, so you can't delete the showing..");
            deleteShowingErrorLabel.setVisible(true);
            return;
        }

        // Remove the selected showing from the list and save updated database
        showings.remove(selectedShowing);
        database.deleteShowing(selectedShowing);
        saveDatabase();
        showingTableView.refresh();
    }


    @FXML
    private void cancelAddShowing() {
        // Hide the add showing pane and return to main pane
        addNewShowingPane.setVisible(false);
        manageShowingsPane.setVisible(true);
    }

    @FXML
    private void cancelEditShowing() {
        // Hide the edit showing pane and return to main pane
        clearEditShowingFields();
        editShowingPane.setVisible(false);
        manageShowingsPane.setVisible(true);
    }

    private void clearAddNewShowingFields() {
        addTitleField.clear();
        addStartDatePicker.setValue(null);
        addEndDatePicker.setValue(null);
        addStartTimeField.clear();
        addEndTimeField.clear();
    }

    private void clearEditShowingFields() {
        editTitleField.clear();
        editStartDatePicker.setValue(null);
        editEndDatePicker.setValue(null);
        editStartTimeField.clear();
        editEndTimeField.clear();
    }
    private void clearErrorMessages() {
        editingErrorLabel.setVisible(false);
        addingErrorLabel.setVisible(false);
        deleteShowingErrorLabel.setVisible(false);
    }
}
