package com.stocking.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.stocking.utils.ConnectionManager;

import net.sf.json.JSONObject;

public class RecordsDao {
	
	public int getRevordId(Connection connection,String date,String ticker) {
		Statement stmt = null;
		ResultSet res = null;
		try {
			String query = "select id from records where date='"+date+"' and ticker='"+ticker+"'";
			stmt = connection.createStatement();
			res = stmt.executeQuery(query);
			if(res.next()) {
				return res.getInt("id");
			}
			else {
				return -1;
			}
		}catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		finally {
			ConnectionManager.closeStatement(stmt);
			ConnectionManager.closeResultSet(res);
		}
	}
	
	public void insertIntoRecords(Connection connection,JSONObject obj) {
		Statement stmt = null;
		try {
			if(obj !=null && !obj.isEmpty()) {
				String ticker = obj.getString("ticker");
				String date = obj.getString("date");
				String high = obj.getString("high");
				String low = obj.getString("low");
				String open = obj.getString("open");
				String close = obj.getString("close");
				String adjustclose = obj.getString("adjustclose");
				String volume = obj.getString("volume");
				
				String query = "insert into records (ticker,date,high,low,open,close,adjustclose,volume) values ('"
						+ticker+"','"+date+"','"+high+"','"+low+"','"+open+"','"+close+"','"+adjustclose+"','"+volume+"')";
				stmt = connection.createStatement();
				stmt.executeUpdate(query);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			ConnectionManager.closeStatement(stmt);
		}
	}

	public void updateRecord(Connection connection, JSONObject obj) {
		Statement stmt = null;
		try {
			if(obj !=null && !obj.isEmpty()) {
				String ticker = obj.getString("ticker");
				String date = obj.getString("date");
				String high = obj.getString("high");
				String low = obj.getString("low");
				String open = obj.getString("open");
				String close = obj.getString("close");
				String adjustclose = obj.getString("adjustclose");
				String volume = obj.getString("volume");
				
				String query = "update records set high='"+high+"',low='"+low+"',open='"+open+"',close='"+close+"',adjustclose='"+adjustclose+"',volume='"+volume+"' where ticker='"+ticker+"' and date='"+date+"'";
				stmt = connection.createStatement();
				stmt.executeUpdate(query);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			ConnectionManager.closeStatement(stmt);
		}
	}
}
