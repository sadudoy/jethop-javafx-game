module com.jethop {
    requires transitive javafx.controls;
    requires javafx.fxml;

    opens com.jethop to javafx.fxml;
    exports com.jethop;
}
