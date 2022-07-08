package com.whatsapp.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.stereotype.Repository;

import com.whatsapp.queries.WhatsappQueries;
import com.whatsapp.utils.RestUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Repository
public class WhatsAppRepo {
	RestUtils rs = new RestUtils();

	public synchronized JSONArray getNumbersWithMessage(int totalThread, int threadId, Connection con) {
		JSONArray arr = new JSONArray();
		PreparedStatement ps = null;
		ResultSet rs = null;
		int id = -1;
		try {
			ps = con.prepareStatement(WhatsappQueries.SELECT_NUMBERS_FOR_MESSAGE);
			ps.setInt(1, totalThread);
			ps.setInt(2, threadId);
			rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject obj = new JSONObject();
				id = rs.getInt("id");
				obj.put("id", id);
				obj.put("number", rs.getString("number"));
				obj.put("message", rs.getString("message"));
				if (id > 0) {
					updateSentFlag(true, id, con);
				}
				arr.add(obj);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return arr;

	}

	public synchronized void updateSentFlag(boolean flag, int id, Connection con) {
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(WhatsappQueries.UPDATE_SENT_FLAG);
			ps.setBoolean(1, flag);
			ps.setInt(2, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	public void updateResponse(String response, int id, Connection con) {
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(WhatsappQueries.UPDATE_SENT_FLAG);
			ps.setString(1, response);
			ps.setInt(2, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}
}
