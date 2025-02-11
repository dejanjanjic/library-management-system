package net.etfbl.dobavljac.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import net.etfbl.dobavljac.config.ConfigLoader;
import net.etfbl.dobavljac.logger.DobavljacLogger;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.util.Base64;


public class AddBookController {
    private String supplierName;
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
            InetAddress address = InetAddress.getLocalHost();
            int port = Integer.parseInt(ConfigLoader.getInstance().getProperty("server.port"));
            String base64Image = encodeImageToBase64(picturePathField.getText());
            try(Socket socket = new Socket(address, port);
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())){
                oos.writeObject("ADD#" + supplierName + "#" + idField.getText() + "#" +
                        titleField.getText() + "#" + authorField.getText() + "#" +
                        yearField.getText() + "#" + languageField.getText() + "#" +
                        base64Image + "#" + contentField.getText());
                String response = (String)ois.readObject();
                if(!"OK".equals(response)){
                    throw new RuntimeException("Los zahtjev");
                }
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            }

        } catch (Exception e) {
            DobavljacLogger.logger.severe("Error: " + e);
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

    public void setSupplierName(String supplierName) {
        this.supplierName=supplierName;
    }

    private String encodeImageToBase64(String imagePath) throws IOException {
        File imageFile = new File(imagePath);
        byte[] fileContent = Files.readAllBytes(imageFile.toPath());
        return Base64.getEncoder().encodeToString(fileContent);
    }
}
