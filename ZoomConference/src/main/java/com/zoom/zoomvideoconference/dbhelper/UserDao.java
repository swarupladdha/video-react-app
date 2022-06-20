package com.zoom.zoomvideoconference.dbhelper;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class UserDao {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer Id;
	private String username;
	private String userEmail;
	private String password;
	private String isHost;
	
	public UserDao() {}
	
	public UserDao(String username, String userEmail, String password, String isHost) {	
		this.username = username;
		this.userEmail = userEmail;
		this.password = password;
		this.isHost = isHost;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getIsHost() {
		return isHost;
	}

	public void setIsHost(String isHost) {
		this.isHost = isHost;
	}

}