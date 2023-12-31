package com.groupz.followup.operations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.groupz.followup.utils.ConnectionUtils;

public class ServiceRequestAggregationOperations {

	String getServiceAggregationQuery = " select category,IssueState, "
			+ " sum(case   when ClosedTime  is null and InvalidIssue = 0 then 1 else 0 end) as openSR, "
			+ " sum(case   when ClosedTime  is not null and InvalidIssue = 0 then 1 else 0 end) as closedSR, "
			+ " sum(case   when ApartmentId = %s   then 1 else 0 end) as totalescalations "
			+ " from issue where YEAR(FiledDate) = %s AND MONTH(FiledDate) = %s "
			+ " and ApartmentId = %s  group by Category ";
	String getIssueAggregationQry = "select count(issue.id)totalissues, sum(case   when ClosedTime  is null and InvalidIssue = 0 and issuestate=0 then 1 else 0 end) as openSR,sum(case   when ClosedTime  is not null and InvalidIssue = 0 and issuestate=0 then 1 else 0 end) as closedSR ,apt.id apartmentid ,apt.builderid builderid from issue ,apartment apt where  ApartmentId = 1  and apt.id=issue.apartmentid and isenquiry=0;";
	String deleteServiceAggregationQuery = "delete from issueaggregation where  apartmentid=%d";
	String addServiceAggregationQuery = "INSERT INTO escalationaggregation "
			+ " (openescalations,closedescalations,totalescalations,groupzid,category,month,year) "
			+ " VALUES( %s, %s, %s, %s, '%s', %s, %s)";
	String insertIssueAggregationQry = " insert into issueaggregation (totalissues,openissues,closedIssues,escalatedissues,apartmentid,builderid,updateddate,month,year) values(%d,%d,%d,%d,%d,%d,'%s','%s','%s');";
	String paymentInsertQry = "insert into issueaggregation(month,year,escalatedissues,apartmentid,updateddate) values('%s','%s',%d,%d,'%s')";

