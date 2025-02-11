package net.etfbl.korisnikbibliotekeaplikacija.controller;

import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import net.etfbl.korisnikbibliotekeaplikacija.config.ConfigLoader;
import net.etfbl.korisnikbibliotekeaplikacija.model.Message;
import net.etfbl.korisnikbibliotekeaplikacija.model.User;
import net.etfbl.korisnikbibliotekeaplikacija.logger.KorisnikLogger;


import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatController {
    @FXML
    private TextField messageField;

    @FXML
    private ListView<String> messagesList;

    @FXML
    private ListView<String> usersList;
    
    private User user;
    private String usernameTo;

    {
        System.setProperty("javax.net.ssl.trustStore", ConfigLoader.getInstance().getProperty("keystore.path"));
        System.setProperty("javax.net.ssl.trustStorePassword", ConfigLoader.getInstance().getProperty("keystore.password"));
    }

    @FXML
    void openUserChat(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String selectedUser = usersList.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                this.usernameTo = selectedUser;
                ArrayList<Message> messages = getMessages(user.getUsername(), selectedUser);
                if(messages != null){
                    messagesList.getItems().clear();
                    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
                    for (Message m :
                            messages) {
                        if (m.getUsernameFrom().equals(user.getUsername())) {
                            messagesList.getItems().add("Me: " + m.getContent() + "     " + formatter.format(m.getCreateDate()));
                        }else{
                            messagesList.getItems().add(usernameTo + ": " + m.getContent() + "      " + formatter.format(m.getCreateDate()));
                        }
                        }
                }

            }
        }
    }

    private ArrayList<Message> getMessages(String firstUser, String secondUser) {

        SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try(SSLSocket s = (SSLSocket) sf.createSocket(ConfigLoader.getInstance().getProperty("keystore.host"), Integer.parseInt(ConfigLoader.getInstance().getProperty("keystore.port")));
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream())){
            oos.writeObject("MESSAGES#" + firstUser + "#" + secondUser);
            ArrayList<Message> messages = (ArrayList<Message>) ois.readObject();
            return messages;
        }catch (IOException | ClassNotFoundException e){
            KorisnikLogger.logger.severe("Error: " + e);
        }
        return null;
    }

    @FXML
    void sendMessage(MouseEvent event) {
        String content = messageField.getText();
        Message message = new Message(user.getUsername(), usernameTo, content, new Date());
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm");
        messagesList.getItems().add("Me: " + message.getContent() + "     " + formatter.format(message.getCreateDate()));
        SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
        try(SSLSocket s = (SSLSocket) sf.createSocket(ConfigLoader.getInstance().getProperty("keystore.host"), Integer.parseInt(ConfigLoader.getInstance().getProperty("keystore.port")));
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream())){
            oos.writeObject("SEND");
            oos.writeObject(message);
            //ArrayList<Message> messages = (ArrayList<Message>) ois.readObject();

        }catch (IOException e){
            KorisnikLogger.logger.severe("Error: " + e);
        }

    }

    public void setUser(User user) {
        this.user = user;
        setOtherUsers();
    }

    private void setOtherUsers() {
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new URL(ConfigLoader.getInstance().getProperty("url.users")).openStream()))) {
            usersList.getItems().clear();

            Gson gson = new Gson();
            User[] users = gson.fromJson(bufferedReader, User[].class);

            ArrayList<String> usernames = new ArrayList<>();
            for (User u :
                    users) {
                if(!u.getUsername().equals(this.user.getUsername())){
                    usernames.add(u.getUsername());
                }
            }
            usersList.getItems().addAll(usernames);
        }catch (IOException e){
            KorisnikLogger.logger.severe("Error: " + e);
        }
    }
}
