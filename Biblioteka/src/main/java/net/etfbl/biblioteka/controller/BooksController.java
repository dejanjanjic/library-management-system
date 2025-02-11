package net.etfbl.biblioteka.controller;

import com.google.gson.Gson;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.etfbl.biblioteka.config.ConfigLoader;
import net.etfbl.biblioteka.gui.BibliotekaApplication;
import net.etfbl.biblioteka.model.Book;
import net.etfbl.biblioteka.logger.BibliotekaLogger;
import net.etfbl.biblioteka.logger.BibliotekaLogger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.Buffer;
import java.nio.charset.StandardCharsets;

public class BooksController {

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private ImageView deleteImageView;

    @FXML
    private TableColumn<Book, String> detailsColumn;

    @FXML
    private TableColumn<Book, Integer> idColumn;

    @FXML
    private TableColumn<Book, String> languageColumn;

    @FXML
    private ImageView plusImageView;

    @FXML
    private ImageView searchIconImageView;

    @FXML
    private TableColumn<Book, String> titleColumn;

    @FXML
    private TextField titleTextField;

    @FXML
    private TableView<Book> booksTable;

    @FXML
    private TableColumn<Book, String> yearColumn;
    private ObservableList<String> messageList;

    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));



        detailsColumn.setCellFactory(new Callback<TableColumn<Book, String>, TableCell<Book, String>>() {
            @Override
            public TableCell<Book, String> call(TableColumn<Book, String> param) {
                return new TableCell<Book, String>() {
                    private final Button button = new Button("Details");

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty) {
                            setGraphic(null);
                        } else {
                            button.setOnAction(event -> {
                                Book book = getTableView().getItems().get(getIndex());
                                Book fetchedBook = getBookByTitle(book.getTitle());

                                if (fetchedBook != null) {
                                    try {
                                        FXMLLoader loader = new FXMLLoader(BibliotekaApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.book-details")));
                                        Parent root = loader.load();

                                        BookDetailsController bookDetailsController = loader.getController();
                                        bookDetailsController.setBookDetails(fetchedBook);

                                        Stage stage = new Stage();
                                        stage.setTitle("Book Details");
                                        stage.setScene(new Scene(root));
                                        stage.show();
                                    } catch (IOException e) {
                                        BibliotekaLogger.logger.severe("Error: " + e);
                                    }
                                }
                            });

                            setGraphic(button);
                        }
                    }
                };
            }
        });


        authorColumn.setOnEditCommit(event -> {
            Book book = event.getRowValue();
            book.setAuthor(event.getNewValue());
            updateBook(book);
        });
        titleColumn.setOnEditCommit(event -> {
            Book book = event.getRowValue();
            book.setTitle(event.getNewValue());
            updateBook(book);
        });
        yearColumn.setOnEditCommit(event -> {
            Book book = event.getRowValue();
            book.setLanguage(event.getNewValue());
            updateBook(book);
        });
        languageColumn.setOnEditCommit(event -> {
            Book book = event.getRowValue();
            book.setLanguage(event.getNewValue());
            updateBook(book);
        });



        showAll();
    }

    private Book getBookByTitle(String title) {
        try{
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());

            URL bookURL = new URL(ConfigLoader.getInstance().getProperty("url.books") + "/" + encodedTitle);
            HttpURLConnection connection = (HttpURLConnection) bookURL.openConnection();
            connection.setRequestMethod("GET");


            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                //System.out.println("OVO treba");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                Gson gson = new Gson();
                Book book = gson.fromJson(br, Book.class);
                return book;
            }

            connection.disconnect();
        } catch (IOException e) {
            BibliotekaLogger.logger.severe("Error: " + e);
        }
        return null;
    }

    @FXML
    void showUsersWindow(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(BibliotekaApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.library-users")));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setScene(scene);
        stage.show();
    }

    private void updateBook(Book book) {
        try{
            URL acceptURL = new URL(ConfigLoader.getInstance().getProperty("url.books"));
            HttpURLConnection connection = (HttpURLConnection) acceptURL.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            Gson gson = new Gson();
            try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), true)){
                printWriter.println(gson.toJson(book));
                if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK){
                    throw new RuntimeException();
                }
            }
        }catch(IOException e){
            BibliotekaLogger.logger.severe("Error: " + e);
        }
    }

    private void showAll() {
        booksTable.setEditable(true);


        titleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        authorColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        yearColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        languageColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(ConfigLoader.getInstance().getProperty("url.books")).openStream()))) {
            booksTable.getItems().clear();

            Gson gson = new Gson();
            Book[] books = gson.fromJson(bufferedReader, Book[].class);

            booksTable.getItems().addAll(books);
        }catch (IOException e){
            BibliotekaLogger.logger.severe("Error: " + e);
        }

    }

    @FXML
    void addNewBook(MouseEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(BibliotekaApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.add-book")));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("eLibrary");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void deleteBook(MouseEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(BibliotekaApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.book-delete")));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("eLibrary");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void searchOnClick(MouseEvent event) {
        String title = titleTextField.getText();
        if(!title.isEmpty()) {
            searchByTitle(title);
        }else{
            booksTable.getItems().clear();
        }
    }

    private void searchByTitle(String title) {
        try{
            String encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());

            URL url = new URL(ConfigLoader.getInstance().getProperty("url.books") + "/" + encodedTitle);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    booksTable.getItems().clear();

                    Gson gson = new Gson();
                    Book book = gson.fromJson(bufferedReader, Book.class);
                    booksTable.getItems().add(book);


                }
            }else{
                booksTable.getItems().clear();
            }
        }catch (IOException e){
            BibliotekaLogger.logger.severe("Error: " + e);
        }
    }

    @FXML
    void searchOnEnter(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)){
            String title = titleTextField.getText();
            if (!title.isEmpty()) {
                searchByTitle(title);
            } else {
                booksTable.getItems().clear();
            }
        }
    }

    @FXML
    void showAllButtonClicked(MouseEvent event) {
        showAll();
    }


    @FXML
    void openSuggestions(MouseEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(BibliotekaApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.sugesstions")));
        Parent root = fxmlLoader.load();

        SuggestionsController suggestionsController = fxmlLoader.getController();
        suggestionsController.setMessageList(messageList);

        Scene scene = new Scene(root);
        stage.setTitle("eLibrary");
        stage.setScene(scene);
        stage.show();
    }

    public void setMessageList(ObservableList<String> messageList) {
        this.messageList = messageList;
    }

    @FXML
    void orderBooks(MouseEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(BibliotekaApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.suppliers")));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("eLibrary");
        stage.setScene(scene);
        stage.show();
    }
}
