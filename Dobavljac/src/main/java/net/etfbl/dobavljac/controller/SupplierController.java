package net.etfbl.dobavljac.controller;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import net.etfbl.dobavljac.app.DobavljacApplication;
import net.etfbl.dobavljac.config.ConfigLoader;
import net.etfbl.biblioteka.model.Book;
import net.etfbl.dobavljac.logger.DobavljacLogger;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class SupplierController {
    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableView<Book> booksTable;

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
    private Label titleLabel;

    @FXML
    private TextField titleTextField;

    @FXML
    private TableColumn<Book, String> yearColumn;

    @FXML
    private Label statusLabel;
    private String supplierName;

    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        languageColumn.setCellValueFactory(new PropertyValueFactory<>("language"));


        // Postavljanje dugmića u blockedColumn
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


                        // Postavi akciju na dugme
                        button.setOnAction(event -> {
                            try {
                                // Učitavanje FXML-a
                                FXMLLoader loader = new FXMLLoader(DobavljacApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.book-details")));
                                Parent root = loader.load();

                                // Dobavljanje instance kontrolera
                                BookDetailsController bookDetailsController = loader.getController();

                                // Prosleđivanje podataka o knjizi kontroleru
                                bookDetailsController.setBookDetails(book);

                                // Kreiranje novog prozora
                                Stage stage = new Stage();
                                stage.setTitle("Book Details");
                                stage.setScene(new Scene(root));
                                stage.show();
                            } catch (IOException e) {
                                DobavljacLogger.logger.severe("Error: " + e);
                            }
                        });

                        setGraphic(button);
                    }
                };
            }
        });




    }

    private void showAll() {
        booksTable.getItems().clear();
        //initFromLinkovi();
        initFromFile();
    }
    private void initFromLinkovi() {
        try {
            InetAddress address = InetAddress.getLocalHost();
            int port = Integer.parseInt(ConfigLoader.getInstance().getProperty("server.port"));

            try(Socket socket = new Socket(address, port);
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())){
                oos.writeObject("LOAD");
                ArrayList<Book> response = (ArrayList<Book>) ois.readObject();
                //booksTable.getItems().addAll(response);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void showAllButtonClicked(MouseEvent event) {
        showAll();
    }

    private void initFromFile() {
        try {
            InetAddress address = InetAddress.getByName(ConfigLoader.getInstance().getProperty("server.address"));
            int port = Integer.parseInt(ConfigLoader.getInstance().getProperty("server.port"));

            try(Socket socket = new Socket(address, port);
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())){
                System.out.println("Dosao do slanja zahtjeva");
                oos.writeObject("BOOKS#" + supplierName);
                ArrayList<Book> books = (ArrayList<Book>) ois.readObject();
                System.out.println("Primio sve dobro");
                booksTable.getItems().addAll(books);
            }

        } catch (Exception e) {
            DobavljacLogger.logger.severe("Error: " + e);
        }
    }

    @FXML
    void addNewBook(MouseEvent event) throws IOException {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(DobavljacApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.add-book")));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("eLibrary");

        AddBookController controller = fxmlLoader.getController();
        controller.setSupplierName(supplierName);

        stage.setScene(scene);
        stage.show();
    }


    @FXML
    void checkForOrders(MouseEvent event) throws IOException {
        String message = readMessage();
        System.out.println("Message = " + message);
        if(message == null){
            statusLabel.setVisible(true);
        }else{
            ArrayList<Book> orderBooks = new ArrayList<>();
            String[] parts = message.split("#");
            ObservableList<Book> supplierBooks = booksTable.getItems();
            for (String s :
                    parts) {
                int id = Integer.parseInt(s);
                for (Book b :
                        supplierBooks) {
                    if (b.getId() == id) {

                        orderBooks.add(b);
                    }
                    }
            }
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(DobavljacApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.order")));
            Parent root = fxmlLoader.load();

            OrderController orderController = fxmlLoader.getController();
            orderController.setSupplierName(supplierName);
            orderController.setOrderBooks(orderBooks);

            Scene scene = new Scene(root);
            stage.setTitle("eLibrary");
            stage.setScene(scene);
            stage.show();
        }
    }

    private String readMessage() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(ConfigLoader.getInstance().getProperty("rabbitmq.address"));

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(supplierName, false, false, false, null);

            GetResponse response = channel.basicGet(supplierName, true);
            if (response != null) {
                return new String(response.getBody(), "UTF-8");
            } else {
                return null;
            }
        }catch (IOException | TimeoutException e){
            DobavljacLogger.logger.severe("Error: " + e);
            return null;
        }
    }

    public void setSupplierName(String supplierName){
        this.supplierName = supplierName;
        titleLabel.setText(titleLabel.getText() + " by " + this.supplierName);
        showAll();
    }
}
