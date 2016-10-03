package com.groupz.followup.manager;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.groupz.followup.utils.ConnectionUtils;
import com.groupz.message.Message;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class FollowupManager {
	static final Logger logger = Logger.getLogger(FollowupManager.class);

	 String followupSQL =" select gbfollowup.id, groupzbaseid,noofdays,roleid,followuptime,lastsenttime from gbfollowup, builder "
	 					+ "where gbfollowup.id MOD %s = %s and hour(gbfollowup.FollowupTime)<CURTIME() and "
	 					+ "minute(gbfollowup.FollowupTime)<CURTIME() and (gbfollowup.lastsenttime<CURDATE() "
	 					+ "or gbfollowup.lastsenttime is null) and builder.id = gbfollowup.groupzbaseid ";

	 /*String followupSQL = "select gbfollowup.id, groupzbaseid,noofdays,roleid,followuptime,lastsenttime "
				+ " from gbfollowup, builder "
				+ " and STR_TO_DATE(gbfollowup.followuptime,'%H:%i')<CURTIME() and gbfollowup.lastsenttime<CURDATE() "
				+ " and builder.id = gbfollowup.groupzbaseid";*/
	 
	 String updateFollowupSQL = "update gbfollowup set LastSentTime=NOW() where Id=%s";

	 String contactsListSQL = "select ufm.id,a.societycode,f.id, a.mobile, a.name, s.senderemail, s.sendersms, u.email   "
			+ "from flat f, userflatmapping ufm, user u,  apartment a, apartment_settings s,roledefinition r "
			+ "where DATE(DATE_ADD(f.approvaldate, INTERVAL %s DAY) ) = CURRENT_DATE and "
			+ "f.contact = true and ufm.flatid = f.id  and ufm.enabled = true and "
			+ "u.id = ufm.userid and u.enabled = true  and  ufm.roleid=r.id and "
			+ "f.apartmentid = a.id and a.enabled = true and s.apartmentid = a.id and a.builderid = %s and r.gbroleid = %s";

	private List<Message> getTargetList(Connection c, int baseid, int days, int roleid) {
		Statement stmt =null;
		try {
		stmt = c.createStatement();
		List<Message> theFinalList = new ArrayList<Message>();
		String QueryString = String.format(contactsListSQL, days, baseid,roleid);
		logger.debug(QueryString);
		System.out.println("@@@@@@@@@@@@@@@@@@@@");
		System.out.println(QueryString);
		ResultSet rs = stmt.executeQuery(QueryString);
		while (rs.next()) {
		int ufmId = rs.getInt("ufm.id");
		String groupzCode = rs.getString("a.societycode");
	//	int memid = rs.getInt("f.id");
		// String mob = rs.getString("p.mobile");
		String email = rs.getString("u.email");
		// String name = rs.getString("p.name");
		// String gender = rs.getString("p.gender");
		String fromEmail = rs.getString("s.senderemail");
		String fromMob = rs.getString("a.mobile");
		String senderid = rs.getString("s.sendersms");
		String groupz = rs.getString("a.name");
		// String contactName = name;
	//	String namePrefix;
	//	String contactPrefix;
		/*
		 * if (gender != null && gender.trim().equalsIgnoreCase("male"))
		 * { namePrefix = "Mr."; } else { namePrefix = "Ms."; }
		 */
		// contactPrefix = namePrefix;
		Message m = new Message();
		// m.setToName(name);
		// m.setToContactName(contactName);
		// m.setNamePrefix(namePrefix);
		// m.setContactPrefix(contactPrefix);
		// m.setMobNumber(mob);
		m.setEmail(email);
		m.setSenderId(senderid);
		m.setFromEmail(fromEmail);
		m.setFromMobile(fromMob);
		m.setGroupzName(groupz);
		m.setGroupzCode(groupzCode);
		m.setMemberId(ufmId);
		theFinalList.add(m);
		/*
		 * logger.debug("Sending...." + memid + "  " + mob +
		 * "  -  " + email);
		 */
		}
		stmt.close();
		return theFinalList;
		} 
		catch (Exception e) {
		e.printStackTrace();
		if(stmt!=null){
		try {
			stmt.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();					
		}
		}
		return null;
		}
	}
/*
	private void sendEmailFollowup(int baseid, int days, String emailText) {
		logger.debug(" Email sent : " + emailText);
	}
*/
	private void followupDone(Connection c, int id) {
		Statement stmt=null;
		try {
			String updateSQL = String.format(updateFollowupSQL, id);
			 stmt = c.createStatement();
			stmt.executeUpdate(updateSQL);
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
			if(stmt!=null){
				try {
					stmt.close();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}

	public void followUpMessage(int threadId, int followUpThread_POOL_SIZE, ComboPooledDataSource connectionPool) throws SQLException {
		Statement stmt=null;
		Connection connection= null;
		
		try {
			connection=connectionPool.getConnection();
			 stmt = connection.createStatement();
			 System.out.println("********************************************************");
			
			
			String poolSize=String.valueOf(followUpThread_POOL_SIZE);
			String threadid=String.valueOf(threadId);
			String QueryString = String.format(followupSQL,poolSize,threadid);
			logger.debug("Followup Sql:"+QueryString);
			System.out.println(QueryString);
			ResultSet rs = stmt.executeQuery(QueryString);
			while (rs.next()) {
			int id = rs.getInt("gbfollowup.id");
			int gbid = rs.getInt("groupzbaseid");
			int days = rs.getInt("noofdays");
			int roleid = rs.getInt("roleid");
			//String smsText = rs.getString("FollowupApprovedSMSText");
			//	String providercode = rs.getString("SmsProviderCode");
			//String userid = rs.getString("SmsProviderUserName");
			//String password = rs.getString("SmsProviderPassword");
			logger.debug("Sending.... FollowupId-->" + id + " For GroupzbaseId-->  " + gbid + "  and No.OfDays-->  "+ days);
			List<Message> targetList = getTargetList(connection, gbid,days, roleid);
			// logger.debug("Target List:"+targetList.size());
			// if (smsText != null && smsText.trim().isEmpty() == false) {
			if (targetList != null && targetList.size() > 0) {
			for (Message msg : targetList) {
			// msg.sendMessage(dbConnection, providercode, userid, password, smsText, null) ;
			logger.debug("Follow up id:" + id+ " and gpzCode:" + msg.getGroupzCode()+ " and memId:" + msg.getMemberId());
			boolean followUpStatus = msg.sendFollowupinURL(id,msg.getGroupzCode(), msg.getMemberId());
			logger.debug("Follow up status through URL:"+ followUpStatus);
			}
		}
			followupDone(connection, id);
	}
			ConnectionUtils.close(stmt,connection);
	}
		catch (Exception e) {
		e.printStackTrace();
		ConnectionUtils.close(stmt, connection);
	
	}
	
}

}
