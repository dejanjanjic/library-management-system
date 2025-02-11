package net.etfbl.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.ArrayList;

import net.etfbl.config.ConfigLoader;
import net.etfbl.logger.ServerLogger;
import net.etfbl.model.User;

import java.beans.XMLEncoder;
import java.beans.XMLDecoder;

public class UserService {
	
	private String usersFileName = ConfigLoader.getInstance().getProperty("xml.users");
	private ArrayList<User> users = new ArrayList<>();

	public boolean register(User user) {
		File usersFile = new File(usersFileName);
		loadUsersFromXML();
		for (User u : users) {
			if(u.equals(user)) {
				return false;
			}
		}
		users.add(user);
		saveUsersToXML();
		return true;
	}
	

   
	public void saveUsersToXML() {
		
		try(XMLEncoder encoder = new XMLEncoder(new FileOutputStream(new File(usersFileName)))) {			
			encoder.writeObject(users);
		} catch (Exception e) {
			ServerLogger.logger.severe("Error: " + e);
		}
	}

	
	public void loadUsersFromXML() {
		try(XMLDecoder decoder = new XMLDecoder(new FileInputStream(new File(usersFileName)))) {			
			users = (ArrayList<User>) decoder.readObject();
		} catch (Exception e) {
			ServerLogger.logger.severe("Error: " + e);
		}
	
	}



	public ArrayList<User> getUsers() {
		loadUsersFromXML();
		ArrayList<User> activeUsers = new ArrayList<User>();
		for (User user : users) {
			if(user.getActivated() == true) {
				activeUsers.add(user);
			}
		}
		return activeUsers;
	}
	
	public User getUserByUsername(String username) {
		loadUsersFromXML();
		User user = null;
		for (User u : users) {
			if(u.getUsername().equals(username)) {
				user = u;
			}
		}
		return user;
	}
	
	public ArrayList<User> getRequests() {
		loadUsersFromXML();
		ArrayList<User> activeUsers = new ArrayList<User>();
		for (User user : users) {
			if(user.getActivated() == false) {
				activeUsers.add(user);
			}
		}
		return activeUsers;
	}



	public boolean acceptRequest(User user) {
		loadUsersFromXML();
		for (User u : users) {
			if(user.equals(u)) {
				u.setActivated(true);
				saveUsersToXML();
				return true;
			}
		}
		
		return false;
	}
	
	public boolean update(User user) {
		loadUsersFromXML();
		ArrayList<User> temp = new ArrayList<>(users);
		for (User u : temp) {
			if(user.equals(u)) {
				users.remove(u);
				users.add(user);
				saveUsersToXML();
				return true;
			}
		}
		
		return false;
	}




	public boolean delete(User user) {
		loadUsersFromXML();
		for (User u : users) {
			if(u.equals(user)) {
				users.remove(u);
				saveUsersToXML();
				return true;
			}
		}
		return false;
	}



	public User login(User user) {
		loadUsersFromXML();
		for(User u : users) {
			if(u.equals(user) && user.getPassword().equals(u.getPassword()) && u.getActivated() && !u.getBlocked()) {
				return u;
			}
		}
		return null;
	}



	public boolean deleteByUsername(String username) {
		loadUsersFromXML();
		for (User u : users) {
			if(u.getUsername().equals(username)) {
				users.remove(u);
				saveUsersToXML();
				return true;
			}
		}
		return false;
	}



	
}
