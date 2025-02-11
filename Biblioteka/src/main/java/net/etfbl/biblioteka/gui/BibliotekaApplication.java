package net.etfbl.biblioteka.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.etfbl.biblioteka.config.ConfigLoader;
import net.etfbl.biblioteka.controller.BibliotekaController;
import net.etfbl.biblioteka.multicast.MulticastClient;

import java.io.IOException;

public class BibliotekaApplication extends Application {
    private ObservableList<String> messageList = FXCollections.observableArrayList();
    @Override
    public void start(Stage stage) throws IOException {
        MulticastClient multicastClient = new MulticastClient(messageList);
        multicastClient.start();

        FXMLLoader fxmlLoader = new FXMLLoader(BibliotekaApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.library-users")));
        Scene scene = new Scene(fxmlLoader.load());

        BibliotekaController controller = fxmlLoader.getController();
        controller.setMessageList(messageList);


        stage.setTitle("eLibrary");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}

