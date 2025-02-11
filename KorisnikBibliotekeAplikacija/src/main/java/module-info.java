module net.etfbl.korisnikbibliotekeaplikacija {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.logging;


    opens net.etfbl.korisnikbibliotekeaplikacija.gui to javafx.fxml;
    exports net.etfbl.korisnikbibliotekeaplikacija.gui;

    opens net.etfbl.korisnikbibliotekeaplikacija.controller to javafx.fxml;
    exports net.etfbl.korisnikbibliotekeaplikacija.controller;

    opens net.etfbl.korisnikbibliotekeaplikacija.model to javafx.fxml, com.google.gson;
    exports net.etfbl.korisnikbibliotekeaplikacija.model;

    opens net.etfbl.korisnikbibliotekeaplikacija.logger to javafx.fxml, com.google.gson;
    exports net.etfbl.korisnikbibliotekeaplikacija.logger;

}