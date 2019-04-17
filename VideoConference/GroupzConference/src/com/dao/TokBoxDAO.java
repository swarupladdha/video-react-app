package com.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.utils.PropertiesUtil;
import com.utils.RestUtils;
import com.utils.TokBoxInterfaceKeys;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TokBoxDAO {

	static final Logger logger = Logger.getLogger(TokBoxDAO.class);
	RestUtils restUtils = new RestUtils();
	
	public void closeStatement(Statement stmt) {
		try{
			if (stmt!=null) {
				stmt.close();
			}
		}
		catch(Exception e){
			logger.error("Exception",e);
		}
	}
	
	public void closeResultSet(ResultSet res) {
		try {
			if(res!=null){
				res.close();
			}
		} 
		catch (SQLException e) {
			logger.error("Exception",e);
		}
	}
	
	public void closePreparedStatement(PreparedStatement stmt) {
		try{
			if (stmt!=null) {
				stmt.close();
			}
		}
		catch(Exception e){
			logger.error("Exception",e);
		}
	}

	public int saveSession(Connection con, String sessionId, String token, boolean autoArchive) {
		PreparedStatement stmt = null;
		
		final String updateStorySQL = "insert into session (sessionid,token,autoarchive,createddate) "
					+ "values (?,?,?,'"+new java.sql.Timestamp( (new java.util.Date()).getTime())+"')";
		try {
			stmt=con.prepareStatement(updateStorySQL.toString());
			stmt.setString(1, sessionId);
			stmt.setString(2, token);
			stmt.setBoolean(3,autoArchive);
			int id = stmt.executeUpdate();
			
			ResultSet generatedKeys = stmt.getGeneratedKeys(); 
	            if (generatedKeys.next()) {
	                id = (generatedKeys.getInt(1));
	            }
			return id;
		} catch (Exception e) {
			logger.error("Exception",e);
		}
		finally{
			closePreparedStatement(stmt);
		}
		return 0;
	}
	
	public JSONObject retrieveSession(Connection connection, int id) {
		ResultSet res=null;
		Statement stmt = null;
		final String query = "select sessionid,token from session where id = "+id;
		JSONObject search = new JSONObject();
		try{
				stmt = connection.createStatement();
				logger.info(query);
				res = stmt.executeQuery(query);
				if(res.next()){
					search.put(TokBoxInterfaceKeys.id, id);
					search.put(TokBoxInterfaceKeys.sessionid, res.getString( TokBoxInterfaceKeys.sessionid));
					search.put(TokBoxInterfaceKeys.tokenid, res.getString( TokBoxInterfaceKeys.token));
					return search;
				}
			}
			catch (Exception e){
				logger.error("Exception",e);
			}
			finally{
				closeResultSet(res);
				closeStatement(stmt);
			}
			return search;
	}
	
	public JSONObject getList(Connection connection, String id,String paginatioQuery) {
		ResultSet res=null;
		Statement stmt = null;
		final String query = "select * from session where id in ("+id+") "+paginatioQuery;
		JSONObject response = new JSONObject();
		JSONArray list = new JSONArray();
		try{
				stmt = connection.createStatement();
				logger.info(query);
				res = stmt.executeQuery(query);
				while(res.next()){
					JSONObject search = new JSONObject();
					search.put(TokBoxInterfaceKeys.id, res.getInt( TokBoxInterfaceKeys.id));
					search.put(TokBoxInterfaceKeys.sessionid, res.getString( TokBoxInterfaceKeys.sessionid));
					search.put(TokBoxInterfaceKeys.tokenid, res.getString( TokBoxInterfaceKeys.token));
					search.put(TokBoxInterfaceKeys.archiveid, res.getString( TokBoxInterfaceKeys.archiveid));
					search.put(TokBoxInterfaceKeys.event, res.getString( TokBoxInterfaceKeys.event));
					search.put(TokBoxInterfaceKeys.status, res.getString( TokBoxInterfaceKeys.status));
					search.put(TokBoxInterfaceKeys.reason, res.getString( TokBoxInterfaceKeys.reason));
					search.put(TokBoxInterfaceKeys.downloadurl, res.getString( TokBoxInterfaceKeys.downloadurl));
					search.put(TokBoxInterfaceKeys.details, res.getString( TokBoxInterfaceKeys.details));
					search.put(TokBoxInterfaceKeys.duration, res.getInt( TokBoxInterfaceKeys.duration));
					search.put(TokBoxInterfaceKeys.size, res.getInt( TokBoxInterfaceKeys.size));
					search.put(TokBoxInterfaceKeys.videoid, res.getInt( TokBoxInterfaceKeys.videoid));
					search.put(TokBoxInterfaceKeys.createddate, res.getString( TokBoxInterfaceKeys.createddate));
					search.put(TokBoxInterfaceKeys.updateddate, res.getString( TokBoxInterfaceKeys.updateddate));
					list.add(search);
				}
				response.put(TokBoxInterfaceKeys.list, list);
				return response;
			}
			catch (Exception e){
				logger.error("Exception",e);
			}
			finally{
				closeResultSet(res);
				closeStatement(stmt);
			}
			return response;
	}
	
	public int updateSession(Connection connection, String sessionId, String event,String reason, String downloadUrl,String status,int duration,int size,String archiveId,String details) {
		PreparedStatement stmt = null;
		final String updateJobSQL = "update session set event=?,reason=?,downloadurl=?,status=?,duration=?,size=?,archiveId=?,details=?,updateddate=? where sessionid=?";
		try {
			stmt=connection.prepareStatement(updateJobSQL.toString());
			stmt.setString(1, event);
			stmt.setString(2, reason);
			stmt.setString(3, downloadUrl);
			stmt.setString(4, status);
			stmt.setInt(5, duration);
			stmt.setInt(6, size);
			stmt.setString(7, archiveId);
			stmt.setString(8, details);
			stmt.setString(9, new java.sql.Timestamp( (new java.util.Date()).getTime()).toString());
			stmt.setString(10, sessionId);
			logger.info(stmt+"");
			int id = stmt.executeUpdate();
			return id;
		} catch (Exception e) {
			logger.error("Exception",e);
		}
		finally{
			closePreparedStatement(stmt);
		}
		return 0;
	}
	
	public int updateSessionStatus(Connection connection, String sessionId, String event, String reason,
			String status,String details) {
		PreparedStatement stmt = null;
		final String updateJobSQL = "update session set event=?,reason=?,status=?,details=?,updateddate=? where sessionid=?";
		try {
			stmt=connection.prepareStatement(updateJobSQL.toString());
			stmt.setString(1, event);
			stmt.setString(2, reason);	
			stmt.setString(3, status);
			stmt.setString(4, details);
			stmt.setString(5, new java.sql.Timestamp( (new java.util.Date()).getTime()).toString());
			stmt.setString(6, sessionId);
			logger.info(stmt+"");
			int id = stmt.executeUpdate();
			return id;
		} catch (Exception e) {
			logger.error("Exception",e);
		}
		finally{
			closePreparedStatement(stmt);
		}
		return 0;
	}
	

	public int updateVideoId(Connection connection, String sessionId, int videoId) {
		PreparedStatement stmt = null;
		final String updateJobSQL = "update session set videoid=? where sessionid=?";
		try {
			stmt=connection.prepareStatement(updateJobSQL.toString());
			stmt.setInt(1, videoId);
			stmt.setString(2, sessionId);
			logger.info(stmt+"");
			int id = stmt.executeUpdate();
			return id;
		} catch (Exception e) {
			logger.error("Exception",e);
		}
		finally{
			closePreparedStatement(stmt);
		}
		return 0;
	}
	
	public JSONObject getSessionDetails(Connection connection, String sessionId) {
		PreparedStatement stmt = null;
		ResultSet res = null;
		final String getSqlQuery ="select id,status,event,reason,videoid,duration,size,downloadurl from session where sessionid=?";
		try {
		stmt=connection.prepareStatement(getSqlQuery.toString());
		stmt.setString(1, sessionId);
		res = stmt.executeQuery();
		if (res.next()) {
			JSONObject obj = new JSONObject();
			obj.put(TokBoxInterfaceKeys.sessionid, res.getInt(TokBoxInterfaceKeys.id));
			obj.put(TokBoxInterfaceKeys.status, res.getString(TokBoxInterfaceKeys.status));
			obj.put(TokBoxInterfaceKeys.event, res.getString(TokBoxInterfaceKeys.event));
			obj.put(TokBoxInterfaceKeys.reason, res.getString(TokBoxInterfaceKeys.reason));
			obj.put(TokBoxInterfaceKeys.videoid, res.getInt(TokBoxInterfaceKeys.videoid));
			obj.put(TokBoxInterfaceKeys.duration, res.getInt(TokBoxInterfaceKeys.duration));
			obj.put(TokBoxInterfaceKeys.size, res.getInt(TokBoxInterfaceKeys.size));
			obj.put(TokBoxInterfaceKeys.downloadurl, res.getString(TokBoxInterfaceKeys.downloadurl));
			return obj;
		}
		}catch (Exception e) {
			logger.error("Exception",e);
		}
		finally{
			closePreparedStatement(stmt);
		}
		return null;
	}
	
	public String getSessionId(Connection connection, int id) {
		PreparedStatement stmt = null;
		ResultSet res = null;
		final String getSqlQuery ="select sessionid from session where id=?";
		try {
		stmt=connection.prepareStatement(getSqlQuery.toString());
		stmt.setInt(1, id);
		res = stmt.executeQuery();
		if (res.next()) {
			return res.getString("sessionid");
		}
		}catch (Exception e) {
			logger.error("Exception",e);
		}
		finally{
			closePreparedStatement(stmt);
		}
		return null;
	}
// For Reconnecting Session
	public int saveSessionRec(Connection con, String sessionId, String token,int id,boolean autoArchive) {
		PreparedStatement stmt = null;
		Statement stmt1 = null;
		ResultSet res = null;
		int previousid = 0;
		 String query = "select id,previousid from session where previousid =" +id;
		try {
			stmt1=con.createStatement();
			res = stmt1.executeQuery(query);
			if (res.next()) {
				previousid = res.getInt("previousid");
			}
			if(previousid!=id) {
		 final String updateStorySQL = "insert into session (sessionid,token,previousid,autoarchive,createddate) "
									+ "values (?,?,?,?,'"+new java.sql.Timestamp( (new java.util.Date()).getTime())+"')";
			
		    stmt=con.prepareStatement(updateStorySQL.toString());
			stmt.setString(1, sessionId);
			stmt.setString(2, token);
			stmt.setInt(3, id);
			stmt.setBoolean(4,autoArchive);
			int id1 = stmt.executeUpdate();
			
			ResultSet generatedKeys = stmt.getGeneratedKeys(); 
	            if (generatedKeys.next()) {
	                id1 = (generatedKeys.getInt(1));
	            }
			return id1;
			}
			else {
				return 0;
			}
		} catch (Exception e) {
			logger.error("Exception",e);
		}
		finally{
			closePreparedStatement(stmt);
			closeStatement(stmt1);
		}
		return 0;
	}
	//for getting video

	public int getVideo(Connection connection, int id) {
		
		PreparedStatement stmt = null;
		ResultSet res = null;
		final String getSqlQuery ="select id,videoid,previousid,joinvideoid,autoarchive from session where id=?";
		try {
		stmt=connection.prepareStatement(getSqlQuery.toString());
		stmt.setInt(1, id);
		res = stmt.executeQuery();
		if (res.next())
		{
			boolean autoArchive = res.getBoolean("autoarchive");
			if(autoArchive) {
			int joinvideoid = res.getInt("joinvideoid");
			if(joinvideoid>0) {
				return joinvideoid;
			}
			else {
				return res.getInt("videoid");
			}
		}
			else {
				return -1;
			}
		}
		
		}catch (Exception e) {
			logger.error("Exception",e);
		}
		finally{
			closePreparedStatement(stmt);
		}
		return 0;
	}
	

}

