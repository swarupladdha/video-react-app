package com.groupz.followup.threads;

import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.groupz.followup.manager.FollowupManager;

public class FollowUpThread implements Runnable{

	static final Logger logger = Logger.getLogger(FollowUpThread.class);
	private int threadId=0;
	private Connection connection;
	private int contactFollowUpTimeout;
	private int followUpThread_POOL_SIZE;
	public FollowUpThread() {
			
		}
	
	FollowupManager fm = new FollowupManager();
	
	public FollowUpThread( int followUpThreadPoolSize,int threadId, Connection connection, int contactFollowUpTimeout) {
		// TODO Auto-generated constructor stub
		this.threadId=threadId;
		this.connection=connection;
		this.contactFollowUpTimeout=contactFollowUpTimeout;
		this.followUpThread_POOL_SIZE=followUpThreadPoolSize;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
	
		while (true) {
			System.out
					.println("Contact Follow up thread started every 2 minutes:"
							+ new Date());
			logger.debug("Contact Follow up runs every 2 minutes");
			fm.run(this.threadId,this.followUpThread_POOL_SIZE,this.connection);
			try {
				Thread.sleep(contactFollowUpTimeout);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
			
	/*public void startFollowUpThread(FollowUpThread followup) {
		Thread followupThread = new Thread(followup);
		followupThread.start();
		
	}*/


}
