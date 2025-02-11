package net.etfbl.biblioteka.controller;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import net.etfbl.biblioteka.config.ConfigLoader;
import net.etfbl.biblioteka.model.User;
import net.etfbl.biblioteka.logger.BibliotekaLogger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddUserController {
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";


    @FXML
    private TextField addressField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField repeatPasswordField;

    @FXML
    private Label status;

    @FXML
    private TextField usernameField;

    @FXML
    void register(MouseEvent event) {
        try {
            URL registerURL = new URL(ConfigLoader.getInstance().getProperty("url.register"));
            HttpURLConnection connection = (HttpURLConnection) registerURL.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            Gson gson = new Gson();
            try(PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), true)){
                String firstName = firstNameField.getText();
                String lastName = lastNameField.getText();
                String address = addressField.getText();
                String email = emailField.getText();
                String username = usernameField.getText();
                String password = passwordField.getText();
                String repeatPassword = repeatPasswordField.getText();
                if(!email.matches(EMAIL_REGEX)){
                    status.setVisible(true);
                    status.setText("Unsuccessful registration. Email must be valid.");
                    status.setTextFill(Color.RED);
                    return;
                }
                if(password.equals(repeatPassword)){
                    printWriter.println(gson.toJson(new User(firstName, lastName, address, email, username, password)));
                    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        status.setVisible(true);
                        status.setText("Successful registration.");
                        status.setTextFill(Color.GREEN);
                    } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                        status.setVisible(true);
                        status.setText("Unsuccessful registration. Username already exists.");
                        status.setTextFill(Color.RED);
                    } else{
                        status.setVisible(true);
                        status.setText("Unsuccessful registration. Try again later");
                        status.setTextFill(Color.RED);
                    }
                }else{
                    status.setVisible(true);
                    status.setText("Passwords must be same");
                    status.setTextFill(Color.RED);
                }

            }
        }catch (IOException e){
            BibliotekaLogger.logger.severe("Error: " + e);
        }
    }

}
