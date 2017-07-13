package operations;

import java.sql.ResultSet;
import java.sql.Statement;
import manager.GlobalVariables;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DBOperations {

	public JSONArray getGeographyDetails(String query, java.sql.Connection con, int listType) {
		ResultSet rs = null;
		Statement stmt = null;
		try {
			JSONArray array = new JSONArray();
			JSONObject object = new JSONObject();

			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			if (listType == GlobalVariables.COUNTRY_LIST) {
				while (rs.next()) {
					object = new JSONObject();
					object.put(GlobalVariables.COUNTRYID_TAG, rs.getInt("Id"));
					object.put("countryname", rs.getString("countryname"));
					object.put("countrycode", rs.getString("countrycode"));
					object.put("currencyname", rs.getString("currencyname"));
					object.put("currencycode", rs.getString("currencycode"));
					object.put("latitude", rs.getString("latitude"));
					object.put("longitude", rs.getString("longitude"));
					array.add(object);
				}
			} else if (listType == GlobalVariables.STATE_LIST) {
				while (rs.next()) {
					object = new JSONObject();
					object.put("stateid", rs.getInt("id"));
					object.put("countryid", rs.getInt("countryid"));
					object.put("statename", rs.getString("statename"));
					object.put("latitude", rs.getString("latitude"));
					object.put("longitude", rs.getString("longitude"));
					array.add(object);
				}
			} else if (listType == GlobalVariables.CITY_LIST) {
				while (rs.next()) {
					object = new JSONObject();
					object.put("cityid", rs.getInt("id"));
					object.put("stateid", rs.getInt("stateid"));
					object.put("cityname", rs.getString("cityname"));
					object.put("latitude", rs.getString("latitude"));
					object.put("longitude", rs.getString("longitude"));
					object.put("stdcode", rs.getString("stdcode"));
					array.add(object);

				}
			} else if (listType == GlobalVariables.AREA_LIST) {
				while (rs.next()) {
					object = new JSONObject();
					object.put("areaid", rs.getInt("id"));
					object.put("cityid", rs.getInt("cityid"));
					object.put("areaname", rs.getString("areaname"));
					object.put("stdcode", rs.getString("stdcode"));
					object.put("zip", rs.getString("zip"));
					object.put("latitude", rs.getString("latitude"));
					object.put("longitude", rs.getString("longitude"));
					array.add(object);
				}
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
