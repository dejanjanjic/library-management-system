package net.etfbl.dobavljac.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.etfbl.dobavljac.config.ConfigLoader;

import java.io.IOException;

public class DobavljacApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DobavljacApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.login")));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("eLibrary");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
