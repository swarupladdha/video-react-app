package com.groupz.followup.manager;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.groupz.message.Message;

public class FollowupManager {
	
	static String followupSQL = "select gbfollowup.id, groupzbaseid,noofdays,roleid, "
			+ "FollowupApprovedMailText, FollowupApprovedMailTitle, "
			+ "SmsProviderCode, SmsProviderPassword, SmsProviderUserName, "
			+ "FollowupApprovedSMSText from gbfollowup, builder "
			+ "where followupsent=false and builder.id = gbfollowup.groupzbaseid" ;

	static String updateFollowupSQL = "update gbfollowup set followupsent=true where Id=%s" ;

	static String contactsListSQL = "select f.id, p.mobile, a.mobile, a.name, s.senderemail, s.sendersms, u.email, p.name, p.gender  "
			+ "from flat f, userflatmapping ufm, user u, person p , apartment a, apartment_settings s "
			+ "where DATE(DATE_ADD(f.approvaldate, INTERVAL %s DAY) ) = CURRENT_DATE and "
			+ "f.contact = true and ufm.flatid = f.id  and ufm.enabled = true and "
			+ "u.id = ufm.userid and u.enabled = true  and p.flatid = f.id and " 
			+ "f.apartmentid = a.id and a.enabled = true and s.apartmentid = a.id and a.builderid = %s and ufm.roleid = %s";


	private List<Message> getTargetList( Connection c, int baseid, int days, int roleid ){
        try {
            Statement stmt = c.createStatement();		                	
        	List<Message> theFinalList = new ArrayList<Message>() ;

            String QueryString = String.format(contactsListSQL, days, baseid, roleid ) ;	                	

            System.out.println(QueryString) ;

            ResultSet rs = stmt.executeQuery(QueryString);
            while( rs.next()){
            	int memid = rs.getInt("f.id") ;
            	String mob = rs.getString("p.mobile") ;
            	String email = rs.getString("u.email") ;
            	String name = rs.getString("p.name") ;
            	String gender = rs.getString("p.gender") ;	            	
            	String fromEmail = rs.getString("s.senderemail") ;
            	String fromMob = rs.getString("a.mobile") ;
            	String senderid = rs.getString("s.sendersms") ;
            	String groupz = rs.getString("a.name") ;
            	String contactName = name ;
            	String namePrefix ;
            	String contactPrefix ;
            	if( gender != null && gender.trim().equalsIgnoreCase("male")){
            		namePrefix = "Mr." ;
            	}else{
            		namePrefix = "Ms." ;
            	}
            	contactPrefix = namePrefix ;
            	
            	Message m = new Message() ;
            	m.setToName(name); ;
            	m.setToContactName(contactName);
            	m.setNamePrefix(namePrefix);
            	m.setContactPrefix(contactPrefix);
            	m.setMobNumber(mob);
            	m.setEmail(email);
            	m.setSenderId(senderid);
            	m.setFromEmail(fromEmail);
            	m.setFromMobile(fromMob);
            	m.setGroupzName(groupz);
            	theFinalList.add(m) ;
            	System.out.println("Sending...." + memid + "  " + mob + "  -  " + email) ;            	
            }
			stmt.close();
			return theFinalList ;
        } catch(Exception e){
        	e.printStackTrace();
        	return null ;
        }		
	}

	private void sendEmailFollowup( int baseid, int days, String emailText ){
		System.out.println(" Email sent : " + emailText) ;
	}

	private void followupDone( Connection c, int id){
		try{
			String updateSQL = String.format(updateFollowupSQL, id ) ;
			Statement stmt = c.createStatement() ;
			stmt.executeUpdate(updateSQL) ;
			stmt.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void run(  Connection dbConnection){
        try {
            Statement stmt = dbConnection.createStatement();		                	

            String QueryString = followupSQL;	                	

            ResultSet rs = stmt.executeQuery(QueryString);
            while( rs.next()){
            	int id = rs.getInt("gbfollowup.id") ;
            	int gbid = rs.getInt("groupzbaseid") ;
            	int days = rs.getInt("noofdays") ;
            	int roleid = rs.getInt("roleid") ;
            	String smsText = rs.getString("FollowupApprovedSMSText") ;
            	String providercode = rs.getString("SmsProviderCode") ;
            	String userid = rs.getString("SmsProviderUserName") ;
            	String password = rs.getString("SmsProviderPassword") ;
            	
            	System.out.println("Sending...." + id + "  " + gbid + "  -  " + days) ;            	
            	List<Message> targetList = getTargetList(dbConnection, gbid, days, roleid);            	
            	if( smsText != null && smsText.trim().isEmpty() == false){
                	for( Message msg : targetList){
                		msg.sendMessage(dbConnection, providercode, userid, password, smsText, null) ;
                	}
            	}
            	followupDone( dbConnection, id ) ;
            }
			stmt.close();
        } catch(Exception e){
        	e.printStackTrace();
        }
		
	}

}
