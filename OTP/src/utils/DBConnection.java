package utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
	static String dbusername = PropertiesUtil.getProperty("db_user_name");
	static String dbpassword = PropertiesUtil.getProperty("db_password");
	static String dburl = PropertiesUtil.getProperty("db_url");
	static String jdbcDriver = PropertiesUtil.getProperty("jdbc_driver");

	public Connection getConnection() {
		Connection dbConnection = null;

		try {
			System.out.println("11");
			Class.forName(jdbcDriver);
			dbConnection = (Connection) DriverManager.getConnection(dburl,
					dbusername, dbpassword);
			return dbConnection;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return dbConnection;

	}
}
