package api.groupz.database;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DBConnect
{
	public static Properties prop = new Properties();
	
	// JDBC driver name and database URL
		public static String JDBC_DRIVER;
		public static String DB_URL;  

	//  Database credentials
		public static String USER;
		public static String PASS;
		
		static
		{
			try
			{
				InputStream in = DBConnect.class.getResourceAsStream("/api.properties");
				prop.load(in);
				JDBC_DRIVER = prop.getProperty("driver");
				DB_URL = prop.getProperty("dburl");
				USER = prop.getProperty("dbusername");
				PASS = prop.getProperty("dbpassword");
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	
	public static Connection establishConnection()
	{
		Connection conn = null;

		try
		{
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			System.out.println("Connecting to a selected database...");
			return conn;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Error in database Connection");
			return null;
		}
		
	}
	public static void closeConnection(Connection conn)
	{
		try
		{
			conn.close();
		}
		catch(SQLException se)
		{
			se.printStackTrace();
		}
	}
}




