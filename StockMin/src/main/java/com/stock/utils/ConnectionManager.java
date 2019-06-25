package com.stock.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConnectionManager {

	private static final String dbusername = PropertiesUtil.getProperty("USER");
	private static final String dbpassword = PropertiesUtil.getProperty("PASSWORD");
	private static final String dburl = PropertiesUtil.getProperty("JDBC_URL");
	private static final String jdbcDriver = PropertiesUtil.getProperty("JDBC_DRIVER");
	
	
	public Connection getConnection() {
		try {
			Class.forName(jdbcDriver);
			Connection connection = DriverManager.getConnection(dburl, dbusername, dbpassword) ;
			return connection;
		}catch (Exception e) {
			return null;
		}
	}
	
	public void closeConnection(Connection connection) {
		try {
			if(connection != null) {
				connection.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void closeStatement(Statement stmt) {
		try {
			if(stmt != null) {
				stmt.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void closeResultSet(ResultSet res) {
		try {
			if(res != null) {
				res.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
