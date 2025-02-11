package net.etfbl.korisnikbibliotekeaplikacija.secure;


    import net.etfbl.korisnikbibliotekeaplikacija.config.ConfigLoader;
    import net.etfbl.korisnikbibliotekeaplikacija.model.Message;

    import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
    import java.io.IOException;
    import java.net.ServerSocket;
    import java.util.ArrayList;

public class ChatServer {

    public static void main(String[] args) throws IOException {
        System.setProperty("javax.net.ssl.keyStore", ConfigLoader.getInstance().getProperty("keystore.path"));
        System.setProperty("javax.net.ssl.keyStorePassword", ConfigLoader.getInstance().getProperty("keystore.password"));

        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        ServerSocket ss = ssf.createServerSocket(Integer.parseInt(ConfigLoader.getInstance().getProperty("keystore.port")));
        System.out.println("Server started");

        ArrayList<Message> messages = new ArrayList<>();
        Object lock = new Object();
        while (true) {
            SSLSocket s = (SSLSocket) ss.accept();
            new ChatServerThread(s, messages, lock).start();
        }
    }
}
