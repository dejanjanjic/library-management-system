module net.etfbl.biblioteka {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires com.rabbitmq.client;
    requires org.slf4j;
    requires org.apache.logging.log4j;
    requires java.logging;

    opens net.etfbl.biblioteka.gui to javafx.fxml;
    exports net.etfbl.biblioteka.gui;
    opens net.etfbl.biblioteka.controller to javafx.fxml, com.google.gson, com.rabbitmq.client, org.slf4j, org.apache.logging.log4j;
    exports net.etfbl.biblioteka.controller;
    opens net.etfbl.biblioteka.model to javafx.fxml, com.google.gson, com.rabbitmq.client, org.slf4j, org.apache.logging.log4j;
    exports net.etfbl.biblioteka.model;

    opens net.etfbl.biblioteka.logger to javafx.fxml, com.google.gson, com.rabbitmq.client, org.slf4j, org.apache.logging.log4j;
    exports net.etfbl.biblioteka.logger;
}