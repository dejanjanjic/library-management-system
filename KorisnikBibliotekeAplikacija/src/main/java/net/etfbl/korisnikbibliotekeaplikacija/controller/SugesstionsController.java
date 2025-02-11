package net.etfbl.korisnikbibliotekeaplikacija.controller;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import net.etfbl.korisnikbibliotekeaplikacija.config.ConfigLoader;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import net.etfbl.korisnikbibliotekeaplikacija.logger.KorisnikLogger;


public class SugesstionsController {
    @FXML
    private ListView<String> chatList;

    @FXML
    private TextField sugesstionField;
    private ObservableList<String> messageList;

    public void setMessageList(ObservableList<String> messageList) {
        this.messageList = messageList;
        chatList.setItems(messageList);

        messageList.addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    chatList.scrollTo(change.getFrom());
                }
            }
        });
    }

    @FXML
    void sendMessage(MouseEvent event) throws UnknownHostException {
        InetAddress addr = InetAddress.getByName(ConfigLoader.getInstance().getProperty("multicast.address"));
        System.out.println("Tekst: " + sugesstionField.getText());
        String message = LoginController.user.getUsername() + ": " + sugesstionField.getText();
        byte[] buf = message.getBytes();
        try (DatagramSocket socket = new DatagramSocket()) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length, addr, Integer.parseInt(ConfigLoader.getInstance().getProperty("multicast.port")));
            socket.send(packet);
        } catch (IOException ioe) {
            KorisnikLogger.logger.severe("Error: " + ioe);
        }
    }
}
