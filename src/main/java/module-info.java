module com.example.javafundamentalsproject {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.javafundamentalsproject to javafx.fxml;
    exports com.example.javafundamentalsproject;
    exports com.example.javafundamentalsproject.Controllers;
    opens com.example.javafundamentalsproject.Controllers to javafx.fxml;
    exports com.example.javafundamentalsproject.Model;
    opens com.example.javafundamentalsproject.Model to javafx.fxml;
}