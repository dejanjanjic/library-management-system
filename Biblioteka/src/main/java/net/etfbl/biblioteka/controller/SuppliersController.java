package net.etfbl.biblioteka.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.etfbl.biblioteka.config.ConfigLoader;
import net.etfbl.biblioteka.gui.BibliotekaApplication;
import net.etfbl.biblioteka.model.Book;
import net.etfbl.biblioteka.logger.BibliotekaLogger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class SuppliersController {
    @FXML
    private ListView<String> suppliersListView;

    @FXML
    public void initialize() throws UnknownHostException {
        loadSuppliersFromServer();
    }

    private void loadSuppliersFromServer() throws UnknownHostException {
        InetAddress address = InetAddress.getByName(ConfigLoader.getInstance().getProperty("server.address"));
        int port = Integer.parseInt(ConfigLoader.getInstance().getProperty("server.port"));
        try(Socket socket = new Socket(address, port);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())){
            oos.writeObject("SUPPLIERS");
            ArrayList<String> suppliersList = (ArrayList<String>) ois.readObject();
            suppliersListView.setItems(FXCollections.observableArrayList(suppliersList));
        }catch (Exception e){
            BibliotekaLogger.logger.severe("Error: " + e);
        }
    }


    @FXML
    void openSupplierWindow(MouseEvent event) throws IOException {
        if (event.getClickCount() == 2) {
            String supplier = suppliersListView.getSelectionModel().getSelectedItem();
            if (supplier != null) {
                ArrayList<Book> books = getBooksFromSupplier(supplier);
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(BibliotekaApplication.class.getResource(ConfigLoader.getInstance().getProperty("fxml.books-by-supplier")));
                Parent root = fxmlLoader.load();

                BooksBySupplierController booksBySupplierController = fxmlLoader.getController();
                booksBySupplierController.setSupplier(supplier);
                booksBySupplierController.setBooksBySupplier(books);

                Scene scene = new Scene(root);
                stage.setTitle("eLibrary");
                stage.setScene(scene);
                stage.show();
            }
        }

    }

    private ArrayList<Book> getBooksFromSupplier(String supplier) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(ConfigLoader.getInstance().getProperty("server.address"));
        int port = Integer.parseInt(ConfigLoader.getInstance().getProperty("server.port"));
        try(Socket socket = new Socket(address, port);
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream())){
            oos.writeObject("BOOKS#" + supplier);
            return (ArrayList<Book>) ois.readObject();
        }catch (Exception e){
            BibliotekaLogger.logger.severe("Error: " + e);
        }
        return null;
    }
}
