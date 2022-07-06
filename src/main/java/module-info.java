module com.vanityblade.cgol {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.vanityblade.cgol to javafx.fxml;
    exports com.vanityblade.cgol;
}