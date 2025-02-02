module org.example.myapp {
    requires javafx.graphics;
    requires javafx.controls;
    requires java.desktop;
    requires static lombok;
    requires javax.websocket.api;
    requires org.apache.commons.io;
    exports org.example.myapp;
}