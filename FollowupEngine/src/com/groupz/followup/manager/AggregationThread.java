package com.groupz.followup.manager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.groupz.followup.operations.FeeAggregationOperations;
import com.groupz.followup.operations.FollowUpGraphOperations;
import com.groupz.followup.operations.HeadCountOperations;
import com.groupz.followup.utils.ConnectionUtils;
import com.groupz.followup.utils.FollowUpUtils;

import src.followupconfig.PropertiesUtil;

public class AggregationThread implements Runnable {
	private int id = 0;
	private Connection connection = null;
	static final Logger logger = Logger
			.getLogger(AggregationThread.class);

	public AggregationThread(int id, Connection c) {
		this.id = id;
		this.connection = c;

	}
	 String getLatestReportsQuery = "select * from UpdateModuleActivities where Id MOD %s= %s and UpdatedDate >=  '%s'";
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String date = getAnalyticsBackupDate();

	@Override
	public void run() {
		Statement stmt = null;
		int THREAD_POOL_SIZE = Integer.parseInt(PropertiesUtil
				.getProperty("THREAD_POOL"));
		// this.date=null;
		while (true) {
			try {
				stmt = connection.createStatement();
				Thread.sleep(1000);
				String threadpool = String.valueOf(THREAD_POOL_SIZE);
				String strid = String.valueOf(this.id);
				String strdate = String.valueOf(this.date);

				String QueryString = String.format(getLatestReportsQuery,threadpool, strid, strdate);

				// System.out.println("get updatemoduleactivities report Sql   :"// + QueryString);

				ResultSet updatemoduleactivitiesSet = stmt.executeQuery(QueryString);

				while (updatemoduleactivitiesSet.next()) {
					int groupzId = updatemoduleactivitiesSet.getInt("GROUPZID");
					String groupzCode = updatemoduleactivitiesSet.getString("GROUPZCODE");
					String moduleType = updatemoduleactivitiesSet.getString("MODULETYPE");
					String updatedDate = updatemoduleactivitiesSet.getString("UPDATEDDATE");
					String[] monthyear = getYearAndMonth(updatedDate);
					String year = monthyear[0];
					String month = monthyear[1];
				//	System.out.println(groupzId + "" + groupzCode + ""+ moduleType + "" + updatedDate);
					
					if (moduleType.equals("MEMBER")) {
						System.out.println("new "+moduleType+" request received for groupid: "+groupzId+" UpdatedTime: "+updatedDate);
							
						HeadCountOperations headCountOperations = new HeadCountOperations();
						headCountOperations.deleteHeadCount(connection,groupzId);
						headCountOperations.saveHeadcount(connection,groupzId);
						this.date = getCurrentTime();
						System.out.println("thread id: "+this.id+"date: "+this.date);
						//FollowUp Data
						FollowUpGraphOperations fugo=new FollowUpGraphOperations();
						fugo.deleteFollowUpGraphData(connection,groupzId,month,year);
					    fugo.saveFollowUpGraphData(connection,groupzId,month,year);
						//HeadCount By Location
						HeadCountByLocation hsbl=new HeadCountByLocation();
						hsbl.deleteHeadCountByLocation(connection,groupzId,updatedDate);
						hsbl.saveHeadCountByLocation(connection,groupzId,updatedDate);
						
					}
					
									
					if (moduleType.equals("PAYMENT")) {
						System.out.println("new "+moduleType+" request received for groupid: "+groupzId);
												
						FeeAggregationOperations feeAggregation=new FeeAggregationOperations();
						feeAggregation.deleteFeeAgg(connection,year,month,groupzId);
						feeAggregation.saveFeeAgg(connection,year,month, groupzId);			
						this.date = getCurrentTime();
						System.out.println("thread id: "+this.id+"date: "+this.date);
						}
					if (moduleType.equals("DUES")) {
						System.out.println("new "+moduleType+" request received for groupid: "+groupzId);
						/*						
						FeeAggregationOperations feeAggregation=new FeeAggregationOperations();
						feeAggregation.deleteFeeAgg(connection,year,month,groupzId);
						feeAggregation.saveFeeAgg(connection,year,month, groupzId);			
						this.date = getCurrentTime();
						System.out.println("thread id: "+this.id+"date: "+this.date);*/
						}

					}
				ConnectionUtils.close(stmt);
			} catch (Exception e) {
				ConnectionUtils.close(stmt);
				logger.error("Excepton Caught in followuorefreshqueue execute Class");
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	

	private String[] getYearAndMonth(String updatedDate) {

		String[] date = (updatedDate.toString()).split("-");
		return date;
	}

	public String getCurrentTime() {
		Date time = new Date();
		// Print the date in the default timezone
	//	System.out.println(sdf.format(time));
		// Set a specific timezone
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(time);
	}

	public String getAnalyticsBackupDate() {
		String analyticsDate = PropertiesUtil
				.getProperty("analytics_backup_date");
		FollowUpUtils analytics = new FollowUpUtils();
		if (analytics.isEmpty(analyticsDate) == false) {
			try{
			Calendar now = Calendar.getInstance();
			now.set(Calendar.HOUR, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.SECOND, 0);
		//	System.out.println(sdf.format(now.getTime()));
			now.set(Calendar.HOUR_OF_DAY, 0);
			return sdf.format(now.getTime());
			}
			catch(Exception e)
			{
				System.out.println("exception in time input");
				return null;
			}
		} else
		{
			return analyticsDate;
		}

	}

}
