package api.groupz.admin.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionManager {

	 static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   static final String DB_URL = "jdbc:mysql://localhost/groupzivr";
	   static final String USER = "root";
	   static final String PASS = "password";
	   
	  
	   static Statement stmt = null;
	   
	  
	   public static Connection getConnect()
	   {
		    Connection conn = null;
		   try{
			   Class.forName(JDBC_DRIVER);
			   conn = DriverManager.getConnection(DB_URL,USER,PASS);
			   System.out.println("Connecting to a GroupzIvr Database...");
			   System.out.println("Connected Successfully !");
				return conn;
		
			  }catch(Exception e)
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
