package com.groupz.followup.operations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.groupz.followup.utils.ConnectionUtils;

public class FollowUpGraphOperations {

	static String deleteFollowUpGraphDataQuery = "delete from followupgraph where year = %s and month = %s and apartmentId=%s";

	static String getFollowupData = "select f.id, f.apartmentid,r.id as roleid,f.followupcount,f.createddate,f.approvaldate,f.contact, "
			+ " case when Contact = 0 then 1 else 0 end as user "
			+ " from  flat f,userflatmapping ufm,roledefinition r,apartment_settings aptset  where ufm.roleid=r.id and ufm.flatid=f.id "
			+ " and(  YEAR(f.CreatedDate) = %s AND MONTH(f.CreatedDate) = %s )  and f.ApartmentId =%s "
			+ " and f.CreatedDate between aptset.TransactionStartDate and aptset.TransactionEndDate "
			+ " group by f.createdDate";

	static String saveFollowUpGraphDataQuery = "insert into followupgraph (flatid,apartmentid,roleid,followupcount,createddate,approvaldate,"
			+ "contact,user,month,year)values(%s,%s,%s,%s,'%s','%s',%s,%s,%s,%s)";

	public void deleteFollowUpGraphData(Connection connection, int groupzId,
			String month, String year) {
		// TODO Auto-generated method stub
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			System.out
					.println("Inside deleteFollowUpGraphData() of FollowUpGraphOperations deleting from followupgraph...");
			String deleteQuery = String.format(deleteFollowUpGraphDataQuery,
					year, month, groupzId);
			// System.out.println(deleteQuery);
			boolean deleteQuerySet = stmt.execute(deleteQuery);

			if (deleteQuerySet) {
				System.out
						.println("Inside deleteFollowUpGraphData() of FollowUpGraphOperations deleted from followupgraph...");
			}
			ConnectionUtils.close(stmt);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			ConnectionUtils.close(stmt);
			e.printStackTrace();
		}

	}

	public void saveFollowUpGraphData(Connection connection, int groupzId,
			String month, String year) {
		// TODO Auto-generated method stub
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			System.out
					.println("Inside saveFollowUpGraphData() of FollowUpGraphOperations saving into followupgraph...");
			String getFollowupDataQuery = String.format(getFollowupData, year,
					month, groupzId);
			System.out.println(getFollowupDataQuery);
			ResultSet getFollowUpGraphData = stmt
					.executeQuery(getFollowupDataQuery);

			while (getFollowUpGraphData.next()) {
				String id = getFollowUpGraphData.getString("id");
				String apartmentId = getFollowUpGraphData
						.getString("apartmentId");
				String roleid = getFollowUpGraphData.getString("roleid");
				String followupcount = getFollowUpGraphData
						.getString("followupcount");
				String createddate = getFollowUpGraphData
						.getString("createddate");
				String approveddate = getFollowUpGraphData
						.getString("approvaldate");
			//	String noofdays = getFollowUpGraphData.getString("noOfDays");
				String contact = getFollowUpGraphData.getString("contact");
				String user = getFollowUpGraphData.getString("user");

				stmt = connection.createStatement();

				String saveQuery = String.format(saveFollowUpGraphDataQuery,
						id, apartmentId, roleid, followupcount, createddate,
						approveddate,  contact, user, month, year);
				// System.out.println(saveQuery);
				boolean saveQuerySet = stmt.execute(saveQuery);

			}
			ConnectionUtils.close(stmt);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			ConnectionUtils.close(stmt);
			e.printStackTrace();
		}

	}

}
