package net.etfbl.dobavljac.server;

import com.google.gson.reflect.TypeToken;
import net.etfbl.dobavljac.app.DobavljacApplication;
import net.etfbl.dobavljac.config.ConfigLoader;
import net.etfbl.biblioteka.model.Book;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import com.google.gson.Gson;
import net.etfbl.dobavljac.logger.DobavljacLogger;

public class ServerThread extends Thread{
    private Socket socket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private ArrayList<Book> books = new ArrayList<>();

    public ServerThread(Socket socket){
        this.socket = socket;
        try {
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());

        } catch (IOException e) {
            DobavljacLogger.logger.severe("Error: " + e);
        }

    }

    @Override
    public void run(){
        try{
            String request = (String)ois.readObject();
            if(request.startsWith("ADD")){
                String[] parts = request.split("#");
                String directoryName = ConfigLoader.getInstance().getProperty("directory.path") + parts[1];
                System.out.println(parts[1]);
                File supplierDirectory = new File(directoryName);
                File jsonFile = new File(supplierDirectory, "books.json");
                if(!supplierDirectory.exists()){
                    supplierDirectory.mkdir();
                }


                Book book = new Book(Integer.parseInt(parts[2]), parts[3], parts[4], parts[5], parts[6], parts[7], parts[8]);
                books = loadBooksFromJson(jsonFile);
                books.add(book);

                Gson gson = new Gson();
                try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(jsonFile)), true)){
                    pw.println(gson.toJson(books));
                }catch (IOException e){
                    oos.writeObject("NOK");
                }
                oos.writeObject("OK");
            }else if("LOAD".equals(request)){
                ArrayList<Book> books = loadBooksFromLinks();
                oos.writeObject(books);
            } else if ("SUPPLIERS".equals(request)) {
                File directory = new File(ConfigLoader.getInstance().getProperty("directory.path"));
                ArrayList<String> suppliers = new ArrayList<>();
                for (File f :
                        Objects.requireNonNull(directory.listFiles())) {
                    suppliers.add(f.getName());
                }
                oos.writeObject(suppliers);
            } else if (request.startsWith("BOOKS")){
                System.out.println("Dosao na serversku stranu");;
                String username = request.split("#")[1];
                String directoryName = ConfigLoader.getInstance().getProperty("directory.path") + username;
                File supplierDirectory = new File(directoryName);
                File jsonFile = new File(supplierDirectory, "books.json");
                if(!supplierDirectory.exists()){
                    supplierDirectory.mkdir();
                }
                ArrayList<Book> books = loadBooksFromJson(jsonFile);
                ArrayList<Book> sharedBooks = loadBooksFromLinks();
                for (Book b :
                        sharedBooks) {
                    books.add(b);
                }
                oos.writeObject(books);
            } else if (request.startsWith("GET")){
                String[] parts = request.split("#");
                String username = parts[1];
                String title = parts[2];
                String directoryName = ConfigLoader.getInstance().getProperty("directory.path") + username;
                File supplierDirectory = new File(directoryName);
                File jsonFile = new File(supplierDirectory, "books.json");

                ArrayList<Book> books = loadBooksFromJson(jsonFile);
                ArrayList<Book> sharedBooks = loadBooksFromLinks();

                for (Book b :
                        sharedBooks) {
                    books.add(b);
                }
                for (Book b :
                        books) {
                    if (b.getTitle().equals(title)) {
                        oos.writeObject(b);
                        break;
                    }
                    }

            }
            ois.close();
            oos.close();
            socket.close();
        }catch (Exception e){
            DobavljacLogger.logger.severe("Error: " + e);
        }
    }

    private ArrayList<Book> loadBooksFromJson(File jsonFile) throws IOException {
        System.out.println("Ucitavanje JSONa");
        Gson gson = new Gson();
        if(!jsonFile.exists()){
            System.out.println("Ovdje ako nema json file");
            jsonFile.createNewFile();
            ArrayList<Book> temp = new ArrayList<>();
            try(PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(jsonFile)), true)){
                pw.println(gson.toJson(temp));
            }catch (IOException e){
                DobavljacLogger.logger.severe("ERROR: " + e);
            }
            return temp;
        }

        System.out.println("Ovdje samo ako ima fajl");
        // Kreiraj Type za listu Book objekata
        Type bookListType = new TypeToken<ArrayList<Book>>() {}.getType();

        FileReader reader = new FileReader(jsonFile);
        ArrayList<Book> books = gson.fromJson(reader, bookListType);
        reader.close();
        return books;
    }
    
    private ArrayList<Book> loadBooksFromLinks(){
        try {
            List<String> lines = Files.readAllLines(Paths.get(DobavljacApplication.class.getResource(ConfigLoader.getInstance().getProperty("links.file-name")).toURI()));
            ArrayList<Book> books = new ArrayList<>();
            for (String line :
                    lines) {
                int id = Integer.parseInt(line.split("/")[5]);
                String picturePath = line.substring(0, line.length() - 4) + ".cover.medium.jpg";
                String base64Image = getBase64FromURL(picturePath);
                //System.out.println("KOD: " + base64Image);
                String author ="", title="", year="", language="", content="";
                URL url = new URL(line);
                try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()))){
                    String l;
                    while((l = bufferedReader.readLine())!= null){
                        content += l + "\n";
                        if(l.startsWith("Title: ")){
                            title = l.substring(l.indexOf(" ") + 1);
                        }else if(l.startsWith("Author: ")){
                            author = l.substring(l.indexOf(" ") + 1);
                        }else if(l.startsWith("Language: ")){
                            language = l.substring(l.indexOf(" ") + 1);
                        }else if(l.startsWith("Release date: ")){
                            year = l.substring(l.indexOf(" ", 10) + 1);
                            int index = year.indexOf("[");
                            if (index != -1) {
                                year = year.substring(0, index).trim();
                            }
                        }
                    }
                }
               // System.out.println(new Book(id, title, author, year, language, picturePath, content));
                books.add(new Book(id, title, author, year, language, base64Image, content));
                //oos.writeObject(books);
            }
            return books;
        } catch (IOException | URISyntaxException e) {
            DobavljacLogger.logger.severe("Error: " + e);
        }
        return books;
    }

    public static String getBase64FromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);

            InputStream inputStream = url.openStream();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }


            byte[] imageBytes = byteArrayOutputStream.toByteArray();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);


            inputStream.close();
            byteArrayOutputStream.close();

            return base64Image;

        } catch (Exception e) {
            DobavljacLogger.logger.severe("Error: " + e);
            return null;
        }
    }
}
