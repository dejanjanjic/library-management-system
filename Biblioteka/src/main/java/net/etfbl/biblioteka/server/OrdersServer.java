package net.etfbl.biblioteka.server;

import javafx.collections.ObservableList;
import net.etfbl.biblioteka.config.ConfigLoader;
import net.etfbl.biblioteka.model.Book;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import net.etfbl.biblioteka.logger.BibliotekaLogger;

public class OrdersServer extends Thread{
    private ObservableList<Book> booksList;

    public OrdersServer(ObservableList<Book> booksList){
        this.booksList = booksList;
        setDaemon(true);
    }

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(ConfigLoader.getInstance().getProperty("tcp.port")));
            while (true){
                Socket socket = serverSocket.accept();
                new OrdersServerThread(socket).start();
            }
        } catch (IOException e) {
            BibliotekaLogger.logger.severe("Error: " + e);
        }
    }
}
