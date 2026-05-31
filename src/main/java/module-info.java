module com.jethop {
    requires transitive javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.base;

    opens com.jethop to javafx.fxml;
    exports com.jethop;
}
