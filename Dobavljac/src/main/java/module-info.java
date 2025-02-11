module net.etfbl.dobavljac {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires com.rabbitmq.client;
    requires java.rmi;
    requires java.logging;


    opens net.etfbl.dobavljac.app to javafx.fxml, com.google.gson, com.rabbitmq.client, java.rmi, java.logging;
    exports net.etfbl.dobavljac.app;

    opens net.etfbl.dobavljac.controller to javafx.fxml, com.google.gson, com.rabbitmq.client, java.rmi, java.logging;
    exports net.etfbl.dobavljac.controller;

    opens net.etfbl.dobavljac.server to javafx.fxml, com.google.gson, com.rabbitmq.client, java.rmi, java.logging;
    exports net.etfbl.dobavljac.server;

    opens net.etfbl.biblioteka.model to com.google.gson, com.rabbitmq.client, java.rmi, java.logging;
    exports net.etfbl.biblioteka.model;

    opens net.etfbl.dobavljac.service to com.google.gson, com.rabbitmq.client, java.rmi, java.logging;
    exports net.etfbl.dobavljac.service;

    opens net.etfbl.dobavljac.model to com.google.gson, com.rabbitmq.client, java.rmi, java.logging;
    exports net.etfbl.dobavljac.model;

    opens net.etfbl.dobavljac.logger to com.google.gson, com.rabbitmq.client, java.rmi, java.logging;
    exports net.etfbl.dobavljac.logger;

}