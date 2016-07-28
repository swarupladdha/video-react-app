package com.groupz.followup.threads;

import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.groupz.followup.manager.CacheManager;

public class CacheUpdateThread implements Runnable {

	static final Logger logger = Logger.getLogger(CacheUpdateThread.class);
	private Connection connection;
	private int cacheUpdateTimeout;
	private int threadId=0;
	private int cachePoolSize;
	CacheManager cm = new CacheManager();
	public CacheUpdateThread() {
		// TODO Auto-generated constructor stub
		
	}
	public CacheUpdateThread(int cachePoolSize, int threadId, Connection connection, int cacheUpdateTimeout) {
		this.threadId=threadId;
		this.connection=connection;
		this.cacheUpdateTimeout=cacheUpdateTimeout;
		this.cachePoolSize=cachePoolSize;
		}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			System.out
					.println("Cache Update thread started every half a second:"
							+ new Date());
			logger.debug("Cache Update runs every half a second");
			cm.run(this.connection,this.cachePoolSize,this.threadId);
			try {
				Thread.sleep(cacheUpdateTimeout);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}



	/*public void startCacheThread(CacheUpdateThread cacheUpdateThread) {
		
		Thread cacheThread = new Thread(cacheUpdateThread);
		cacheThread.start();
		
	}
	
*/
}
