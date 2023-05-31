package com.flexical.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.flexical.model.ResourceAvailabilityBean;
import com.flexical.util.AllKeys;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class ResourceSettingsDao {

	public int checkResourceExist(String resourceId, String vendorId, int clientId, Connection dbConnection) {
		String sql = "Select * from vendorresource where ClientId = ? and VendorId = ? and ResourceId = ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = dbConnection.prepareStatement(sql);
			ps.setInt(1, clientId);
			ps.setString(2, vendorId);
			ps.setString(3, resourceId);
			rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt("Id");
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

	public int addResourceVendor(String resourceId, String vendorId, int clientId, int clientVendorId, int slotTimeId,
			String timeZone, Connection dbConnection) {
		String sql = "insert into vendorresource (ClientId, VendorId, ResourceId, ClientVendorId, SlotTimeId, TimeZone) values "
				+ "(?, ?, ?, ?, ?, ?)";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = dbConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, clientId);
			ps.setString(2, vendorId);
			ps.setString(3, resourceId);
			ps.setInt(4, clientVendorId);
			ps.setInt(5, slotTimeId);
			ps.setString(6, timeZone);
			int affectedRows = ps.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Could not insert into clientvendor");
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

	public int addResourceAvailabilitySettings(int clientId, String vendorId, int clientVendorId, String resourceId,
			int resourceVendorId, int weekdayId, String startTime, String endTime, int working,
			Connection dbConnection) {
		String sql = "insert into resourcegeneralavailability (ClientId, VendorId, ClientVendorId, ResourceId, VendorResourceId, CreatedDate, UpdatedDate, weekdayId, startTime, endTime, working)"
				+ " values (?, ?, ?, ?, ?, NOW(), NOW(), ?, ?, ?, ?)";
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = dbConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, clientId);
			ps.setString(2, vendorId);
			ps.setInt(3, clientVendorId);
			ps.setString(4, resourceId);
			ps.setInt(5, resourceVendorId);
			ps.setInt(6, weekdayId);
			ps.setString(7, startTime);
			ps.setString(8, endTime);
			ps.setInt(9, working);
			int affectedRows = ps.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Could not insert into resourcegeneralavailability");
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

	public JSONArray getResourceAvailabilitySettings(int clientId, String vendorId, String resourceId,
			Connection dbConnection) {
		JSONObject jsonObject = new JSONObject();
		// String query = "select ClientId, VendorId, ResourceId,
		// JSON_OBJECT('weekdayId', weekdayId, 'startTime', startTime, 'endTime',
		// endTime) AS json_object, 'availabile' as status from
		// resourcegeneralavailability where ClientId = "+clientId+" and ResourceId =
		// '"+resourceId+"' and VendorId = '"+vendorId+"' and working=1 union "
		// + "select ClientId, VendorId, CASE WHEN ResourceId = '"+resourceId+"' THEN
		// ResourceId ELSE UserId END AS ResourceId, JSON_OBJECT('weekdayId',
		// DAYOFWEEK(StartTime),'startTime', TIME(StartTime), 'endTime', TIME(endTime)),
		// 'booked' as status from booking where ClientId = "+clientId+" and ResourceId
		// = '"+resourceId+"' and VendorId = '"+vendorId+"'";
		String query = "select * from week";

		// String query = "select a.ClientId, a.VendorId, a.ResourceId, a.weekdayId,
		// a.startTime as availableStartTime, a.endTime as availableEndTime,
		// time(b.StartTime) as bookedStartTime, time(b.EndTime) as bookedEndTime from
		// resourcegeneralavailability a left join booking b on a.weekdayId =
		// (DAYOFWEEK(b.StartTime) + 5) % 7 + 2 where a.ClientId = "+clientId+" and
		// a.ResourceId = '"+resourceId+"' and a.VendorId = '"+vendorId+"'";
		PreparedStatement ps = null, ps1 = null;
		ResultSet rs = null, rs1 = null;
		ArrayList objectList = new ArrayList<>();
		JSONObject obj = new JSONObject();
		JSONArray jsonArray = new JSONArray();
		try {
			System.out.println("query " + query);
			ps = dbConnection.prepareStatement(query);
			rs = ps.executeQuery();

			while (rs.next()) {
				int weekId = rs.getInt("Id");
				String query1 = "select Id, ClientId, VendorId, ResourceId, weekdayId, startTime as startTime, endTime as endTime, 'available' as status from resourcegeneralavailability where ClientId = "
						+ clientId + " and ResourceId = '" + resourceId + "'and VendorId = '" + vendorId
						+ "' and weekdayId = " + weekId + " union "
						+ "select b.Id, b.ClientId, b.VendorId, CASE when b.UserId = '" + resourceId
						+ "' then b.UserId else b.ResourceId end as ResourceId, (DAYOFWEEK(b.StartTime) + 5) % 7 + 2 as weekdayId, time(b.StartTime) as startTime, time(b.EndTime) as endTime, 'booked' as status from booking b left join week w on (DAYOFWEEK(b.StartTime) + 5) % 7 + 2 = w.Id where w.Id = "
						+ weekId + " and b.ClientId = " + clientId + " and b.ResourceId = '" + resourceId
						+ "' or UserId = '" + resourceId + "' and b.VendorId = '" + vendorId + "'";
				ps1 = dbConnection.prepareStatement(query1);
				rs1 = ps1.executeQuery();
				while (rs1.next()) {
					obj.put(AllKeys.CLIENTID_KEY, rs1.getDouble(AllKeys.CLIENTID_KEY));
					obj.put(AllKeys.VENDORID_KEY, rs1.getString(AllKeys.VENDORID_KEY));
					obj.put(AllKeys.RESOURCEID_KEY, rs1.getString(AllKeys.RESOURCEID_KEY));
					obj.put(AllKeys.WEEKDAYID_KEY, rs1.getString(AllKeys.WEEKDAYID_KEY));
					obj.put("startTime", rs1.getString("startTime"));
					obj.put("endTime", rs1.getString("endTime"));
					obj.put("status", rs1.getString("status"));
					//obj.put("bookedEndTime", rs1.getString("bookedEndTime"));
					if(!obj.isEmpty()) {
						jsonArray.add(obj);
					}
				}
				// obj.put(AllKeys.STATUS_KEY, rs.getString(AllKeys.STATUS_KEY));
				// objectList.add(obj);
				
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

		return jsonArray;
	}
}
