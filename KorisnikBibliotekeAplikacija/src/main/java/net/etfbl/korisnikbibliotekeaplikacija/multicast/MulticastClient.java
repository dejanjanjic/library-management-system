package net.etfbl.korisnikbibliotekeaplikacija.multicast;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import net.etfbl.korisnikbibliotekeaplikacija.logger.KorisnikLogger;
import net.etfbl.korisnikbibliotekeaplikacija.config.ConfigLoader;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastClient extends Thread{
    private ObservableList<String> messageList;

    public MulticastClient(ObservableList<String> messageList){
        this.messageList = messageList;
        setDaemon(true);
    }

    @Override
    public void run(){
        byte[] buf = new byte[1024];
        try (MulticastSocket socket = new MulticastSocket(Integer.parseInt(ConfigLoader.getInstance().getProperty("multicast.port"))) ){
            InetAddress groupAddress = InetAddress.getByName(ConfigLoader.getInstance().getProperty("multicast.address"));
            socket.joinGroup(groupAddress);
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                Platform.runLater(() -> messageList.add(received));
                System.out.println(received);
            }
        } catch (IOException ioe) {
            KorisnikLogger.logger.severe("Error: " + ioe);
        }
    }
}
