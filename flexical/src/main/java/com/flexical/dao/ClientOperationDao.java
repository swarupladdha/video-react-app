package com.flexical.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

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

		/*public void addClientSettings(int clientId, ClientSettingsBean dataObject, Connection dbConnection) {
			String sql = "insert into clientdefaultavailability (clientId, weekdayId, startTime, endTime, working) values "
					+ "(?, ?, ?, ?, ?)";
			PreparedStatement ps = null;
			boolean rs;
			try {
				ps = dbConnection.prepareStatement(sql);
				ps.setInt(1, clientId);
				ps.setInt(2, dataObject.getWeekdayId());
				ps.setString(3, dataObject.getStartTime().toString());
				ps.setString(4, dataObject.getEndTime().toString());
				ps.setInt(5, dataObject.getWorking());
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

		}*/

		public int checkVendorExist(String vendorId, int clientId, Connection dbConnection) {
			// TODO Auto-generated method stub
			String sql = "Select * from clientvendor where VendorId = ? and ClientId = ?";
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				ps = dbConnection.prepareStatement(sql);
				ps.setString(1, vendorId);
				ps.setInt(2, clientId);
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
			String sql = "Select * from vendorresource where ResourceId LIKE ?";
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
			String sql = "insert into vendorresource (ClientId, VendorId, ResourceId, clientVendorId, slotTimeId) values "
					+ "(?, ?, ?, IF("+clientVendorId+" <= 0, NULL, "+clientVendorId+"), ?)";
			PreparedStatement ps = null;
			boolean rs;
			try {
				ps = dbConnection.prepareStatement(sql);
				ps.setInt(1, clientId);
				ps.setString(2, vendorId);
				ps.setString(3, resourceId);
				ps.setInt(4, 1);
				//ps.setString(4, "00:30:00");
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
			String query = "Select st.SlotTime, vr.ResourceId from vendorresource vr, slottime st where vr.ResourceId = ? and vr.SlotTimeId=st.Id;";
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
