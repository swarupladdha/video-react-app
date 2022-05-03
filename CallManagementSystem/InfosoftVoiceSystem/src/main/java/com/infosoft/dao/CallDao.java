package com.infosoft.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Repository;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.infosoft.queries.CallQueries;
import com.infosoft.utils.AllKeys;
import com.infosoft.utils.Utils;

@Repository
public class CallDao {
	public static final Logger logger = Logger.getLogger(CallDao.class);
	Utils utils = new Utils();

	public String insertCallDetails(String fromNumber, String toNumber, String callStatus, String timeLimit,
			String callbackURL, String callid, Connection con) {
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(CallQueries.ADD_CALLDETAILS);
			ps.setString(1, fromNumber);
			ps.setString(2, toNumber);
			ps.setString(3, callStatus);
			ps.setString(4, timeLimit);
			ps.setString(5, callbackURL);
			ps.setString(6, callid);

			int i = ps.executeUpdate();
			if (i > 0) {
				return utils.successResponse();
			}
		} catch (SQLException e) {

			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;

	}

	public JSONArray getNewPhoneDetails(int trialCount, Connection con) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		JSONArray arr = new JSONArray();
		try {
			ps = con.prepareStatement(CallQueries.GET_NEW_NUMBERS);
			ps.setInt(1, trialCount);
			rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject ob = new JSONObject();
				ob.put(AllKeys.ID, rs.getInt(AllKeys.ID));
				ob.put(AllKeys.FROM_NUMBER, rs.getString(AllKeys.FROM_NUMBER));
				ob.put(AllKeys.TO_NUMBER, rs.getString(AllKeys.TO_NUMBER));
				ob.put(AllKeys.TIME_LIMIT, rs.getString(AllKeys.TIME_LIMIT));
				ob.put(AllKeys.TRIAL_COUNT, rs.getInt(AllKeys.TRIAL_COUNT));
				arr.add(ob);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return arr;

	}

	public void updateCallDetailsOnceCallInitiate(int id, String callSid, String callStatus, String callStartTime,
			int trialCount, Connection con) {
		PreparedStatement ps = null;
		try {

			ps = con.prepareStatement(CallQueries.UPDATE_AFTER_CALL_INITIATE);
			ps.setString(1, callStatus);
			ps.setString(2, callSid);
			ps.setString(3, callStartTime);
			ps.setInt(4, trialCount);
			ps.setInt(5, id);
			int i = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void updateCallDetailsAfterCallBackResponse(String callSid, String callStatus, String callStartTime,
			String callEndTime, Connection con) {
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(CallQueries.UPDATE_AFTER_CALLBACK_RESPONSE);
			ps.setString(1, callStatus);
			ps.setString(2, callStartTime);
			ps.setString(3, callEndTime);
			ps.setString(4, callSid);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public JSONArray getPhoneDetailsForCallBack(int id, String callSid, Connection con) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		JSONArray arr = new JSONArray();
		try {
			if (id > 0) {
				ps = con.prepareStatement(CallQueries.GET_DETAILS_FOR_UPDATING_CALLBACK_RESPONSE2);
				ps.setInt(1, id);
			} else {
				ps = con.prepareStatement(CallQueries.GET_DETAILS_FOR_UPDATING_CALLBACK_RESPONSE);
				ps.setString(1, callSid);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject ob = new JSONObject();
				ob.put(AllKeys.ID, rs.getInt(AllKeys.ID));
				ob.put(AllKeys.CALLID, rs.getString(AllKeys.CALLID));
				ob.put(AllKeys.CALL_STATUS, rs.getString(AllKeys.CALL_STATUS));
				ob.put(AllKeys.CALLSID, rs.getString(AllKeys.CALLSID));
				ob.put(AllKeys.CALLBACK_URL, rs.getString(AllKeys.CALLBACK_URL));
				arr.add(ob);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return arr;

	}

}
