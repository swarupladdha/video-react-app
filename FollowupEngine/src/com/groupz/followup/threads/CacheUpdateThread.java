package com.groupz.followup.threads;

import java.sql.SQLException;
import java.util.Date;

import org.apache.log4j.Logger;

import com.groupz.followup.manager.CacheManager;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class CacheUpdateThread implements Runnable {

	static final Logger logger = Logger.getLogger(CacheUpdateThread.class);
	private ComboPooledDataSource connectionPool;
	private int cacheUpdateTimeout;
	private int threadId=0;
	private int cachePoolSize;
	CacheManager cm = new CacheManager();
	public CacheUpdateThread() {
		// TODO Auto-generated constructor stub
		
	}
	public CacheUpdateThread(int cachePoolSize, int threadId, ComboPooledDataSource connectionPool, int cacheUpdateTimeout) {
		this.threadId=threadId;
		this.connectionPool=connectionPool;
		this.cacheUpdateTimeout=cacheUpdateTimeout;
		this.cachePoolSize=cachePoolSize;
		}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			
			
			try {
				System.out.println("Cache Update thread started every half a second:"+ new Date()+"conn: "+connectionPool.getNumConnections());
				logger.debug("Cache Update runs every half a second");
				cm.startCacheManager(this.connectionPool,this.cachePoolSize,this.threadId);
				Thread.sleep(cacheUpdateTimeout);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


}
