package net.etfbl.biblioteka.controller;

import com.google.gson.Gson;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.IntegerStringConverter;
import net.etfbl.biblioteka.config.ConfigLoader;
import net.etfbl.biblioteka.gui.BibliotekaApplication;
import net.etfbl.biblioteka.model.User;
import net.etfbl.biblioteka.logger.BibliotekaLogger;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BibliotekaController {
    @FXML
    private TableColumn<User, String> addressColumn;

    @FXML
    private ImageView deleteImageView;

    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> firstNameColumn;

    @FXML
    private TableColumn<User, String> lastNameColumn;

    @FXML
    private ImageView plusImageView;

    @FXML
    private ImageView searchIconImageView;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TableColumn<User, String> usernameColumn;

    @FXML
    private TableColumn<User, String> blockedColumn;

    @FXML
    private TableView<User> usersTable;

    private ObservableList<String> messageList;


    public void initialize() {
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));


        // Postavljanje dugmića u blockedColumn
        blockedColumn.setCellFactory(new Callback<TableColumn<User, String>, TableCell<User, String>>() {
            @Override
            public TableCell<User, String> call(TableColumn<User, String> param) {
                return new TableCell<User, String>() {
                    private final Button button = new Button();

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (empty || getTableRow() == null) {
                            setGraphic(null);
                            return;
                        }

                        User user = getTableView().getItems().get(getIndex());

                        // Postavi tekst dugmeta na osnovu toga da li je korisnik blokiran
                        if (user.getBlocked()) {
                            button.setText("Unblock");
                        } else {
                            button.setText("Block");
                        }

                        // Postavi akciju na dugme
                        button.setOnAction(event -> {
                            // Promeni status blocked korisnika
                            user.setBlocked(!user.getBlocked());

                            // Ažuriraj tekst dugmeta
                            if (user.getBlocked()) {
                                button.setText("Unblock");
                            } else {
                                button.setText("Block");
                            }

                            // Ovde možeš ažurirati status u bazi ili na serveru
                            updateUser(user);
                        });

                        setGraphic(button);
                    }
                };
            }
        });


        firstNameColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setFirstName(event.getNewValue());
            updateUser(user);
        });
        lastNameColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setLastName(event.getNewValue());
            updateUser(user);
        });
        addressColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setAddress(event.getNewValue());
            updateUser(user);
        });
        emailColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setEmail(event.getNewValue());
            updateUser(user);
        });



        showAll();
    }

    @FXML
    void showBooksWindow(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(BibliotekaApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.books")));
        Parent root = fxmlLoader.load();

        BooksController booksController = fxmlLoader.getController();
        booksController.setMessageList(messageList);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    private void updateUser(User user) {
        try{
            URL acceptURL = new URL(ConfigLoader.getInstance().getProperty("url.users"));
            HttpURLConnection connection = (HttpURLConnection) acceptURL.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            Gson gson = new Gson();
            try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), true)){
                printWriter.println(gson.toJson(user));
                if(connection.getResponseCode()!=HttpURLConnection.HTTP_OK){
                    throw new RuntimeException();
                }
            }
        }catch(IOException e){
            BibliotekaLogger.logger.severe("Error: " + e);
        }
    }

    private void showAll() {
        usersTable.setEditable(true);


        firstNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        lastNameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        addressColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        emailColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(ConfigLoader.getInstance().getProperty("url.users")).openStream()))) {
            usersTable.getItems().clear();

            Gson gson = new Gson();
            User[] users = gson.fromJson(bufferedReader, User[].class);

            usersTable.getItems().addAll(users);
        }catch (IOException e){
            BibliotekaLogger.logger.severe("Error: " + e);
        }

    }

    @FXML
    void addNewUser(MouseEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(BibliotekaApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.register")));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("eLibrary");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void deleteUser(MouseEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(BibliotekaApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.user-delete")));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("eLibrary");
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    void searchOnClick(MouseEvent event) {
        String username = usernameTextField.getText();
        if(!username.isEmpty()) {
            searchByUsername(username);
        }else{
            usersTable.getItems().clear();
        }
    }

    private void searchByUsername(String username) {
        try{
            URL url = new URL(ConfigLoader.getInstance().getProperty("url.users") + "/" + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            if(connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    usersTable.getItems().clear();

                    Gson gson = new Gson();
                    User user = gson.fromJson(bufferedReader, User.class);
                    usersTable.getItems().add(user);


                }
            }else{
                usersTable.getItems().clear();
            }
        }catch (IOException e){
            BibliotekaLogger.logger.severe("Error: " + e);
        }
    }

    @FXML
    void searchOnEnter(KeyEvent event) {
        if(event.getCode().equals(KeyCode.ENTER)){
            String username = usernameTextField.getText();
            if (!username.isEmpty()) {
                searchByUsername(username);
            } else {
                usersTable.getItems().clear();
            }
        }
    }

    @FXML
    void showAllButtonClicked(MouseEvent event) {
        showAll();
    }
    @FXML
    void showRequests(MouseEvent event) {
        usersTable.setEditable(false);
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(ConfigLoader.getInstance().getProperty("url.requests")).openStream()))) {
            usersTable.getItems().clear();

            Gson gson = new Gson();
            User[] users = gson.fromJson(bufferedReader, User[].class);
            usersTable.getItems().addAll(users);

            usersTable.setOnMouseClicked(event1 -> {
                if (event1.getClickCount() == 2) {
                    User selectedUser = usersTable.getSelectionModel().getSelectedItem();
                    if (selectedUser != null) {
                        showConfirmationDialog(selectedUser, null); // Prikazi prozor
                    }
                }
            });
        }catch (IOException e){
            BibliotekaLogger.logger.severe("Error: " + e);
        }
    }

    private void showConfirmationDialog(User user, TableRow<User> row) {


        // Kreiraj prozor
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.setTitle("Accept Request");

        // Postavi vlasnika prozora
        dialogStage.initOwner(usersTable.getScene().getWindow());

        // Kreiraj label i dugmad
        Label messageLabel = new Label("Accept request for " + user.getFirstName() + "?");

        Button yesButton = new Button("Yes");
        Button noButton = new Button("No");

        // Dodaj akcije za dugmad
        yesButton.setOnAction(event -> {
            // Izbriši korisnika iz tabele
            user.setActivated(true);
            updateUser(user);
            usersTable.getItems().remove(user);
            dialogStage.close(); // Zatvori prozor
        });

        noButton.setOnAction(event -> {

            usersTable.getItems().remove(user);
            delete(user);
            dialogStage.close();
        });

        // Layout za prozor
        HBox buttonsBox = new HBox(10, yesButton, noButton);
        buttonsBox.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(10, messageLabel, buttonsBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(20));

        Scene dialogScene = new Scene(vbox);
        dialogStage.setScene(dialogScene);

        // Prikazi prozor
        dialogStage.show();
        System.out.println("Prozor prikazan.");
    }

    private void delete(User user) {
        try{
            URL acceptURL = new URL(ConfigLoader.getInstance().getProperty("url.delete"));
            HttpURLConnection connection = (HttpURLConnection) acceptURL.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");
            Gson gson = new Gson();
            try (PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), true)){
                printWriter.println(gson.toJson(user));
                if(connection.getResponseCode()!=HttpURLConnection.HTTP_NO_CONTENT){
                    throw new RuntimeException();
                }
            }
        }catch(IOException e){
            BibliotekaLogger.logger.severe("Error: " + e);
        }
    }

    public void setMessageList(ObservableList<String> messageList) {
        this.messageList = messageList;
    }
}

