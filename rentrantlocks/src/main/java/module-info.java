module com.example.rentrantlocks {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.rentrantlocks to javafx.fxml;
    exports com.example.rentrantlocks;
}