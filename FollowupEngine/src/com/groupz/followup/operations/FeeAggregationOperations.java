package com.groupz.followup.operations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import net.sf.json.JSONArray;
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
	String paymentInsertQry = "insert into feeaggregation(month,year,totalpaid,apartmentid) values('%s','%s',%f,%d);";

	String feeAggregationInsertQry = "insert into feeaggregation (totalincome,totalbalance,totaldiscount,totalexpense,apartmentId,Builderid,updateddate,month,year) values(%f,%f,%f,%f,%d,%d,'%s','%s','%s')";

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
			JSONArray totalAndDiscountArr = getGrandTotalAndDiscountForGroupz(
					groupzId, connection);
			JSONArray paymentArray = getAmountPaidForGroupzId(groupzId,
					connection);
			float expense = getExpenseForGroupzId(groupzId, connection);
			if (totalAndDiscountArr != null && totalAndDiscountArr.size() > 0) {
				for (int i = 0; i < totalAndDiscountArr.size(); i++) {
					JSONObject totalAndDiscountObj = totalAndDiscountArr
							.getJSONObject(i);
					String insertFeeAgQry = String.format(
							feeAggregationInsertQry, Float
									.valueOf(totalAndDiscountObj
											.getString("totalincome")), Float
									.valueOf(totalAndDiscountObj
											.getString("totaldue")), Float
									.valueOf(totalAndDiscountObj
											.getString("totaldiscount")),
							expense, groupzId, totalAndDiscountObj
									.getInt("builderid"), updatedDate,
							totalAndDiscountObj.getString("month"),
							totalAndDiscountObj.getString("year"));
					System.out.println("Final Insert Query Is : "
							+ insertFeeAgQry);
					stmt.execute(insertFeeAgQry);
				}
			}
			if (paymentArray != null && paymentArray.size() > 0) {
				for (int j = 0; j < paymentArray.size(); j++) {
					JSONObject payObj = paymentArray.getJSONObject(j);
					String month = payObj.getString("month");
					String year = payObj.getString("year");
					float amountPaid = Float
							.valueOf(payObj.getString("amount"));
					boolean columnExist = checkColumnExistForMonthAndYear(
							month, year, groupzId, connection);
					if (columnExist) {
						System.out.println("Coulumn Exist");
						updateForPaymentPaidOnMonthAndYear(amountPaid, month,
								year, groupzId, connection);
					} else {
						System.out.println("Coulumn Does Not Exist");
						insertForPaymentPaidOnMonthAndYear(amountPaid, month,
								year, groupzId, connection);
					}
				}
			}

			ConnectionUtils.close(stmt);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ConnectionUtils.close(stmt);
		}
	}

	private JSONArray getAmountPaidForGroupzId(int groupzId, Connection con) {
		JSONArray paymentArr = new JSONArray();
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = con.createStatement();
			String query = "select month(paymentdate)month,year(paymentdate)year,sum(Amount)amount from payment where apartmentid="
					+ groupzId
					+ " and paymentdate is not null and cleared=1 and bounce=0 and cancelled=0  group by month(paymentdate),year(paymentdate) ";
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				JSONObject payObj = new JSONObject();
				payObj.put("month", rs.getString("month"));
				payObj.put("year", rs.getString("year"));
				payObj.put("amount", rs.getFloat("amount"));
				paymentArr.add(payObj);
			}
			ConnectionUtils.close(stmt);
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
		}
		return paymentArr;
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

	public JSONArray getGrandTotalAndDiscountForGroupz(int groupzId,
			Connection con) {
		JSONArray feeArr = new JSONArray();
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			String query = "select month(udt.createddate)month,year(udt.createddate)year,apt.builderid builderid,sum(case when udt.voidItem=0 then udt.grandtotal else 0 end )totalincome,sum(case when udt.voiditem=0 then udt.grandtotal-(udt.amountpaid-udt.discount) else 0 end )totaldue,sum(case when udt.voiditem=0 then udt.discount else 0 end)totaldiscount from userduetracker udt ,apartment apt,builder b where udt.apartmentid=apt.id and b.id=apt.builderid and apartmentid="
					+ groupzId
					+ " group by month(udt.createddate),year(udt.createddate)";
			System.out.println("Fee Query Is : " + query);
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next()) {
				JSONObject feeObj = new JSONObject();
				feeObj = new JSONObject();
				feeObj.put("totalincome",
						Math.round(rs.getFloat("totalincome")));
				feeObj.put("totaldiscount",
						Math.round(rs.getFloat("totaldiscount")));
				feeObj.put("totaldue", Math.round(rs.getFloat("totaldue")));
				feeObj.put("builderid", rs.getInt("builderid"));
				feeObj.put("month", rs.getString("month"));
				feeObj.put("year", rs.getString("year"));
				feeArr.add(feeObj);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return feeArr;
	}

	private boolean checkColumnExistForMonthAndYear(String month, String year,
			int groupzId, Connection con) {
		boolean doesColumnExist = false;
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			String query = "select * from feeaggregation where month='" + month
					+ "'" + " and year='" + year + "'" + " and apartmentid="
					+ groupzId;
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

	private void updateForPaymentPaidOnMonthAndYear(float amountPaid,
			String month, String year, int groupzId, Connection con) {
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			String query = "update feeaggregation set totalpaid=" + amountPaid
					+ " where month='" + month + "'" + " and year='" + year
					+ "'" + " and apartmentid=" + groupzId;
			System.out
					.println("Update Payment For Month And Year For Groupz Id : "
							+ groupzId + " Is :" + query);
			stmt.executeUpdate(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void insertForPaymentPaidOnMonthAndYear(float amountPaid,
			String month, String year, int groupzId, Connection con) {
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			String query = String.format(paymentInsertQry, month, year,
					amountPaid, groupzId);
			System.out.println("Payment Insert To Month And Year Query Is : "
					+ query);
			stmt.executeUpdate(query);
			ConnectionUtils.close(stmt);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
