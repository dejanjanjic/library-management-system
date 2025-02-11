package net.etfbl.dobavljac.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.etfbl.dobavljac.app.DobavljacApplication;
import net.etfbl.dobavljac.config.ConfigLoader;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField supplierNameField;

    @FXML
    void signIn(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(DobavljacApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.supplier")));
        Scene scene = new Scene(fxmlLoader.load());

        SupplierController controller = fxmlLoader.getController();
        controller.setSupplierName(supplierNameField.getText());

        stage.setScene(scene);
        stage.show();
    }
}
