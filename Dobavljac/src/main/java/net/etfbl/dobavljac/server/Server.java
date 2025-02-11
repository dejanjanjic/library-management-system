package net.etfbl.dobavljac.server;

import net.etfbl.dobavljac.config.ConfigLoader;
import net.etfbl.dobavljac.logger.DobavljacLogger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(Integer.parseInt(ConfigLoader.getInstance().getProperty("server.port")));
            while(true){
                Socket socket = serverSocket.accept();
                new ServerThread(socket).start();
            }
        } catch (IOException e) {
            DobavljacLogger.logger.severe("Error: " + e);
        }
    }
}
