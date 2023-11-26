module com.exemple.pets {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.example.pets to javafx.fxml;
    exports com.example.pets.model;
    opens com.example.pets.controller to javafx.fxml;


    opens com.example.pets.model to javafx.fxml;
    exports com.example.pets;
}