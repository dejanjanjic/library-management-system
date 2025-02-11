package net.etfbl.biblioteka.server;

import java.io.*;
import java.net.Socket;
import net.etfbl.biblioteka.logger.BibliotekaLogger;

public class OrdersServerThread extends Thread{
    Socket socket;
    PrintWriter printWriter;
    BufferedReader bufferedReader;
    public OrdersServerThread(Socket socket) {
        this.socket = socket;
        try {
            printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            BibliotekaLogger.logger.severe("Error: " + e);
        }
    }

    @Override
    public void run() {
        try {
            socket.close();
        } catch (IOException e) {
            BibliotekaLogger.logger.severe("Error: " + e);
        }
    }
}
