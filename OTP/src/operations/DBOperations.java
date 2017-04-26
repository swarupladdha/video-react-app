package operations;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DBOperations {

	public List<Integer> getIntegerList(Connection dbConnection, String query) {
		Statement stmt = null;
		ResultSet rs = null;
		List<Integer> intList = new ArrayList<Integer>();

		System.out.println("Get Integers List Query is :" + query);

		try {
			stmt = dbConnection.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				intList.add(rs.getInt(1));

			}
			return intList;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null) {
					stmt.close();

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return intList;

	}

	public void executeUpdate(String query, Connection conn) {
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			stmt.executeUpdate(query);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}

	}

	public String getOldOTP(String query, Connection conn) {
		Statement stmt = null;
		ResultSet rs = null;
		String otp = "";
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				otp = rs.getString("OriginalOtp");
				return otp;

			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (rs != null)
					rs.close();
				if (stmt != null) {
					stmt.close();

				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;

	}

}
