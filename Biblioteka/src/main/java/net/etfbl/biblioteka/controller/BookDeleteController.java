package net.etfbl.biblioteka.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.etfbl.biblioteka.config.ConfigLoader;
import net.etfbl.biblioteka.logger.BibliotekaLogger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class BookDeleteController {

    @FXML
    private TextField idTextField;

    @FXML
    private Label status;

    @FXML
    void deleteBook(MouseEvent event) {
        int  id = Integer.parseInt(idTextField.getText());
        if(deleteById(id)){
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }else{
            status.setVisible(true);

        }
    }

    private boolean deleteById(int id) {
        try{
            URL url = new URL(ConfigLoader.getInstance().getProperty("url.books") + "/" + id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");
            try(PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), true)){
                printWriter.println(id);
                if(connection.getResponseCode() == HttpURLConnection.HTTP_NO_CONTENT){
                    return true;
                }
                else{
                    return false;
                }
            }
        }catch (IOException e){
            BibliotekaLogger.logger.severe("Error: " + e);
        }
        return false;
    }

}
