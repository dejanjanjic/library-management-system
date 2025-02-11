package net.etfbl.biblioteka.controller;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.etfbl.biblioteka.config.ConfigLoader;
import net.etfbl.biblioteka.model.Book;
import net.etfbl.biblioteka.model.User;
import net.etfbl.biblioteka.logger.BibliotekaLogger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.Base64;

public class AddBookController {

    @FXML
    private TextField authorField;

    @FXML
    private TextField contentField;

    @FXML
    private TextField idField;

    @FXML
    private TextField languageField;

    @FXML
    private TextField picturePathField;

    @FXML
    private Label status;

    @FXML
    private TextField titleField;

    @FXML
    private TextField yearField;

    @FXML
    void addBook(MouseEvent event) {
        try {
            URL registerURL = new URL(ConfigLoader.getInstance().getProperty("url.books"));
            HttpURLConnection connection = (HttpURLConnection) registerURL.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            Gson gson = new Gson();

            String base64Image = encodeImageToBase64(picturePathField.getText());

            try(PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), true)){
                Integer id = Integer.parseInt(idField.getText());
                String author = authorField.getText();
                String title = titleField.getText();
                String year = yearField.getText();
                String language = languageField.getText();
                String content = contentField.getText();

                printWriter.println(gson.toJson(new Book(id, title, author, year, language, base64Image, content)));
                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.close();
                } else if (connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND) {
                    status.setVisible(true);
                    status.setText("Unsuccessful attempt. Book with this id already exists.");
                    status.setTextFill(Color.RED);
                } else{
                    status.setVisible(true);
                    status.setText("Unsuccessful attempt. Try again later");
                    status.setTextFill(Color.RED);
                }
            }

        }catch (IOException e){
            BibliotekaLogger.logger.severe("Error: " + e);
        }
    }

    @FXML
    void insertPicture(MouseEvent event) {
// Kreiramo FileChooser objekat
        FileChooser fileChooser = new FileChooser();

        // Podešavamo naslov prozora
        fileChooser.setTitle("Select Picture");

        // Filtriramo tipove fajlova samo na slike (možete dodati više ekstenzija po potrebi)
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Otvaramo dijalog prozor i uzimamo fajl koji je korisnik izabrao
        File selectedFile = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

        if (selectedFile != null) {
            // Ako je korisnik izabrao fajl, postavljamo njegovu putanju u picturePathField
            picturePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private String encodeImageToBase64(String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        byte[] fileContent = Files.readAllBytes(imageFile.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }
}
