package com.groupz.followup.operations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.groupz.followup.utils.ConnectionUtils;

public class GroupzBaseAggOperation {
	static final Logger logger = Logger.getLogger(GroupzBaseAggOperation.class);
	String deleteGroupzBaseDetails = "delete from groupzbaseaggregation where Builderid=(select builderid from apartment where id=%d)";
	String insertGroupzBaseDetails = "insert into groupzbaseaggregation (totalaccounts,totalactiveaccounts,updateddate,Builderid) values(%d,%d,'%s',%d);";

	public void deleteGroupzDetailsOnBase(Connection connection, int groupzId) {
		Statement stmt = null;
		try {
			String deleteGroupzBaseDetails = "delete from groupzbaseaggregation where Builderid=(select builderid from apartment where id="
					+ groupzId + ")";
			System.out.println("Delete GroupzBase Details Query Is : "
					+ deleteGroupzBaseDetails);
			stmt = connection.createStatement();
			boolean rs = stmt.execute(deleteGroupzBaseDetails);
			logger.error("GroupzBase Details Deleted For builder Of Groupz Id : "
					+ groupzId);
			ConnectionUtils.close(stmt);
		} catch (Exception e) {
			logger.error("Excepton Caught in HeadCountByLocation  Class");
			logger.error(e.getMessage());
			e.printStackTrace();
			ConnectionUtils.close(stmt);
		}
	}

	public void saveGroupzBaseDetails(Connection connection, int groupzId,
			String updatedDate) {
		Statement stmt = null;
		try {
			String Query = "select count(apt.id)total,SUM(apt.enabled)enabled,count(apt.id)-SUM(apt.enabled)disabled,b.id builderid from apartment apt,builder b where apt.builderid=b.id and apt.builderid=(select builderid from apartment where id="
					+ groupzId + ")";
			System.out.println("Final select Query Is : " + Query);
			stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery(Query);
			while (rs.next()) {
				int builderid = rs.getInt("builderid");
				int totalAccounts = rs.getInt("total");
				int activeAccounts = rs.getInt("enabled");
				// int contactCount = rs.getInt("disabled");
				formInsertAndSaveGroupzBaseDetails(connection, builderid,
						totalAccounts, activeAccounts, updatedDate);
			}
			ConnectionUtils.close(stmt);
		} catch (Exception e) {
			logger.error("Excepton Caught in HeadCountByLocation  Class");
			logger.error(e.getMessage());
			e.printStackTrace();
			ConnectionUtils.close(stmt);
		}
	}

	public void formInsertAndSaveGroupzBaseDetails(Connection con,
			int builderId, int totalAccounts, int activeAccounts,
			String updatedDate) {
		Statement stmt = null;
		try {
			System.out.println("Inside Inserting New Record For member");
			stmt = con.createStatement();
			String finalInsertQry = String.format(insertGroupzBaseDetails,
					totalAccounts, activeAccounts, updatedDate, builderId);
			System.out.println("Final groupzBaseDetails Insert Query Is : "
					+ finalInsertQry);
			stmt.executeUpdate(finalInsertQry);
			ConnectionUtils.close(stmt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
