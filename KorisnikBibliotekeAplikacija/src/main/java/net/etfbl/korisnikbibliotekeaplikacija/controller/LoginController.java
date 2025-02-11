package net.etfbl.korisnikbibliotekeaplikacija.controller;

import com.google.gson.Gson;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import net.etfbl.korisnikbibliotekeaplikacija.config.ConfigLoader;
import net.etfbl.korisnikbibliotekeaplikacija.gui.KorisnikApplication;
import net.etfbl.korisnikbibliotekeaplikacija.model.User;
import net.etfbl.korisnikbibliotekeaplikacija.multicast.MulticastClient;
import net.etfbl.korisnikbibliotekeaplikacija.logger.KorisnikLogger;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginController {
    private ObservableList<String> messageList = FXCollections.observableArrayList();

    public static User user;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField usernameField;
    @FXML
    private Label status;

    @FXML
    void showRegisterWindow(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(KorisnikApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.register")));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void signIn(MouseEvent event) {
        try {
            URL registerURL = new URL(ConfigLoader.getInstance().getProperty("url.login"));
            HttpURLConnection connection = (HttpURLConnection) registerURL.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            Gson gson = new Gson();
            try(PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), true)){
                String username = usernameField.getText();
                String password = passwordField.getText();


                printWriter.println(gson.toJson(new User(username, password)));
                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))){
                        user = gson.fromJson(bufferedReader, User.class);
                    }
                    MulticastClient multicastClient = new MulticastClient(messageList);
                    multicastClient.start();

                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    FXMLLoader fxmlLoader = new FXMLLoader(KorisnikApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.books")));
                    Scene scene = new Scene(fxmlLoader.load());

                    BooksController controller = fxmlLoader.getController();
                    controller.setMessageList(messageList);

                    stage.setScene(scene);
                    stage.show();
                } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    status.setVisible(true);
                    status.setText("Unsuccessful login.");
                    status.setTextFill(Color.RED);
                } else{
                    status.setVisible(true);
                    status.setText("Unsuccessful login. Try again later");
                    status.setTextFill(Color.RED);
                }


            }
        }catch (IOException e){
            KorisnikLogger.logger.severe("Error: " + e);
        }
    }

}
