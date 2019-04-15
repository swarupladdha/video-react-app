package com.services;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ConnectionUtils {
	
	public static void closeStatement (Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void closePreparedStatement (PreparedStatement pStmt) {
		try {
			if (pStmt != null) {
				pStmt.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void closeResultSet (ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
