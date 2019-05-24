package com.stocking.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.stocking.utils.ConnectionManager;

public class UserDao {
	
	public int getUserIdByUserName(Connection connection,String name) {
		Statement stmt=null;
		ResultSet res=null;
		try{
			String getUserQuery = "select id from user where username = '"+name+"'";
			stmt = connection.createStatement();
			res = stmt.executeQuery(getUserQuery);
			if(res.next()) {
				return res.getInt("id");
			}
			else {
				return -1;
			}
		}catch (Exception e) {
			return 0;
		}
		finally {
			ConnectionManager.closeStatement(stmt);
			ConnectionManager.closeResultSet(res);
		}
	}
	
	public void addUser(Connection connection,String fName,String lName,String uName,String password,String email) {
		Statement stmt=null;
		try {
			String addQuery="insert into user (firstname,lastname,username,password,email) values ('"
					+fName+"','"+lName+"','"+uName+"','"+password+"','"+email+"')";
			stmt = connection.createStatement();
			stmt.executeUpdate(addQuery);
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			ConnectionManager.closeStatement(stmt);
		}
	}
	
	public void updateUser() {
		
	}

}
