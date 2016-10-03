package com.groupz.followup.database;

import java.beans.PropertyVetoException;
import java.io.FileInputStream;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import src.followupconfig.PropertiesUtil;

import com.groupz.followup.manager.AggregationThread;
import com.groupz.followup.threads.CacheUpdateThread;
import com.groupz.followup.threads.FollowUpThread;
import com.groupz.followup.threads.serviceRequestThread;
import com.mchange.v2.c3p0.ComboPooledDataSource;


public class ConnectDatabase {

	
	static final Logger logger = Logger.getLogger(ConnectDatabase.class);

		ComboPooledDataSource getConnectionPool(Properties p)throws PropertyVetoException {
		ComboPooledDataSource cpds = new ComboPooledDataSource();
		System.out.println("inside cpds");
		System.out.println(p.getProperty("url"));
		cpds.setJdbcUrl(p.getProperty("url"));
		cpds.setDriverClass(p.getProperty("driver"));
		cpds.setUser(p.getProperty("userName"));
		cpds.setPassword(p.getProperty("password"));
		System.out.println(cpds.getUser() + "" + cpds.getPassword() + ""+ cpds.getJdbcUrl() + "" + cpds.getDriverClass());
		// Optional Settings
		/*cpds.setMaxPoolSize(50);
		cpds.setAcquireIncrement(1);
		cpds.setMaxIdleTimeExcessConnections(100);
		cpds.setUnreturnedConnectionTimeout(100);
		cpds.setDebugUnreturnedConnectionStackTraces(true);*/
		return cpds;
	}

	public static void main(String args[]) throws InterruptedException,	SQLException, PropertyVetoException {
		ConnectDatabase cd = new ConnectDatabase();
		String fileName = null ;
		Properties p = null;
		ComboPooledDataSource connectionPool=null;
		
		try {
			if (fileName == null) {
				fileName = System.getenv("FE_CONFIG_FILE");
				logger.debug("Env. Variable FE is not set, using default file vinralerts.properties");
				fileName = "conf/db.properties";
				p = new Properties(System.getProperties());
				System.out.println("p1"+p);
				FileInputStream propFile = new FileInputStream(fileName);
				p.load(propFile);
				System.out.println("p"+p);
				System.out.println("propFile"+propFile);
			}			
		} 
		catch (Exception e) {
			logger.debug("Error opening properties file." + e);
			e.printStackTrace();
		}
		//connection pool
		connectionPool = cd.getConnectionPool(p);
		final int aggregationTimeout = Integer.parseInt(p.getProperty("aggregation_sleepTime"));
		final int contactFollowUpTimeout = Integer.parseInt(p.getProperty("contactfollowup_db_timeout"));
		final int cacheUpdateTimeout = Integer.parseInt(p.getProperty("cachefollowup_db_timeout"));
		final int serviceRequestTimeout = Integer.parseInt(p.getProperty("serviceRequest_db_timeout"));
		final int AGGREGATION_POOL_SIZE = Integer.parseInt(PropertiesUtil.getProperty("THREAD_POOL"));
		final int cache_POOL_SIZE = Integer.parseInt(PropertiesUtil.getProperty("CacheUpdateThread_POOL"));
		final int followUpThread_POOL_SIZE = Integer.parseInt(PropertiesUtil.getProperty("FollowUpThread_POOL"));
		logger.info("followup alert started");

		
		// feeAggregation , head count and head count By location analytics
		ExecutorService aggregationExecSvc = Executors.newFixedThreadPool(AGGREGATION_POOL_SIZE);
		for (int i = 0; i < AGGREGATION_POOL_SIZE; i++) {
			aggregationExecSvc.execute(new AggregationThread(i, connectionPool,aggregationTimeout));
		}
		aggregationExecSvc.shutdown();
	

		// escalation Upgrade thread
		serviceRequestThread sr = new serviceRequestThread(connectionPool,serviceRequestTimeout);
		sr.startFollowUpThread(sr);

		
		// follow up message
		ExecutorService followupmessageExecSvc = Executors.newFixedThreadPool(followUpThread_POOL_SIZE);
		for (int threadId = 0; threadId < followUpThread_POOL_SIZE; threadId++) {
			followupmessageExecSvc.execute(new FollowUpThread(followUpThread_POOL_SIZE, threadId, connectionPool,aggregationTimeout));
		}
		followupmessageExecSvc.shutdown();
				
		
		// cache updated request to node
		ExecutorService cacheExecSvc = Executors.newFixedThreadPool(cache_POOL_SIZE);
		for (int threadId = 0; threadId < cache_POOL_SIZE; threadId++) {
			cacheExecSvc.execute(new CacheUpdateThread(cache_POOL_SIZE,	threadId, connectionPool, cacheUpdateTimeout));
		}
		cacheExecSvc.shutdown();
	}
}
