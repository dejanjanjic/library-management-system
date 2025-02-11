package net.etfbl.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.etfbl.config.ConfigLoader;
import net.etfbl.logger.ServerLogger;
import net.etfbl.model.Book;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.Authenticator;


public class MailService {
	public boolean sendMail(String mailTo, ArrayList<Book> books) {
		    final String username = ConfigLoader.getInstance().getProperty("username");
		    final String password = ConfigLoader.getInstance().getProperty("password");
		    ArrayList<File> files = new ArrayList<>();

		    for (Book b : books) {
		        files.add(getFileWithBook(b, b.getId()));
		    }
		    
		    File zip = zipFiles(files);

		    Properties props = new Properties();
		    props.put("mail.smtp.auth", ConfigLoader.getInstance().getProperty("mail.smtp.auth"));
		    props.put("mail.smtp.starttls.enable", ConfigLoader.getInstance().getProperty("mail.smtp.starttls.enable"));
		    props.put("mail.smtp.host", ConfigLoader.getInstance().getProperty("mail.smtp.host"));
		    props.put("mail.smtp.port", ConfigLoader.getInstance().getProperty("mail.smtp.port"));

		    Session session = Session.getInstance(props, new Authenticator() {
		        protected PasswordAuthentication getPasswordAuthentication() {
		            return new PasswordAuthentication(username, password);
		        }
		    });

		    try {
		        Message message = new MimeMessage(session);
		        message.setFrom(new InternetAddress(username, ConfigLoader.getInstance().getProperty("mail.from")));
		        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mailTo));
		        message.setSubject(ConfigLoader.getInstance().getProperty("mail.subject"));
		        
		        MimeBodyPart textPart = new MimeBodyPart();
		        String text = "";
		        for (Book b : books) {
					text += b.toString();
				}
		        textPart.setText(text);

		        MimeBodyPart attachmentPart = new MimeBodyPart();
		        attachmentPart.attachFile(zip);

		        Multipart multipart = new MimeMultipart();
		        multipart.addBodyPart(textPart);
		        multipart.addBodyPart(attachmentPart);

		        message.setContent(multipart);

		        Transport.send(message);

		        System.out.println("Done");

		    }  catch (Exception e) {
		    	ServerLogger.logger.severe("Error: " + e);
		    }

		    return true;
		}
	

	private File zipFiles(ArrayList<File> files) {
		File file = new File(String.valueOf(System.currentTimeMillis()) + ".zip");
		try(ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(file))){
			for (File f : files) {
				try (FileInputStream fis = new FileInputStream(f)) {
                    ZipEntry zipEntry = new ZipEntry(f.getName());
                    zipOutputStream.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zipOutputStream.write(buffer, 0, len);
                    }

                    zipOutputStream.closeEntry();
                }
			}
		}catch (Exception e) {
			ServerLogger.logger.severe("Error: " + e);
		}
		return file;
	}

	private File getFileWithBook(Book book, int id) {
		File file = new File(String.valueOf(System.currentTimeMillis()) + "a" + id + ".txt");
		System.out.println("Putanja: " + file.getAbsolutePath());
		try(PrintWriter printWriter = new PrintWriter(new BufferedWriter(new FileWriter(file)))) {
			printWriter.println(book.toString());
		} catch (IOException e) {
			ServerLogger.logger.severe("Error: " + e);
			return null;
		}
		
		return file;
	}
}
