package com.groupz.followup.threads;

import java.sql.Connection;
import java.util.Date;

import org.apache.log4j.Logger;

import com.groupz.followup.manager.CacheManager;
import com.groupz.followup.manager.ServiceRequestManager;

public class serviceRequestThread implements Runnable {
	private Connection connection;
	private int serviceRequestTimeout;
	private int threadId=0;
	private int sRPoolSize;

	ServiceRequestManager srm = new ServiceRequestManager();
	static final Logger logger = Logger.getLogger(serviceRequestThread.class);
	
	public serviceRequestThread() {
		// TODO Auto-generated constructor stub
		
	}
	public serviceRequestThread(int sRPoolSize, int threadId, Connection connection, int serviceRequestTimeout) {
		this.threadId=threadId;
		this.connection=connection;
		this.serviceRequestTimeout=serviceRequestTimeout;
		this.sRPoolSize=sRPoolSize;
		}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			System.out
					.println("Service Request thread started every 2 mins :"
							+ new Date());
			logger.debug("Service Request runs every 2 mins ");
			srm.run(this.connection,this.sRPoolSize,this.threadId);
			try {
				Thread.sleep(serviceRequestTimeout);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
