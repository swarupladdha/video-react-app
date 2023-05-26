package com.flexical.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import net.sf.json.JSONObject;

public class ClientOperationDao {
		public int checkClientExist(String orgName, String address, String contact, Connection dbConnection) {
			// TODO Auto-generated method stub
			String sql = "Select * from clientcontact where orgName LIKE ? and address LIKE ? and contact LIKE ?";
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = dbConnection.prepareStatement(sql);
				ps.setString(1, orgName);
				ps.setString(2, address);
				ps.setString(3, contact);
				rs = ps.executeQuery();
				if (rs.next()) {
					return rs.getInt("clientId");
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

		public int addClientscreat(String status, Connection dbConnection) {
			String key = UUID.randomUUID().toString();
			String sql = "insert into clientscreat (clientkey, createddate, expirydate, status) values "
					+ "(?, NOW(), DATE_ADD(NOW(), INTERVAL 1 year), ?)";
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = dbConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setString(1, key);
				ps.setString(2, status);
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

		public void addClientContact(int clientId, String orgName, String address, String contact,
				Connection dbConnection) {
			String sql = "insert into clientcontact (clientId, orgName, address, contact) values " + "(?, ?, ?, ?)";
			PreparedStatement ps = null;
			boolean rs;
			try {
				ps = dbConnection.prepareStatement(sql);
				ps.setInt(1, clientId);
				ps.setString(2, orgName);
				ps.setString(3, address);
				ps.setString(4, contact);
				rs = ps.execute();
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
		}

		public String getClientKey(int clientId, Connection dbConnection) {
			String query = "Select ClientKey, Id from clientscreat where id = ?";
			ResultSet rs = null;
			PreparedStatement ps = null;
			try {
				ps = dbConnection.prepareStatement(query);
				ps.setInt(1, clientId);
				rs = ps.executeQuery();
				if (rs.next()) {
					return rs.getString("ClientKey");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null)
						rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;

		}

		public int getClientId(String clientKey, Connection dbConnection) {
			String query = "Select ClientKey, Id from clientscreat where ClientKey = ?";
			ResultSet rs = null;
			PreparedStatement ps = null;
			try {
				ps = dbConnection.prepareStatement(query);
				ps.setString(1, clientKey);
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
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return -1;

		}

		public void addClientSettings(JSONObject dataObject, Connection dbConnection) {
			String sql = "insert into clientsettings (clientId, eventId, createdDate, description, slotTime_mins, fromDate, toDate, fromTime, toTime) values "
					+ "(?, ?, NOW(), ?, ?, ?, ?, ?, ?)";
			PreparedStatement ps = null;
			boolean rs;
			try {
				ps = dbConnection.prepareStatement(sql);
				ps.setInt(1, dataObject.getInt("clientId"));
				ps.setInt(2, dataObject.getInt("eventId"));
				ps.setString(3, dataObject.getString("description"));
				ps.setInt(4, dataObject.getInt("timeSlot"));
				int i = 5;
				if (!dataObject.getString("fromDate").isEmpty()) {
					ps.setString(i, dataObject.getString("fromDate"));
					i++;
				}
				if (!dataObject.getString("toDate").isEmpty()) {
					ps.setString(i, dataObject.getString("toDate"));
					i++;
				}
				if (!dataObject.getString("fromTime").isEmpty()) {
					ps.setString(i, dataObject.getString("fromTime"));
					i++;
				}
				if (!dataObject.getString("toTime").isEmpty()) {
					ps.setString(i, dataObject.getString("toTime"));
					i++;
				}
				rs = ps.execute();
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

		}

		public int checkVendorExist(String vendorId, Connection dbConnection) {
			// TODO Auto-generated method stub
			String sql = "Select * from clientvendor where VendorId LIKE ?";
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = dbConnection.prepareStatement(sql);
				ps.setString(1, vendorId);
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

		public int addClientVendor(int clientId, String vendorId, Connection dbConnection) {
			String sql = "insert into clientvendor (ClientId, VendorId) values " + "(?, ?)";
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = dbConnection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
				ps.setInt(1, clientId);
				ps.setString(2, vendorId);
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

		public int checkResourceExist(String resourceId, Connection dbConnection) {
			// TODO Auto-generated method stub
			String sql = "Select * from resourcevendor where ResourceId LIKE ?";
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = dbConnection.prepareStatement(sql);
				ps.setString(1, resourceId);
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

		public void addResourceVendor(int clientId, String vendorId, String resourceId, int clientVendorId,
				Connection dbConnection) {
			String sql = "insert into resourcevendor (ClientId, VendorId, ResourceId, clientVendorId, slotTime) values "
					+ "(?, ?, ?, IF("+clientVendorId+" <= 0, NULL, "+clientVendorId+"), ?)";
			PreparedStatement ps = null;
			boolean rs;
			try {
				ps = dbConnection.prepareStatement(sql);
				ps.setInt(1, clientId);
				ps.setString(2, vendorId);
				ps.setString(3, resourceId);
				ps.setString(4, "00:30:00");
				rs = ps.execute();
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
		}

		public String getSlotTimeFromresourceId(String resourceId, Connection dbConnection) {
			String query = "Select SlotTime, ResourceId from resourcevendor where ResourceId = ?";
			ResultSet rs = null;
			PreparedStatement ps = null;
			try {
				ps = dbConnection.prepareStatement(query);
				ps.setString(1, resourceId);
				rs = ps.executeQuery();
				if (rs.next()) {
					return rs.getString("SlotTime");
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (rs != null)
						rs.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return null;

		}

}
