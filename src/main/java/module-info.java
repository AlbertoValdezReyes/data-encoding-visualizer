module com.uaemex.td.dataencodingvisualizer {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.uaemex.td.dataencodingvisualizer to javafx.fxml;
    exports com.uaemex.td.dataencodingvisualizer;
}