module org.example.myapp {
    requires javafx.graphics;
    requires javafx.controls;
    requires java.desktop;
    requires static lombok;
    requires org.apache.commons.io;
    requires com.fasterxml.jackson.databind;
    exports org.example.myapp;
}