module com.example.laboratorjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    opens com.example.laboratorjavafx.domain to javafx.base;
    opens com.example.laboratorjavafx to javafx.fxml;
    exports com.example.laboratorjavafx;
    exports com.example.laboratorjavafx.controllers;
    opens com.example.laboratorjavafx.controllers to javafx.fxml;
}