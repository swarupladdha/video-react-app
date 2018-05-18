package com.groupz.followup.manager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.Logger;

import com.groupz.followup.operations.FeeAggregationOperations;
import com.groupz.followup.operations.GroupzBaseAggOperation;
import com.groupz.followup.operations.ServiceRequestAggregationOperations;
import com.groupz.followup.utils.ConnectionUtils;
import com.groupz.followup.utils.FollowUpUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import src.followupconfig.PropertiesUtil;

public class AggregationThread implements Runnable {
	public static String lastUpdatedFcmDate = null;
	private int id = 0;
	private Connection connection = null;
	ComboPooledDataSource connectionPool = null;
	int aggregationTimeout = 0;
	static final Logger logger = Logger.getLogger(AggregationThread.class);

	public AggregationThread(int id, ComboPooledDataSource connectionPool1,
			int aggregationTimeout) throws SQLException {
		this.id = id;
		this.connectionPool = connectionPool1;
		this.aggregationTimeout = aggregationTimeout;

	}

	String getLatestReportsQuery = "select * from UpdateModuleActivities where Id MOD %s= %s and UpdatedDate >=  '%s'";
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	String date = getAnalyticsBackupDate();

	@Override
	public void run() {
		Statement stmt = null;
		int THREAD_POOL_SIZE = Integer.parseInt(PropertiesUtil
				.getProperty("THREAD_POOL"));
		while (true) {
			try {
				connection = connectionPool.getConnection();
				stmt = connection.createStatement();
				String threadpool = String.valueOf(THREAD_POOL_SIZE);
				String strid = String.valueOf(this.id);
				if (lastUpdatedFcmDate == null) {
					lastUpdatedFcmDate = getLastSynchTime();
				}
				String QueryString = String.format(getLatestReportsQuery,
						threadpool, strid, lastUpdatedFcmDate);
				System.out.println("UpdateModuleActivities Query : "
						+ QueryString);
				ResultSet updatemoduleactivitiesSet = stmt
						.executeQuery(QueryString);
				while (updatemoduleactivitiesSet.next()) {
					lastUpdatedFcmDate = getLastSynchTime();
					stmt = connection.createStatement();
					int groupzId = updatemoduleactivitiesSet.getInt("GROUPZID");
					String moduleType = updatemoduleactivitiesSet
							.getString("MODULETYPE");
					String updatedDate = updatemoduleactivitiesSet
							.getString("UPDATEDDATE");
					String[] monthyear = getYearAndMonth(updatedDate);
					String year = monthyear[0];
					String month = monthyear[1];
					// System.out.println(groupzId + "" + groupzCode + ""+
					// moduleType + "" + updatedDate);

					if (moduleType.equals("MEMBER")) {
						System.out.println("new " + moduleType
								+ " request received for groupid: " + groupzId
								+ " UpdatedTime: " + updatedDate);
						HeadCountByLocation hsbl = new HeadCountByLocation();
						hsbl.deleteHeadCountByLocation(connection, groupzId,
								updatedDate);
						hsbl.saveHeadCountByLocationNew(connection, groupzId);
						this.date = getCurrentTime();
					}
					if (moduleType.equalsIgnoreCase("GROUPZ")) {
						System.out.println("new " + moduleType
								+ " request received for groupid: " + groupzId
								+ " UpdatedTime: " + updatedDate);
						GroupzBaseAggOperation groupzBaseAgOp = new GroupzBaseAggOperation();
						groupzBaseAgOp.deleteGroupzDetailsOnBase(connection,
								groupzId);
						groupzBaseAgOp.saveGroupzBaseDetails(connection,
								groupzId, updatedDate);
					}

					if (moduleType.equals("DUES")) {
						System.out.println("new " + moduleType
								+ " request received for groupid: " + groupzId
								+ "con: " + connectionPool.getNumConnections());
						FeeAggregationOperations feeAggregation = new FeeAggregationOperations();
						feeAggregation.deleteFeeAgg(connection, year, month,
								groupzId);
						feeAggregation.saveFeeAgg(connection, groupzId,
								updatedDate);
						this.date = getCurrentTime();
					}
					if (moduleType.equalsIgnoreCase("ISSUES")) {
						System.out.println("new " + moduleType
								+ " request received for groupid: " + groupzId
								+ "con: " + connectionPool.getNumConnections());
						ServiceRequestAggregationOperations srm = new ServiceRequestAggregationOperations();
						srm.deleteServiceAggregation(connection, groupzId);
						srm.saveServiceAggregation(connection, groupzId,
								updatedDate);
					}
					if (moduleType.equalsIgnoreCase("MESSAGES")) {
						System.out.println("new " + moduleType
								+ " request received for groupid: " + groupzId
								+ "con: " + connectionPool.getNumConnections());
						ServiceRequestAggregationOperations srm = new ServiceRequestAggregationOperations();
						srm.deleteServiceAggregation(connection, groupzId);
						srm.saveServiceAggregation(connection, groupzId,
								updatedDate);
					}
				}
				// System.out.println("thread id: "+this.id+"date: "+this.date);
				ConnectionUtils.close(stmt, this.connection);
				Thread.sleep(aggregationTimeout);
				// System.out.println("con"+connection);
			} catch (Exception e) {
				ConnectionUtils.close(stmt, connection);
				logger.error("Excepton Caught in AggregationThread Class");
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
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		return sdf.format(time);
	}

	public static String getLastSynchTime() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		String utcTime = f.format(new Date());
		System.out.println("String date:" + utcTime);

		System.out.println("Date :" + utcTime);
		return utcTime;
	}

	public String getAnalyticsBackupDate() {
		String analyticsDate = PropertiesUtil
				.getProperty("analytics_backup_date");
		FollowUpUtils analytics = new FollowUpUtils();
		if (analytics.isEmpty(analyticsDate) == false) {
			try {
				Calendar now = Calendar.getInstance();
				now.set(Calendar.HOUR, 0);
				now.set(Calendar.MINUTE, 0);
				now.set(Calendar.SECOND, 0);
				now.set(Calendar.HOUR_OF_DAY, 0);
				return sdf.format(now.getTime());
			} catch (Exception e) {
				System.out.println("exception in time input");
				return null;
			}
		} else {
			return analyticsDate;
		}
	}
}
