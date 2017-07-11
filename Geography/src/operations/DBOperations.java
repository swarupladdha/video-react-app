package operations;

import java.sql.ResultSet;
import java.sql.Statement;

import manager.GlobalVariables;
import net.minidev.json.JSONArray;
import net.sf.json.JSONObject;

public class DBOperations {

	public JSONArray getCountryDetails(String query, java.sql.Connection con) {
		ResultSet rs = null;
		Statement stmt = null;
		try {
			JSONArray array = new JSONArray();
			JSONObject object = new JSONObject();

			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			while (rs.next()) {
				object = new JSONObject();
				object.put(GlobalVariables.COUNTRYID_TAG, rs.getInt("Id"));
				object.put("countryname", rs.getString("countryname"));
				object.put("countrycode", rs.getString("countrycode"));
				object.put("latitude", rs.getString("latitude"));
				object.put("longitude", rs.getString("longitude"));
				array.add(object);
			}

			if (array.size() > 0) {
				return array;
			}

		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
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
