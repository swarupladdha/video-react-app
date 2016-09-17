package com.groupz.followup.operations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.groupz.followup.utils.ConnectionUtils;

public class ServiceRequestAggregationOperations {

	String getServiceAggregationQuery = " select category,IssueState, "
										+ " sum(case   when ClosedTime  is null and InvalidIssue = 0 then 1 else 0 end) as openSR, "
										+ " sum(case   when ClosedTime  is not null and InvalidIssue = 0 then 1 else 0 end) as closedSR, "
										+ " sum(case   when ApartmentId = %s   then 1 else 0 end) as totalescalations "
										+ " from issue where YEAR(FiledDate) = %s AND MONTH(FiledDate) = %s "
										+ " and ApartmentId = %s  group by Category ";

	 String deleteServiceAggregationQuery = "delete from escalationaggregation where year = %s and month = %s and groupzid=%s";

	 String addServiceAggregationQuery = "INSERT INTO escalationaggregation "
	 									+ " (openescalations,closedescalations,totalescalations,groupzid,category,month,year) "
	 									+ " VALUES( %s, %s, %s, %s, '%s', %s, %s)";
	 
	public void deleteServiceAggregation(Connection connection, String year, String month, int groupzId) {
		// TODO Auto-generated method stub
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			System.out.println("Inside deleteServiceAggregation() of deleteServiceAggregation deleting ...");
			String deleteSRAggQuery = String.format(deleteServiceAggregationQuery, year, month,groupzId);
		//	System.out.println(deleteFeeAggQuery);
			boolean deletedSRAggSet = stmt.execute(deleteSRAggQuery);
			
			if (deletedSRAggSet) {
				System.out.println("Inside deleteServiceAggregation() of deleteServiceAggregation deleted ...");
			}
			ConnectionUtils.close(stmt);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ConnectionUtils.close(stmt);
		}
	}

	public void saveServiceAggregation(Connection connection, String year, String month, int groupzId) {
		// TODO Auto-generated method stub
		Statement stmt = null;
		try {
			String getSRQuery = String.format(getServiceAggregationQuery,groupzId, year, month, groupzId);
			System.out.println(getSRQuery);
			stmt = connection.createStatement();
			ResultSet getSrAggSet = stmt.executeQuery(getSRQuery);
			System.out.println("Inside saveServiceAggregation() of ServiceRequestAggregationOperations inserting into Escalationaggregation..."+getSrAggSet.toString());
		
			while (getSrAggSet.next()) {
			String openSR = getSrAggSet.getString("openSR");
			String closedSR = getSrAggSet.getString("closedSR");
			String totalescalations = getSrAggSet.getString("totalescalations");
			String category = getSrAggSet.getString("category");
	//		System.out.println(" openSR: "+openSR + " closedSR: " + closedSR + " totalescalations: "+totalescalations +" category: "+ category);
			stmt = connection.createStatement();
			String addSRAggSet = String.format(addServiceAggregationQuery, openSR, closedSR,
					totalescalations, groupzId, category,month , year);		
	//			System.out.println(addSRAggSet);
				
			boolean addSRAgg = stmt.execute(addSRAggSet);
	//			System.out.println(addSRAgg);
			}
			System.out.println("Inside saveServiceAggregation() of ServiceRequestAggregationOperations inserted into ServiceRequestAggregationOperations...");
			ConnectionUtils.close(stmt);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ConnectionUtils.close(stmt);
			}
		}
	}


