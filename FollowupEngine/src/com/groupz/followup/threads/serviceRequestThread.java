package com.groupz.followup.threads;

import org.apache.log4j.Logger;

import com.groupz.followup.manager.ServiceRequestManager;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class serviceRequestThread implements Runnable {
	private ComboPooledDataSource connectionPool;
	private int serviceRequestTimeout;


	ServiceRequestManager srm = new ServiceRequestManager();
	static final Logger logger = Logger.getLogger(serviceRequestThread.class);
	
	public serviceRequestThread( ComboPooledDataSource connectionPool, int serviceRequestTimeout) {
		// TODO Auto-generated constructor stub
		this.connectionPool=connectionPool;
		this.serviceRequestTimeout=serviceRequestTimeout;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			logger.debug("Service Request runs every 1 min ");
			//srm.startServiceAggregation(this.connectionPool);
			try {
				Thread.sleep(serviceRequestTimeout);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void startFollowUpThread(serviceRequestThread sr) {
		Thread srThread = new Thread(sr);
		
		srThread.start();
	
	}
}
