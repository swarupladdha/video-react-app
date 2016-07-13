package com.groupz.followup.database;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;




import src.followupconfig.PropertiesUtil;

import com.groupz.followup.manager.CacheManager;
import com.groupz.followup.manager.FollowupManager;
import com.groupz.followup.manager.FollowupRefreshQueueExceute;

public class ConnectDatabase {

	private Connection myConnection = null;
	static final Logger logger = Logger.getLogger(ConnectDatabase.class);

	public Connection establishConnection(Properties p) {

		if (myConnection == null) {
			String url = null;
			String dbName = null;
			String driver = null;
			String userName = null;
			String password = null;

			try {
				/*String fileName = System.getenv("FE_CONFIG_FILE");
				if (fileName == null) {
					logger.debug("Env. Variable FE is not set, using default file vinralerts.properties");
					fileName = "conf/db.properties";
				}

				// Properties p = new Properties(System.getProperties());
				//FileInputStream propFile = new FileInputStream(fileName);
				//p.load(propFile);
*/				url = p.getProperty("url");
				dbName = p.getProperty("dbName");
				driver = p.getProperty("driver");
				userName = p.getProperty("userName");
				password = p.getProperty("password");
			} catch (Exception e) {
				logger.debug("Error opening properties file." + e);
				e.printStackTrace();
			}

			try {
				Class.forName(driver).newInstance();
				myConnection = DriverManager.getConnection(url + dbName,
						userName, password);
				logger.debug("In ConnectDatabase.java : The url and dbname is : "
						+ url + dbName);
				logger.debug("Connected to the database");
			} catch (SQLException sqlException) {
				sqlException.printStackTrace();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
		return myConnection;
	}

	public void closeConnection(Connection connection) {

		try {
			connection.close();
			logger.debug("Disconnected from database");
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public static void main(String args[]) throws InterruptedException {
		ConnectDatabase cd = new ConnectDatabase();
		String fileName = System.getenv("FE_CONFIG_FILE");
		Properties p = null;
		try {
			if (fileName == null) {
				logger.debug("Env. Variable FE is not set, using default file vinralerts.properties");
				fileName = "conf/db.properties";
			}
			p = new Properties(System.getProperties());
			FileInputStream propFile = new FileInputStream(fileName);
			p.load(propFile);
		} catch (Exception e) {
			logger.debug("Error opening properties file." + e);
			e.printStackTrace();
		}
		final int contactFollowUpTimeout = Integer.parseInt(p
				.getProperty("contactfollowup_db_timeout"));
		final int cacheUpdateTimeout = Integer.parseInt(p
				.getProperty("cachefollowup_db_timeout"));
		System.out.println("followup alert started");
		logger.info("followup alert started");
		final Connection c = cd.establishConnection(p);
		// contact Follow up thread
		Thread contactFollowUp = new Thread() {
			FollowupManager fm = new FollowupManager();

			@Override
			public void run() {
				while (true) {
					System.out
							.println("Contact Follow up thread started every 2 minutes:"
									+ new Date());
					logger.debug("Contact Follow up runs every 2 minutes");
					fm.run(c);
					try {
						Thread.sleep(contactFollowUpTimeout);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		contactFollowUp.start();
		// cache Follow up thread
		Thread cacheUpdateFollowUp = new Thread() {
			CacheManager cm = new CacheManager();

			@Override
			public void run() {
				while (true) {
					System.out
							.println("Cache Update thread started every half a second:"
									+ new Date());
					logger.debug("Cache Update runs every half a second");
					cm.run(c);
					try {
						Thread.sleep(cacheUpdateTimeout);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		cacheUpdateFollowUp.start();
	
		//new code
		int THREAD_POOL_SIZE = Integer.parseInt(PropertiesUtil
				.getProperty("THREAD_POOL"));
		ExecutorService refreshQueueExecSvc = Executors
				.newFixedThreadPool(THREAD_POOL_SIZE);
		for (int i = 0; i < THREAD_POOL_SIZE; i++) {
			refreshQueueExecSvc
					.execute(new FollowupRefreshQueueExceute(i,c));
		}
		refreshQueueExecSvc.shutdown();	
		
		
		
		
		
		
		/*
		 * while (true) { System.out.println("runs every 2 minutes:"+new
		 * Date()); logger.debug("runs every 2 minutes"); //fm.run(c);
		 * cm.run(c);
		 * System.out.println("=============================================");
		 * Thread.sleep(120000); }
		 */
		/*
		 * if (c != null) { cd.closeConnection(c); }
		 */

		// return;
	}

}
