package com.groupz.followup.operations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

	 String deleteFeeAggregationQuery = "delete from feeaggregation where year = %s and month = %s and apartmentId=%s";

	 String addFeeAggregationQuery = "insert into feeaggregation "
			+ "(amount,apartmentId,division,month,paymentStatus,paymentType,subDivision,year)"
			+ "values(%s,%s,%s,%s,'%s','%s','%s',%s)";

	
	public void deleteFeeAgg(Connection connection, String year, String month, int groupzId)
	{
	Statement stmt = null;
	try {
		stmt = connection.createStatement();
		System.out.println("Inside deleteFeeAgg() of FeeAggregationOperations deleting from feeaggregation...");
		String deleteFeeAggQuery = String.format(deleteFeeAggregationQuery, year, month,groupzId);
	//	System.out.println(deleteFeeAggQuery);
		boolean deletedFeeAggSet = stmt.execute(deleteFeeAggQuery);
		
		if (deletedFeeAggSet) {
			System.out.println("Inside deleteFeeAgg() of FeeAggregationOperations deleted from feeaggregation...");
		}
		ConnectionUtils.close(stmt);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		ConnectionUtils.close(stmt);
		e.printStackTrace();
	}
	
	}

	public void saveFeeAgg(Connection connection, String year, String month, int groupzId)
	{
	
	
	Statement stmt = null;
	try {
		String getFeeAggQuery = String.format(getFeeAggregationQuery, year, month, groupzId);
		System.out.println(getFeeAggQuery);
		stmt = connection.createStatement();
		ResultSet getFeeAggSet = stmt.executeQuery(getFeeAggQuery);
		System.out.println("Inside getFeeAgg() of FeeAggregationOperations inserting into feeaggregation...");
	
		while (getFeeAggSet.next()) {
			String division = getFeeAggSet.getString("Division");
			String subDivision = getFeeAggSet.getString("SubDivision");
			String paymentType = getFeeAggSet.getString("PaymentType");
			String paymentStatus = getFeeAggSet.getString("statusPayment");
			String amount = getFeeAggSet.getString("amount");
			
		//	System.out.println(division + "" + subDivision + ""+ paymentType + "" + paymentStatus + ""+ amount);

			stmt = connection.createStatement();
			String addFeeAggSet = String.format(addFeeAggregationQuery, amount, groupzId,
					division, month, paymentStatus,paymentType, subDivision, year);			
			System.out.println(addFeeAggregationQuery);
			
			boolean addFeeAgg = stmt.execute(addFeeAggSet);
		//	System.out.println(addFeeAgg);
		}
		ConnectionUtils.close(stmt);
		System.out.println("Inside getFeeAgg() of FeeAggregationOperations inserted into feeaggregation...");
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		ConnectionUtils.close(stmt);
		e.printStackTrace();
	}
	
		}
}