	public void deleteServiceAggregation(Connection connection, int groupzId) {
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			String issueAggregationQry = String.format(
					deleteServiceAggregationQuery, groupzId);
			System.out.println("Delete Issue Aggregation From Groupz Query : "
					+ issueAggregationQry);
			stmt.execute(issueAggregationQry);
			System.out.println("IssueAggregation Deleted For GroupzId : "
					+ groupzId);
			ConnectionUtils.close(stmt);
		} catch (SQLException e) {
			e.printStackTrace();
			ConnectionUtils.close(stmt);
		}
	}

	public void saveServiceAggregation(Connection connection, int groupzId,
			String updatedDate) {
		Statement stmt = null;
		ResultSet rs = null;
		try {
			String aptFinalSrState = getAptDetails(groupzId, connection, false);
			int finalStateIntValue = getValueForState(aptFinalSrState,
					groupzId, connection);
			String getSRQuery = "select month(issue.fileddate)month,year(issue.fileddate)year,count(issue.id)totalissues, sum(case   when ClosedTime  is null and InvalidIssue = 0 and issuestate!="
					+ finalStateIntValue
					// ClosedTime is not null for closed SR's
					+ " then 1 else 0 end) as openSR,sum(case   when InvalidIssue = 0 and issuestate="
					+ finalStateIntValue
					+ " then 1 else 0 end) as closedSR ,apt.id apartmentid ,apt.builderid builderid from issue ,apartment apt where  ApartmentId = "
					+ groupzId
					+ "  and apt.id=issue.apartmentid and isenquiry=0 group by Month(issue.fileddate),year(issue.fileddate)";
			System.out.println("Get Sr Details Of Groupz Is : " + getSRQuery);
			JSONArray escalationCountArr = getEscalatedIssuesForGroupz(
					connection, groupzId);
			stmt = connection.createStatement();
			rs = stmt.executeQuery(getSRQuery);
			while (rs.next()) {
				int openSR = rs.getInt("openSR");
				int closedSR = rs.getInt("closedSR");
				int totalIssues = rs.getInt("totalissues");
				int apartmentid = rs.getInt("apartmentid");
				int builderId = rs.getInt("builderid");
				String month = rs.getString("month");
				String year = rs.getString("year");
				String addSRAggSet = String.format(insertIssueAggregationQry,
						totalIssues, openSR, closedSR, 0, apartmentid,
						builderId, updatedDate, month, year);
				System.out.println("Save IssueAggregation Qry is : "
						+ addSRAggSet);
				insertIssueAggregation(connection, addSRAggSet);
			}
			if (escalationCountArr != null && escalationCountArr.size() > 0) {
				for (int i = 0; i < escalationCountArr.size(); i++) {
					JSONObject escObj = escalationCountArr.getJSONObject(i);
					String month = escObj.getString("month");
					String year = escObj.getString("year");
					int noOfEscalations = escObj.getInt("totalescalations");
					if (checkColumnExistForMonthAndYear(
							escObj.getString("month"),
							escObj.getString("year"), groupzId, connection)) {
						System.out
								.println("Updating For Existing Month And Year");
						updateForEscalationsOnMonthAndYear(noOfEscalations,
								month, year, groupzId, updatedDate, connection);
					} else {
						System.out
								.println("Inserting For Existing Month And Year");
						insertForEscalationsOnMonthAndYear(noOfEscalations,
								month, year, groupzId, updatedDate, connection);
					}
				}
			}
			ConnectionUtils.close(stmt);
		} catch (SQLException e) {
			e.printStackTrace();
			ConnectionUtils.close(stmt);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private JSONArray getEscalatedIssuesForGroupz(Connection con, int groupzId) {
		JSONArray escalationList = new JSONArray();
		Statement stmt = null;
		ResultSet rs = null;
		String countEscalationQuery = "select month(createddate)month,year(createddate)year, count( distinct i.id) totalescalations from issue i,issueescalations ie where i.id=ie.modulereferenceid and i.apartmentid="
				+ groupzId
				+ " and  ie.levelofescalation>0 and ie.istriggered=1 group by month(createddate),year(createddate)";
		try {
			stmt = con.createStatement();
			rs = stmt.executeQuery(countEscalationQuery);
			while (rs.next()) {
				JSONObject escalationObj = new JSONObject();
				escalationObj.put("month", rs.getInt("month"));
				escalationObj.put("year", rs.getInt("year"));
				escalationObj.put("totalescalations",
						rs.getInt("totalescalations"));
				escalationList.add(escalationObj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return escalationList;
	}

	private String getAptDetails(int apartmentId, Connection con,
			boolean isForServiceRequestOfAptSet) {
		Statement stmt = null;
		ResultSet rs = null;
		String query = null;
		try {
			if (isForServiceRequestOfAptSet == false) {
				query = "select srfinalstate from apartment_settings where apartmentid="
						+ apartmentId + " limit 1";
			} else {
				query = "select servicerequest from apartment_settings where apartmentid="
						+ apartmentId + " limit 1";
			}
			System.out.println("Query Is : " + query);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			System.out.println("DEBUG POINT --------------------------------");
			if (rs.next()) {
				if (isForServiceRequestOfAptSet) {
					if (rs.getString("servicerequest") != null) {
						// ConnectionUtils.close(stmt);
						System.out.println("Needed SR : "
								+ rs.getString("servicerequest"));
						return rs.getString("servicerequest");
					}
				} else {
					if (rs.getString("srfinalstate") != null) {
						// ConnectionUtils.close(stmt);
						return rs.getString("srfinalstate");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public int getValueForState(String stateName, int aptId, Connection con) {
		int value = -1;
		String name = "";
		String str = getAptDetails(aptId, con, true);
		System.out.println("SERVICE REQUEST IN GROUPZ IS : " + str);
		JSONObject obj = JSONObject.fromObject(str);
		JSONObject listObj = obj.getJSONObject("issuestatelist");
		JSONArray arr = listObj.getJSONArray("issuestate");
		for (int i = 0; i < arr.size(); i++) {
			JSONObject stateObj = arr.getJSONObject(i);
			name = stateObj.getString("name");
			if (name.equalsIgnoreCase(stateName)) {
				value = Integer.valueOf(stateObj.getString("value"));
			}
		}
		return value;
	}

	public void insertIssueAggregation(Connection connection, String query) {
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			stmt.executeUpdate(query);
			ConnectionUtils.close(stmt);
		} catch (SQLException e) {
			e.printStackTrace();
			ConnectionUtils.close(stmt);
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean checkColumnExistForMonthAndYear(String month, String year,
			int groupzId, Connection con) {
		boolean doesColumnExist = false;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			String query = "select * from issueaggregation where month='"
					+ month + "'" + " and year='" + year + "'"
					+ " and apartmentid=" + groupzId;
			System.out.println("Column Exist Check Query Is : " + query);
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return doesColumnExist;
	}

	private void updateForEscalationsOnMonthAndYear(int noOfEScalations,
			String month, String year, int groupzId, String updatedDate,
			Connection con) {
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			String query = "update issueaggregation set escalatedissues="
					+ noOfEScalations + ",updateddate='" + updatedDate
					+ "' where month='" + month + "'" + " and year='" + year
					+ "'" + " and apartmentid=" + groupzId;
			System.out
					.println("Update Issue For Month And Year For Groupz Id : "
							+ groupzId + " Is :" + query);
			stmt.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void insertForEscalationsOnMonthAndYear(int noOfEscalations,
			String month, String year, int groupzId, String updatedDate,
			Connection con) {
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			String query = String.format(paymentInsertQry, month, year,
					noOfEscalations, groupzId, updatedDate);
			System.out.println("Payment Insert To Month And Year Query Is : "
					+ query);
			stmt.executeUpdate(query);
			ConnectionUtils.close(stmt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
