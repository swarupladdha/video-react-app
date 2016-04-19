package com.groupz.followup.database;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.groupz.followup.manager.FollowupManager;


public class ConnectDatabase {
	
    private  Connection myConnection = null;
    static final Logger logger = Logger.getLogger(ConnectDatabase.class);

    public  Connection establishConnection() {
  
        if (myConnection == null) {
            String url = null;
            String dbName = null;
            String driver = null;
            String userName = null;
            String password = null;

            try {
                String fileName = System.getenv("FE_CONFIG_FILE");
                if (fileName == null) {
                    logger.debug("Env. Variable FE is not set, using default file vinralerts.properties");
                    fileName="conf/db.properties";
                }

                Properties p = new Properties(System.getProperties());
                FileInputStream propFile = new FileInputStream(fileName);
                p.load(propFile);
                url = p.getProperty("url");
                dbName =p.getProperty("dbName");
                driver =p.getProperty("driver");
                userName = p.getProperty("userName") ;
                password = p.getProperty("password") ;
            } catch (Exception e) {
                logger.debug("Error opening properties file."+e);
                e.printStackTrace();
            }

            try {
                Class.forName(driver).newInstance();
                myConnection = DriverManager.getConnection(url+dbName,userName,password);
                logger.debug("In ConnectDatabase.java : The url and dbname is : " + url+dbName);
                logger.debug("Connected to the database");
            } catch(SQLException sqlException) {
                sqlException.printStackTrace();
            } catch(Exception exception) {
                exception.printStackTrace();
            }
        }
        return myConnection ;
    }

    public void closeConnection(Connection connection) {
        
        try {
            connection.close();
            logger.debug("Disconnected from database");
        } catch(SQLException sqlException) {
            sqlException.printStackTrace();
        } catch(Exception exception) {
            exception.printStackTrace();
        }
    }

	public static void main(String args[]) throws InterruptedException {
		ConnectDatabase cd = new ConnectDatabase();			
		FollowupManager fm = new FollowupManager();
		System.out.println("followup alert started");
		logger.info("followup alert started");
		Connection c = cd.establishConnection();
		while (true) {
			System.out.println("runs every 2 minutes:"+new Date());
			logger.debug("runs every 2 minutes");			
			fm.run(c);	
			System.out.println("=============================================");
			Thread.sleep(120000);
		}
		/*if (c != null) {
			cd.closeConnection(c);
		}*/
		
		// return;
	}

}
