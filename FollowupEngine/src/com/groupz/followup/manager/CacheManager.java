package com.groupz.followup.manager;

import java.io.FileInputStream;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.groupz.followup.utils.ConnectionUtils;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class CacheManager {

	static final Logger logger = Logger.getLogger(CacheManager.class);

	 String cacheTableSQL = "SELECT ID,LASTUPDATEDDATE from CACHEHEARTBEATUPDATE where Id MOD %s = %s";

	 String updateCacheTableSQL = "UPDATE CACHEHEARTBEATUPDATE SET ID= %s , LASTUPDATEDDATE='%s'";
	
	 String heartBeatListSQL = "SELECT ID,GROUPZID,GROUPZCODE,MODULETYPE,UPDATEDDATE FROM "
			+ " UPDATEMODULEACTIVITIES WHERE UPDATEDDATE>'%s' ORDER BY UPDATEDDATE ASC";
	
	/*static String heartBeatListSQL = "SELECT ID,GROUPZID,GROUPZCODE,MODULETYPE,UPDATEDDATE FROM "
			+ " UPDATEMODULEACTIVITIES WHERE ((UPDATEDDATE>='%s' and ID!= %s ) or (UPDATEDDATE>'%s' and ID= %s)) ORDER BY UPDATEDDATE ASC";
*/
	private void updateCacheTime(Connection c,String updatedId,String updatedTime) {
		Statement stmt = null;
		try {
			String updateSQL = String.format(updateCacheTableSQL,updatedId,
					updatedTime);
			logger.debug("Update cache time sql:" + updateSQL);
			stmt = c.createStatement();
			stmt.executeUpdate(updateSQL);
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public void startCacheManager(ComboPooledDataSource connectionPool, int cachePoolSize, int threadId) {
		Statement stmt = null;
		Connection connection=null;
	
		try {
			connection = connectionPool.getConnection();
			stmt = connection.createStatement();
	//		System.out.println("conn"+connection.hashCode());
			String QueryString = String.format(cacheTableSQL,cachePoolSize,threadId);
			logger.debug("Cache Update Sql:" + QueryString);
			ResultSet rs = stmt.executeQuery(QueryString);	
			String cacheUpdateTime="";
			String cacheUpdatedId="";
			if (rs.next()) {
				cacheUpdateTime = rs.getString("LASTUPDATEDDATE");
				cacheUpdatedId = String.valueOf(rs.getInt("ID"));
			}else{
				cacheUpdateTime = "0000-00-00 00:00:00";
				cacheUpdatedId = "-1";
			}
				logger.debug("Checking.... lastupdateddate-->"+ cacheUpdateTime+""+cacheUpdatedId);
				/*String cacheListSQL = String.format(heartBeatListSQL,
						cacheUpdateTime,cacheUpdatedId,cacheUpdateTime,cacheUpdatedId);*/
				String cacheListSQL = String.format(heartBeatListSQL,cacheUpdateTime);
				logger.debug("Cache update list sql:" + cacheListSQL);
				ResultSet cacheUpdateSet = stmt.executeQuery(cacheListSQL);
				JSONArray dataArray = new JSONArray();
				String lastUpdatedDate="";String lastUpdatedId="";
				while (cacheUpdateSet.next()) {
					JSONObject dataObj = new JSONObject();
					int groupzId = cacheUpdateSet.getInt("GROUPZID");
					String groupzCode = cacheUpdateSet.getString("GROUPZCODE");
					String moduleType = cacheUpdateSet.getString("MODULETYPE");
					String updatedDate = cacheUpdateSet
							.getString("UPDATEDDATE");
					logger.debug("Groupz Id:" + groupzId+ " and groupz code:" + groupzCode
							+ " and module type:" + moduleType+ " and updateddate:" + updatedDate);
					dataObj.put("groupzid", groupzId);
					dataObj.put("groupzcode", groupzCode);
					dataObj.put("moduletype", moduleType);
					dataObj.put("lastupdateddate", updatedDate);
					dataArray.add(dataObj);		
					lastUpdatedDate = updatedDate;
					lastUpdatedId = String.valueOf(cacheUpdateSet.getInt("ID"));
				}
				// call the cache url
				if((lastUpdatedId!=null && lastUpdatedId.length()>0 && lastUpdatedId.equalsIgnoreCase("")==false) &&
						(lastUpdatedDate!=null && lastUpdatedDate.length()>0 && lastUpdatedDate.equalsIgnoreCase("")==false)){
				updateCacheTime(connection,lastUpdatedId,lastUpdatedDate);
				boolean cacheStatus = sendCacheupdate(dataArray);
				logger.debug("Cache DB  status through URL:"+cacheStatus);
				
				}
			ConnectionUtils.close(stmt, connection);
		} catch (Exception e) {
			e.printStackTrace();
			ConnectionUtils.close(stmt,connection);
		}
	}

	// send cacheUpdate in url

	public boolean sendCacheupdate(JSONArray dataArray) {
		boolean cacheStatus = false;
		try {
			String fileName = System.getenv("FE_CONFIG_FILE");
			if (fileName == null) {
				logger.debug("Env. Variable FE is not set, using default file vinralerts.properties");
				fileName = "conf/db.properties";
			}

			Properties p = new Properties(System.getProperties());
			FileInputStream propFile = new FileInputStream(fileName);
			p.load(propFile);
			JSONObject jsonObj = new JSONObject();
			JSONObject reqObj = new JSONObject();
			JSONObject dataObj = new JSONObject();
			dataObj.put("servicetype", "21");
			dataObj.put("functiontype", "6000");
			dataObj.put("data", dataArray);
			/*dataObj.put("Groupzcode", groupzCode);
			dataObj.put("Groupzid", groupzId);
			dataObj.put("ModuleType", moduleType);
			dataObj.put("LastUpdatedDate", updatedDate);*/
			reqObj.put("request", dataObj);
			jsonObj.put("json", reqObj);
			logger.debug("Final JSON:" + jsonObj.toString());
			String cacheDBURL = p.getProperty("cachefollowup_URL")+ URLEncoder.encode(jsonObj.toString());
			logger.debug("Cache DB URL:" + cacheDBURL);
			ConnectionUtils connectionUtils = new ConnectionUtils();
			String cacheDBResponse = connectionUtils
					.ConnectandRecieve(cacheDBURL);
			if (cacheDBResponse == null || cacheDBResponse.length() == 0) {
				cacheStatus = false;
			} else {
				try {
					JSONObject respJSON = JSONObject
							.fromObject(cacheDBResponse);
					logger.debug("Response JSON:" + respJSON.toString());
					logger.debug("Status code:"+ respJSON.getJSONObject("json").getJSONObject("response").getString("statuscode"));
					logger.debug("Status code:"+ respJSON.getJSONObject("json").getJSONObject("response").getString("statusmessage"));
					if (respJSON.getJSONObject("json").getJSONObject("response").getString("statuscode").equalsIgnoreCase("0") == true) {
						cacheStatus = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
					cacheStatus = false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cacheStatus;
	}

	public static String getLatestTime() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		String utcTime = f.format(new Date());
		// logger.debug("String date:"+utcTime);
		Date lastSynch = StringDateToDate(utcTime);
		String lastSynchTime = getFormattedDateStr(lastSynch);
		return lastSynchTime.trim();
	}

	// Getting synch time in UTC
	public static Date StringDateToDate(String StrDate) {
		String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
		Date dateToReturn = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
		try {
			dateToReturn = (Date) dateFormat.parse(StrDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return dateToReturn;
	}

	public static String getFormattedDateStr(Date date) {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		String strDate = null;
		if (date != null) {
			strDate = f.format(date);
		}
		return strDate;
	}

}
