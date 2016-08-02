package com.groupz.followup.manager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;



public class ServiceRequestManager {
	
	
	public ServiceRequestManager() {
		
		
	}
	
	
	
	Collection<String> stringList = new ArrayList<String>();
	String getServiceRequestsList="select id,"
	 		+ "case"
	 		+ " when (LevelOneEscalationDate < current_time() and STR_TO_DATE(LevelOneEscalationDate,'%H:%i')<CURTIME() "
	 		+ " and CurrentEscalationLevel <= 1) then 'LevelOneEscalationDate'"
	 		+ " when (LevelTwoEscalationDate < current_time() and STR_TO_DATE(LevelTwoEscalationTime,'%H:%i')<CURTIME() "
	 		+ " and CurrentEscalationLevel = 2)  then 'LevelTwoEscalationDate'"
	 		+ " when (LevelThreeEscalationDate < current_time() and STR_TO_DATE(LevelThreeEscalationTime ,'%H:%i')<CURTIME() "
	 		+ "and CurrentEscalationLevel =3)then 'LevelThreeEscalationDate'"
	 		+ "   else 'none'"
	 		+ "end as escalationLevel , CurrentEscalationLevel from issueassignment"
	 		+ " where enddate is  null and LevelFourEscalationDate!=4";
//select * from issueassignment where enddate is null and levelthreeescalationdate  < currenttime() and curesclevel < 3;		
		
		String updateServiceRequest="update issueassignment set CurrentEscalationLevel = %s , LastEscalationTime=CURTIME()"
				+ " where id = %s  and CurrentEscalationLevel = '%s' ";
//update issueassignment set curesclevel = N ,esctime=now() where enddate is null and levelthreeescalationdate > now() and curesclevel < N;	
		
	public void run(Connection connection, int sRPoolSize, int threadId) {
		// TODO Auto-generated method stub
		Statement stmt=null;
		Statement stmt2=null;
		try {
		stmt = connection.createStatement();
		String poolSize=String.valueOf(sRPoolSize);
		String threadid=String.valueOf(threadId);
			String QueryString = String.format(getServiceRequestsList);
			System.out.println("getServiceRequestsList Sql:"+QueryString);
			ResultSet rs = stmt.executeQuery(QueryString);
				while (rs.next()) {
					if (!(rs.getString("escalationLevel").equals("none"))) {
					int curesclev=rs.getInt("CurrentEscalationLevel")+1;
					String Query = String.format(updateServiceRequest,String.valueOf(curesclev),rs.getString("id"),rs.getString("CurrentEscalationLevel"));
					System.out.println("updateServiceRequest Sql:"+Query);
					stmt2 = connection.createStatement();
					int res = stmt2.executeUpdate(Query);
					}
					}
			
		
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
		}
			
			   
			
		 
	}
	

}
