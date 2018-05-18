package com.groupz.followup.operations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.json.JSONObject;

import com.groupz.followup.utils.ConnectionUtils;

public class FeeAggregationOperations {

	String getFeeAggregationQuery = " SELECT Division,SubDivision,"
			+ "case   when paymentType  = 0 then 'cash'"
			+ "   when paymentType  = 1 then 'cheque'"
			+ "   when paymentType  = 2 then 'online'"
			+ "   else 'no one'"
			+ "end as paymentType ,"

			+ "case "
			+ "   when cleared   = 1 then 'cleared'"
			+ "	  when bounce    = 1 then 'bounced'"
			+ "	  when cancelled = 1 then 'cancelled'"
			+ "	  else 'to be deposited'"
			+ "	end as statuspayment,apartmentId,  sum(amount)as amount "
			+ " from payment where YEAR(receiptDate) = '%s' AND MONTH(receiptDate) = '%s' and "
			+ "ApartmentId = '%s'"
			+ " group by paymentType,statusPayment,bounce,cleared,cancelled";

	String deleteFeeAggregationQuery = "delete from feeaggregation where apartmentId=%d";

	String addFeeAggregationQuery = "insert into feeaggregation "
			+ "(amount,apartmentId,division,month,paymentStatus,paymentType,subDivision,year)"
			+ "values(%s,%s,%s,%s,'%s','%s','%s',%s)";

	String feeAggregationInsertQry = "insert into feeaggregation (totalincome,totalpaid,totalbalance,totaldiscount,totalexpense,apartmentId,Builderid,updateddate) values(%f,%f,%f,%f,%f,%d,%d,'%s')";

	public void deleteFeeAgg(Connection connection, String year, String month,
			int groupzId) {
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			String deleteFeeAggQuery = String.format(deleteFeeAggregationQuery,
					groupzId);
			stmt.execute(deleteFeeAggQuery);
			System.out.println("Fee Aggregation Deleted For Groupz Id : "
					+ groupzId);
			ConnectionUtils.close(stmt);
		} catch (SQLException e) {
			e.printStackTrace();
			ConnectionUtils.close(stmt);
		}
	}

	public void saveFeeAgg(Connection connection, int groupzId,
			String updatedDate) {
		Statement stmt = null;
		try {
			stmt = connection.createStatement();
			JSONObject totalAndDiscountObj = getGrandTotalAndDiscountForGroupz(
					groupzId, connection);
			float payment = getAmountPaidForGroupzId(groupzId, connection);
			float expense = getExpenseForGroupzId(groupzId, connection);
			System.out.println("Fee Aggregated Details Are : "
					+ totalAndDiscountObj.toString() + " paymentPaid : "
					+ payment + "Expense Is : " + expense);
			String insertFeeAgQry = String
					.format(feeAggregationInsertQry, Float
							.valueOf(totalAndDiscountObj
									.getString("totalincome")), payment,
							Float.valueOf(totalAndDiscountObj
									.getString("totaldue")), Float
									.valueOf(totalAndDiscountObj
											.getString("totaldiscount")),
							expense, groupzId, totalAndDiscountObj
									.getInt("builderid"), updatedDate);
			System.out.println("Final Insert Query Is : " + insertFeeAgQry);
			stmt.execute(insertFeeAgQry);
			ConnectionUtils.close(stmt);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ConnectionUtils.close(stmt);
		}
	}

	private float getAmountPaidForGroupzId(int groupzId, Connection con) {
		float payment = 0;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			String query = "select sum(p.amount)payment from payment p where p.bounce=0 and p.cancelled=0 and p.cleared=1 and apartmentid="
					+ groupzId;
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (rs.getFloat("payment") > 0) {
					return Math.round(rs.getFloat("payment"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return payment;
	}

	private float getExpenseForGroupzId(int groupzId, Connection con) {
		float payment = 0;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			String query = "select sum(expenseAmount)totalexpense from expense where cancelled=0 and apartmentid="
					+ groupzId;
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				if (rs.getFloat("totalexpense") > 0) {
					return Math.round(rs.getFloat("totalexpense"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return payment;
	}

	public JSONObject getGrandTotalAndDiscountForGroupz(int groupzId,
			Connection con) {
		JSONObject feeObj = null;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			String query = "select apt.builderid builderid,sum(case when udt.voidItem=0 then udt.grandtotal else 0 end )totalincome,sum(case when udt.voiditem=0 then udt.grandtotal-(udt.amountpaid-udt.discount) else 0 end )totaldue,sum(case when udt.voiditem=0 then udt.discount else 0 end)totaldiscount from userduetracker udt ,apartment apt,builder b where udt.apartmentid=apt.id and b.id=apt.builderid and apartmentid="
					+ groupzId + " group by apt.id";
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				feeObj = new JSONObject();
				feeObj.put("totalincome",
						Math.round(rs.getFloat("totalincome")));
				feeObj.put("totaldiscount",
						Math.round(rs.getFloat("totaldiscount")));
				feeObj.put("totaldue", Math.round(rs.getFloat("totaldue")));
				feeObj.put("builderid", rs.getInt("builderid"));
				return feeObj;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return feeObj;
	}
}
