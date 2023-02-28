module com.example.project3_part2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.example.project3_part2.server to javafx.fxml;
        exports com.example.project3_part2.server;
    opens com.example.project3_part2.client to javafx.fxml;
    exports com.example.project3_part2.client;
}