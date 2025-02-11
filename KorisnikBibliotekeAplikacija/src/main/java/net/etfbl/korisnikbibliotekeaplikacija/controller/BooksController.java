package net.etfbl.korisnikbibliotekeaplikacija.controller;

import com.google.gson.Gson;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.etfbl.korisnikbibliotekeaplikacija.config.ConfigLoader;
import net.etfbl.korisnikbibliotekeaplikacija.gui.KorisnikApplication;
import net.etfbl.korisnikbibliotekeaplikacija.model.Book;
import net.etfbl.korisnikbibliotekeaplikacija.model.User;
import net.etfbl.korisnikbibliotekeaplikacija.logger.KorisnikLogger;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class BooksController {

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableView<Book> booksTable;

    @FXML
    private TableColumn<Book, Boolean> checkBookColumn;

    @FXML
    private TableColumn<Book, String> detailsColumn;


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
    private TableColumn<Book, String> yearColumn;
    private ObservableList<String> messageList;

    public void initialize() {
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
                        checkBox.setSelected(book.getSelected());

                        // Event listener za CheckBox
                        checkBox.setOnAction(event -> {
                            book.setSelected(checkBox.isSelected());
                        });

                        setGraphic(checkBox);
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    }
                }
            };

            cell.setEditable(true);
            return cell;
        });

        booksTable.setEditable(true);

        detailsColumn.setCellFactory(new Callback<TableColumn<Book, String>, TableCell<Book, String>>() {
            @Override
            public TableCell<Book, String> call(TableColumn<Book, String> param) {
                return new TableCell<Book, String>() {
                    private final Button button = new Button("Details");

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || getTableRow() == null) {
                            setGraphic(null);
                            return;
                        }

                        Book book = getTableView().getItems().get(getIndex());

                        button.setOnAction(event -> {
                            try {

                                FXMLLoader loader = new FXMLLoader(KorisnikApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.book-details")));
                                Parent root = loader.load();


                                BookDetailsController bookDetailsController = loader.getController();

                                bookDetailsController.setBookDetails(book);

                                Stage stage = new Stage();
                                stage.setTitle("Book Details");
                                stage.setScene(new Scene(root));
                                stage.show();
                            } catch (IOException e) {
                                KorisnikLogger.logger.severe("Error: " + e);
                            }
                        });

                        setGraphic(button);
                    }
                };
            }
        });

        showAll();
    }

    private void showAll() {

        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(ConfigLoader.getInstance().getProperty("url.books")).openStream()))) {
            booksTable.getItems().clear();

            Gson gson = new Gson();
            Book[] books = gson.fromJson(bufferedReader, Book[].class);

            booksTable.getItems().addAll(books);
        }catch (IOException e){
            KorisnikLogger.logger.severe("Error: " + e);
        }

    }
    @FXML
    void downloadBooks(MouseEvent event) {
        ArrayList<Book> selectedBooks = new ArrayList<Book>();
        for (Book b :
                booksTable.getItems()) {
            if (b.getSelected()) {
                selectedBooks.add(b);
            }
        }
        if(!selectedBooks.isEmpty()) {
            String mailTo = LoginController.user.getEmail();
            new Thread(() -> {
                try {
                    URL url = new URL(ConfigLoader.getInstance().getProperty("url.mail") + mailTo);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json");

                    Gson gson = new Gson();
                    try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), true)) {
                        printWriter.println(gson.toJson(selectedBooks));
                    }
                    if (connection.getResponseCode() != 200) {
                        Alert alert = new Alert(Alert.AlertType.ERROR); // ERROR alert type
                        alert.setTitle("Error");
                        alert.setHeaderText(null);
                        alert.setContentText("Unsuccessful operation");

                        alert.showAndWait();
                    }
                    connection.disconnect();
                } catch (IOException e) {
                    KorisnikLogger.logger.severe("Error: " + e);
                }
            }).start();
        }

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
            URL url = new URL(ConfigLoader.getInstance().getProperty("url.books") + "/" + title);
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
            KorisnikLogger.logger.severe("Error: " + e);
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
    void enterChat(MouseEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(KorisnikApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.chat")));
        Parent root = fxmlLoader.load();

        ChatController chatController = fxmlLoader.getController();
        chatController.setUser(LoginController.user);

        Scene scene = new Scene(root);
        stage.setTitle("eLibrary");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void showAllButtonClicked(MouseEvent event) {
        showAll();
    }

    public void setMessageList(ObservableList<String> messageList) {
        this.messageList = messageList;
    }

    @FXML
    void showSugesstions(MouseEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(KorisnikApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.sugesstions")));
        Parent root = fxmlLoader.load();

        SugesstionsController suggestionsController = fxmlLoader.getController();
        suggestionsController.setMessageList(messageList);

        Scene scene = new Scene(root);
        stage.setTitle("eLibrary");
        stage.setScene(scene);
        stage.show();
    }
}
