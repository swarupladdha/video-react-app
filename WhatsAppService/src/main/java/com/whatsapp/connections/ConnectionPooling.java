package com.whatsapp.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;

import com.whatsapp.utils.PropertiesUtil;

public class ConnectionPooling {
	private BasicDataSource ds = null;

//	private BasicDataSource getDataSource() {
//		try {
//			if (ds == null) {
//				ds = new BasicDataSource();
//				ds.setDriverClassName(PropertiesUtil.getProperty("db.class"));
//				ds.setUrl(PropertiesUtil.getProperty("db.url"));
//				ds.setUsername(PropertiesUtil.getProperty("db.username"));
//				ds.setPassword(PropertiesUtil.getProperty("db.password"));
//				ds.setInitialSize(5);
////				ds.setMaxIdle(2);
////				ds.setMinIdle(2);
//				ds.setMaxTotal(20);
//			}
//			return ds;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//
//	}

	private Connection getCon() {

		try {
			Class.forName(PropertiesUtil.getProperty("db.class"));
			Connection con = DriverManager.getConnection(PropertiesUtil.getProperty("db.url"),
					PropertiesUtil.getProperty("db.username"), PropertiesUtil.getProperty("db.password"));
			return con;

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return null;
		}

	}

	public synchronized Connection getConnection() throws SQLException {
		return getCon();
		// return getDataSource().getConnection();

	}

	public void closeConnection(Connection con) {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
