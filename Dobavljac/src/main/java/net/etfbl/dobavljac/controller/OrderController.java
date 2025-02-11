package net.etfbl.dobavljac.controller;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.etfbl.biblioteka.model.Book;
import net.etfbl.dobavljac.config.ConfigLoader;
import net.etfbl.dobavljac.logger.DobavljacLogger;
import net.etfbl.dobavljac.model.Bill;
import net.etfbl.dobavljac.service.IBookkeepingRMIService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

public class OrderController {
    @FXML
    private TextArea orderArea;
    private String supplierName;
    private ArrayList<Book> orderBooks;

    @FXML
    void acceptOrder(MouseEvent event) {
        for (Book b :
                orderBooks) {
            sendBook(b);
        }
        Bill bill = new Bill(orderBooks);
        double taxPrice = sendBill(bill);
        showTaxAlert(taxPrice);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public static void showTaxAlert(double taxPrice) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tax Information");
        alert.setHeaderText(null);
        alert.setContentText("The tax amount is: $" + String.format("%.2f", taxPrice));

        alert.showAndWait();
    }

    private double sendBill(Bill bill) {
        try {
            Registry registry = LocateRegistry.getRegistry(Integer.parseInt(ConfigLoader.getInstance().getProperty("rmi.port")));
            IBookkeepingRMIService bookkeepingRMIService = (IBookkeepingRMIService) registry.lookup(ConfigLoader.getInstance().getProperty("bookkeeping.rmi.class.name"));
            double taxPrice = bookkeepingRMIService.saveBill(bill);
            return taxPrice;
        } catch (RemoteException | NotBoundException e) {
            DobavljacLogger.logger.severe("Error: " + e);
        }
        return 0;
    }

    private void sendBook(Book b) {
        try {
            URL bookURL = new URL(ConfigLoader.getInstance().getProperty("url.books"));
            HttpURLConnection connection = (HttpURLConnection) bookURL.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            Gson gson = new Gson();
            try(PrintWriter printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), true)){

                printWriter.println(gson.toJson(b));
                if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                    System.out.println("Uspjesno");
                } else{
                    System.out.println("Neuspjesno");
                }
            }

        }catch (IOException e){
            DobavljacLogger.logger.severe("Error: " + e);
        }
    }


    @FXML
    void declineOrder(MouseEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public void setOrderBooks(ArrayList<Book> orderBooks) {
        this.orderBooks = orderBooks;
        for (Book b :
                orderBooks) {
            orderArea.setText(orderArea.getText() + b.getId() + " " + b.getTitle() + "\n");
            System.out.println(orderArea.getText() + b.getId() + " " + b.getTitle() + "\n");
        }
    }
}
