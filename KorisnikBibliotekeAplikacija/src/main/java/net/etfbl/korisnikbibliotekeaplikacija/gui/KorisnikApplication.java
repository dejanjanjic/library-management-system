package net.etfbl.korisnikbibliotekeaplikacija.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.etfbl.korisnikbibliotekeaplikacija.config.ConfigLoader;
import net.etfbl.korisnikbibliotekeaplikacija.controller.BooksController;
import net.etfbl.korisnikbibliotekeaplikacija.multicast.MulticastClient;

import java.io.IOException;

public class KorisnikApplication extends Application {




    @Override
    public void start(Stage stage) throws IOException {


        FXMLLoader fxmlLoader = new FXMLLoader(KorisnikApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.login")));
        Scene scene = new Scene(fxmlLoader.load());


        stage.setTitle("eLibrary");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
