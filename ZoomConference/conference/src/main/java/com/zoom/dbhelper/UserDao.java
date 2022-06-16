package com.zoom.dbhelper;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

@Entity
@Table(name="user")
public class UserDao {
	
	@javax.persistence.Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long Id;
	
	@Column(name = "username", nullable = false)
	private String username;
	
	@Column(name = "password", nullable = false)
	private String password;
	
	@Column(name = "ishost", nullable = false)
	private String isHost;
	
	

	public UserDao() {
	}

	
	public UserDao(String username, String password, String isHost) {
		this.username = username;
		this.password = password;
		this.isHost = isHost;
	}
	

	public UserDao(long i, String username, String password, String isHost) {
		super();
		Id = i;
		this.username = username;
		this.password = password;
		this.isHost = isHost;
	}

	public long getId() {
		return Id;
	}

	public void setId(long id) {
		Id = id;
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

	public String getIsHost() {
		return isHost;
	}

	public void setIsHost(String isHost) {
		this.isHost = isHost;
	}
	
	
	
}
