package net.etfbl.biblioteka.controller;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.etfbl.biblioteka.config.ConfigLoader;
import net.etfbl.biblioteka.gui.BibliotekaApplication;
import net.etfbl.biblioteka.model.Book;
import net.etfbl.biblioteka.logger.BibliotekaLogger;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class BooksBySupplierController {
    private String supplier;
    private ArrayList<Book> booksBySupplier;
    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book, Integer> idColumn;

    @FXML
    private TableView<Book> booksTable;

    @FXML
    private TableColumn<Book, Boolean> checkBookColumn;

    @FXML
    private TableColumn<Book, String> languageColumn;

    @FXML
    private TableColumn<Book, String> titleColumn;

    @FXML
    private Label titleLabel;

    @FXML
    private TableColumn<Book, String> yearColumn;

    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));

        checkBookColumn.setCellValueFactory(new PropertyValueFactory<>("selected"));
        // CheckBox kolona
        checkBookColumn.setCellFactory(tc -> {
            TableCell<Book, Boolean> cell = new TableCell<Book, Boolean>() {
                private final CheckBox checkBox = new CheckBox();

                @Override
                protected void updateItem(Boolean item, boolean empty) {
                    super.updateItem(item, empty);

                    if (empty) {
                        setGraphic(null);
                    } else {
                        Book book = getTableView().getItems().get(getIndex());
                        checkBox.setSelected(book.isSelected());

                        // Event listener za CheckBox
                        checkBox.setOnAction(event -> {
                            book.setSelected(checkBox.isSelected());
                        });

                        setGraphic(checkBox);
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    }
                }
            };

            // Sprečava selektovanje reda prilikom klika na CheckBox
            cell.setEditable(true);
            return cell;
        });
    }

    @FXML
    void orderBooks(MouseEvent event) {
        ArrayList<Book> selectedBooks = new ArrayList<Book>();
        for (Book b :
                booksTable.getItems()) {
            if (b.isSelected()) {
                selectedBooks.add(b);
            }
        }
        if(!selectedBooks.isEmpty()){
            sendOrder(selectedBooks);
        }
    }

    private void sendOrder(ArrayList<Book> selectedBooks) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(ConfigLoader.getInstance().getProperty("rabbitmq.address"));
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(supplier, false, false, false, null);
            String message = selectedBooks.stream()
                    .map(book -> String.valueOf(book.getId()))
                    .collect(Collectors.joining("#"));;
            channel.basicPublish("", supplier, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println(" [x] Sent '" + message + "'");
        } catch (IOException | TimeoutException e) {
            BibliotekaLogger.logger.severe("Error: " + e);
        }
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
        this.titleLabel.setText(titleLabel.getText() + " by " + supplier);
    }

    public void setBooksBySupplier(ArrayList<Book> booksBySupplier) {
        this.booksBySupplier = booksBySupplier;
        if(booksBySupplier != null){
            booksTable.getItems().addAll(FXCollections.observableArrayList(booksBySupplier));
        }
    }
}
