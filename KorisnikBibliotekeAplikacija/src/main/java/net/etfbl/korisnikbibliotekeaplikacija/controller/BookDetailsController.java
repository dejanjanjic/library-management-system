package net.etfbl.korisnikbibliotekeaplikacija.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import net.etfbl.korisnikbibliotekeaplikacija.model.Book;
import net.etfbl.korisnikbibliotekeaplikacija.logger.KorisnikLogger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class BookDetailsController {

    @FXML
    private TextArea contentTextArea;

    @FXML
    private ImageView pictureHolder;

    @FXML
    private Label title;

    public void setBookDetails(Book book) {
        //postavljanje naslova knjige
        title.setText(book.getTitle());
        //postavljanje slike knjige
        if (book.getPicturePath() != null && !book.getPicturePath().isEmpty()) {
            Image image = decodeImageFromBase64(book.getPicturePath());
            pictureHolder.setImage(image);
        }
        //postavljanje prvih 100 redova teksta
        contentTextArea.setText(getFirst100Lines(book.getContent()));
    }

    private String getFirst100Lines(String content) {
        String[] lines = content.split("\n");
        StringBuilder first100Lines = new StringBuilder();
        for (int i = 0; i < Math.min(100, lines.length); i++) {
            first100Lines.append(lines[i]).append("\n");
        }
        return first100Lines.toString();
    }

    private Image decodeImageFromBase64(String base64Image) {
        byte[] decodedBytes = Base64.getDecoder().decode(base64Image);
        ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
        return new Image(bis);
    }
}
