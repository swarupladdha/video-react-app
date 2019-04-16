package com.services;

import java.net.URLEncoder;
import java.sql.Connection;

import org.apache.log4j.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MergeVideos extends RIJDBBaseThread {

	
static final Logger logger = Logger.getLogger(MergeVideos.class);
	

	int threadid ;
	java.sql.Statement stmt1 = null;
	java.sql.ResultSet res1 = null;
	java.sql.Statement stmt2 = null;
	java.sql.ResultSet res2 = null;
	java.sql.Statement stmt3 = null;
	
	public MergeVideos(int tid, String url, String username, String passwd) {
		super(tid, url, username, passwd);
		threadid = tid ;
	}
	
	@Override
	void process(Connection dbConn) {
		
		try {	
			String query1 = "select id,videoid,previousid,joinvideoid from session where previousid>0 and videoid>0 and joinvideoid=0"
					+" and ((id % "+Integer.parseInt( PropertiesUtil.getProperty("max_threads"))+") = "+threadid+") order by id desc limit 1 ";

			
			stmt1 = dbConn.createStatement();
			res1 = stmt1.executeQuery(query1);
			while (res1.next()) {
				System.out.println(query1);
				int id = res1.getInt("id");
				int previousid = res1.getInt("previousid");
				int videoid1 = res1.getInt("videoid");
				System.out.println("stmt1");
				
				String query2 = "select id,videoid,previousid,joinvideoid from session where id ="+previousid;
				int videoid2 = 0;
				stmt2 = dbConn.createStatement();
				res2 = stmt2.executeQuery(query2);
				if (res2.next()) {
					int joinvideoid = res2.getInt("joinvideoid");
					System.out.println(query2);
					if(joinvideoid>0) {
						
						videoid2 = joinvideoid;
					}
					else {
						
						videoid2 = res2.getInt("videoid");
					}
					System.out.println("stmt2");
					
					JSONArray ids = new JSONArray();
					ids.add(videoid2);
					ids.add(videoid1);
					JSONObject obj = new JSONObject();
					obj.put("id", ids);
					String url = PropertiesUtil.getProperty("joinvideourl");
					System.out.println(url+obj.toString());
					String finalUrl = url+URLEncoder.encode(obj.toString(),"UTF-8");
					System.out.println(finalUrl);
					ConnectAndRecive cnr = new ConnectAndRecive();
					String response = cnr.ConnectandRecieve(finalUrl);
					System.out.println(response);
					if (response != null) {
						System.out.println(response);
						JSONObject resObj = JSONObject.fromObject(response);
						int resId = resObj.getInt("id");
						stmt3 = dbConn.createStatement();
						String query3 = "update session set joinvideoid="+resId+",updateddate='"+new java.sql.Timestamp( (new java.util.Date()).getTime())+"'  where id ="+id;
						System.out.println(query3);
						stmt3.executeUpdate(query3);
						
						JSONObject fcmObject = new JSONObject();
						fcmObject.put("callid", id);
						fcmObject.put("joinvideoId", resId);
						fcmObject.put("statuscode", PropertiesUtil.getProperty("video_prepared_code"));
						fcmObject.put("statusmessage", PropertiesUtil.getProperty("video_prepared_message"));
					}
				}
			}
			Thread.sleep(5000);
		}
		catch (Exception e) {
			e.printStackTrace();
			//logger.error("Exception in MergeVideos ",e);
		}
		finally {
			ConnectionUtils.closeStatement(stmt1);
			ConnectionUtils.closeStatement(stmt2);
			ConnectionUtils.closeStatement(stmt3);
			ConnectionUtils.closeResultSet(res1);
			ConnectionUtils.closeResultSet(res2);
		}
	}
}
		

	
	

