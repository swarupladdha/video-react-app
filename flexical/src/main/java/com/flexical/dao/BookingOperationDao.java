package com.flexical.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.flexical.util.AllKeys;
import com.flexical.util.RestUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class BookingOperationDao {
	RestUtils utils = new RestUtils();

	public int addBookingSchedule(int clientId, String vendorId, String resourceId, String userId, String startTime,
			String slotTime, Connection dbConnection) {

		String sql = "insert into booking (ClientId, VendorId, ResourceId, UserId, CreatedDate, StartTime, EndTime) values "
				+ "(?, ?, ?, ?, UTC_TIMESTAMP,  ?, DATE_ADD('" + startTime + "', INTERVAL '" + slotTime
				+ "' HOUR_SECOND))";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = dbConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, clientId);
			ps.setString(2, vendorId);
			ps.setString(3, resourceId);
			ps.setString(4, userId);
			ps.setString(5, startTime);
			int affectedRows = ps.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Could not insert into clientscreat");
			}
			rs = ps.getGeneratedKeys();
			if (rs.next()) {
				return (int) rs.getLong(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return -1;
	}

	public boolean checkAvailability(int clientId, String vendorId, String resourceId, String userId,
			String startTime, String slotTime, Connection dbConnection) {
		boolean result = false;
		/*String sql = "select b.* from booking b, vendorresource rv where b.ClientId = ? and "
				+ "b.VendorId = ? and (b.ResourceId = rv.ResourceId or b.UserId = rv.ResourceId) and "
				+ "(rv.ResourceId = ? or rv.ResourceId= ? ) and (? < b.EndTime) "
				+ "AND (DATE_ADD('" + startTime + "', INTERVAL '" + slotTime 
				+ "' HOUR_SECOND) > b.StartTime) group by b.Id";*/
		String sql = "SELECT distinct b.*, rga.working "
		+ "FROM vendorresource rv, booking b "
		+ "LEFT JOIN resourcegeneralavailability rga ON b.ClientId = rga.ClientId "
		+ "AND b.VendorId = rga.VendorId AND b.ResourceId = rga.ResourceId or b.UserId = rga.ResourceId "
		+ "AND rga.weekdayId = (DAYOFWEEK('"+startTime+"') + 5) % 7 + 2 "
		+ "AND rga.startTime >= TIME('"+startTime+"') "
		+ "WHERE b.ClientId = ? AND b.VendorId = ? AND (rv.ResourceId = ? OR rv.ResourceId = ?) "
		+ "AND (? < b.EndTime) AND (DATE_ADD('"+startTime+"', INTERVAL ? HOUR_SECOND) > b.StartTime) "
		+ "GROUP BY b.Id, rga.Id";
		PreparedStatement ps = null;
		ResultSet rs;
		try {
			ps = dbConnection.prepareStatement(sql);
			ps.setInt(1, clientId);
			ps.setString(2, vendorId);
			ps.setString(3, resourceId);
			ps.setString(4, userId);
			ps.setString(5, startTime);
			ps.setString(6, slotTime);
			rs = ps.executeQuery();
			if(rs.next()) {
				result = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public JSONArray getBookingDetails(int clientId, String vendorId, String userId, Connection dbConnection) {
		JSONArray result = new JSONArray();
		JSONObject obj = new JSONObject();
		String sql = "select * from booking where ClientId = ? and VendorId = ? and (ResourceId = ? or UserId = ?)";
		PreparedStatement ps = null;
		ResultSet rs;
		try {
			ps = dbConnection.prepareStatement(sql);
			ps.setInt(1, clientId);
			ps.setString(2, vendorId);
			ps.setString(3, userId);
			ps.setString(4, userId);
			rs = ps.executeQuery();
			while (rs.next()) {
				obj.put(AllKeys.CLIENTID_KEY, rs.getInt(AllKeys.CLIENTID_KEY));
				obj.put(AllKeys.VENDORID_KEY, rs.getString(AllKeys.VENDORID_KEY));
				obj.put(AllKeys.RESOURCEID_KEY, rs.getString(AllKeys.RESOURCEID_KEY));
				obj.put(AllKeys.USERID_KEY, rs.getString(AllKeys.USERID_KEY));
				obj.put(AllKeys.STARTTIME_KEY, rs.getString(AllKeys.STARTTIME_KEY));
				obj.put(AllKeys.ENDTIME_KEY, rs.getString(AllKeys.ENDTIME_KEY));
				result.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return result;
	}

}
