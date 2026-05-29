module com.jethop {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.jethop to javafx.fxml;
    exports com.jethop;
}
