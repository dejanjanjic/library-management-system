package net.etfbl.model;

import java.util.Objects;

public class User {
	private String firstName;
	private String lastName;
	private String address;
	private String email;
	private String username;
	private String password;
	private Boolean activated;
	private Boolean blocked;
	
	
	public User() {
		super();
	}


	public User(String firstName, String lastName, String address, String email, String username,
			String password, Boolean activated, Boolean blocked) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.email = email;
		this.username = username;
		this.password = password;
		this.activated = activated;
		this.blocked = blocked;
	}
	public User(String firstName, String lastName, String address, String email, String username,
			String password) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.address = address;
		this.email = email;
		this.username = username;
		this.password = password;
		this.activated = false;
		this.blocked = false;
	}



	public String getFirstName() {
		return firstName;
	}


	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}


	public String getLastName() {
		return lastName;
	}


	public void setLastName(String lastName) {
		this.lastName = lastName;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public Boolean getActivated() {
		return activated;
	}


	public void setActivated(Boolean activated) {
		this.activated = activated;
	}


	@Override
	public int hashCode() {
		return Objects.hash(username);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(username, other.username);
	}


	public Boolean getBlocked() {
		return blocked;
	}


	public void setBlocked(Boolean blocked) {
		this.blocked = blocked;
	}
	
	

}
