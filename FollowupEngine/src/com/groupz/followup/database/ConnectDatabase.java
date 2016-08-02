package com.groupz.followup.database;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import src.followupconfig.PropertiesUtil;

import com.groupz.followup.manager.FollowupRefreshQueueExceute;
import com.groupz.followup.threads.CacheUpdateThread;
import com.groupz.followup.threads.FollowUpThread;
import com.groupz.followup.threads.serviceRequestThread;

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
				url = p.getProperty("url");
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
		final int contactFollowUpTimeout = Integer.parseInt(p.getProperty("contactfollowup_db_timeout"));
		final int cacheUpdateTimeout = Integer.parseInt(p.getProperty("cachefollowup_db_timeout"));
		final int serviceRequestTimeout = Integer.parseInt(p.getProperty("serviceRequest_db_timeout"));
		System.out.println("followup alert started");
		logger.info("followup alert started");
		final Connection c = cd.establishConnection(p);

		//followupthread

		int followUpThread_POOL_SIZE = Integer.parseInt(PropertiesUtil
				.getProperty("FollowUpThread_POOL"));
		ExecutorService followupthreadExecSvc = Executors
				.newFixedThreadPool(followUpThread_POOL_SIZE);
		for (int threadId = 0; threadId < followUpThread_POOL_SIZE; threadId++) {
		
			followupthreadExecSvc.execute(new FollowUpThread(followUpThread_POOL_SIZE,threadId,c,contactFollowUpTimeout));

		}
		followupthreadExecSvc.shutdown();
		
		//cacheupdatethread
		int cache_POOL_SIZE = Integer.parseInt(PropertiesUtil
				.getProperty("CacheUpdateThread_POOL"));
		ExecutorService cacheExecSvc = Executors
				.newFixedThreadPool(cache_POOL_SIZE);
		for (int threadId = 0; threadId < cache_POOL_SIZE; threadId++) {
		
			cacheExecSvc.execute(new CacheUpdateThread(cache_POOL_SIZE,threadId,c,cacheUpdateTimeout));

		}
		followupthreadExecSvc.shutdown();
		
		//service request thread
		int serviceRequest_POOL_SIZE = Integer.parseInt(PropertiesUtil
				.getProperty("serviceRequestThread_POOL"));
		ExecutorService srExecSvc = Executors
				.newFixedThreadPool(cache_POOL_SIZE);
		for (int threadId = 0; threadId < serviceRequest_POOL_SIZE; threadId++) {
		
			cacheExecSvc.execute(new serviceRequestThread(serviceRequest_POOL_SIZE,threadId,c,serviceRequestTimeout));

		}
		followupthreadExecSvc.shutdown();
		
		// feeAggragation and headcount analytics 
		int THREAD_POOL_SIZE = Integer.parseInt(PropertiesUtil
				.getProperty("THREAD_POOL"));
		ExecutorService refreshQueueExecSvc = Executors
				.newFixedThreadPool(THREAD_POOL_SIZE);
		for (int i = 0; i < THREAD_POOL_SIZE; i++) {
			refreshQueueExecSvc.execute(new FollowupRefreshQueueExceute(i, c));
			
		}
		refreshQueueExecSvc.shutdown();

		
	}

}
