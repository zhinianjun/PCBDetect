module com.example.pcbclient {
    requires javafx.controls;
    requires javafx.fxml;

    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires jeromq;
    requires fastjson;
    requires java.sql;
    requires static lombok;
    requires java.desktop;
    opens com.example.pcbclient to javafx.fxml;
    exports com.example.pcbclient;
    exports com.example.pcbclient.mq;
    opens com.example.pcbclient.mq to javafx.fxml;
    exports com.example.pcbclient.entity;
    opens com.example.pcbclient.entity to fastjson;
    requires hutool.all;
    requires javafx.swing;
    requires java.management;
}