module com.uaemex.td.dataencodingvisualizer {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.uaemex.td.dataencodingvisualizer;

    opens com.uaemex.td.dataencodingvisualizer.controller to javafx.fxml;

}