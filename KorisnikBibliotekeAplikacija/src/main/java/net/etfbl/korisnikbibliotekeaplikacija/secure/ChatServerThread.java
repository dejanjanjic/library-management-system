package net.etfbl.korisnikbibliotekeaplikacija.secure;

import net.etfbl.korisnikbibliotekeaplikacija.model.Message;

import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import net.etfbl.korisnikbibliotekeaplikacija.logger.KorisnikLogger;

public class ChatServerThread extends Thread{
    SSLSocket s;
    ObjectOutputStream oos;
    ObjectInputStream ois;
    ArrayList<Message> messages;
    Object lock;
    public ChatServerThread(SSLSocket s, ArrayList<Message> messages, Object lock) {
        this.s = s;
        this.messages = messages;
        this.lock = lock;
        try{
            oos = new ObjectOutputStream(s.getOutputStream());
            ois = new ObjectInputStream(s.getInputStream());
        }catch (IOException e){
            KorisnikLogger.logger.severe("Error: " + e);
        }
    }

    @Override
    public void run(){
        try{
            String request = (String)ois.readObject();
            if(request.startsWith("MESSAGES")){
                String[] parts = request.split("#");
                String firstUsername = parts[1], secondUsername = parts[2];
                ArrayList<Message> chatMessages = new ArrayList<>();
                synchronized (lock){
                    for (Message m :
                            messages) {
                        System.out.println(m.getUsernameFrom() + " " + m.getUsernameTo() + " " + m.getContent());
                        if((m.getUsernameFrom().equals(firstUsername) && m.getUsernameTo().equals(secondUsername)) ||
                            m.getUsernameFrom().equals(secondUsername) && m.getUsernameTo().equals(firstUsername)){
                            chatMessages.add(m);
                        }
                    }
                    oos.writeObject(chatMessages);
                }
            }else if(request.startsWith("SEND")){
                Message message = (Message) ois.readObject();
                synchronized (lock){
                    messages.add(message);
                }
            }
            ois.close();
            oos.close();
            s.close();
        }catch (Exception e){
            KorisnikLogger.logger.severe("Error: " + e);
        }
    }
}
