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

	String updateToLevelFourEscalations="update issueassignment set  CurrentEscalationLevel=4 , LastEscalationTime = now() "
			+" where enddate is null and CurrentEscalationLevel =3 and  STR_TO_DATE(LevelFourEscalationTime,'%H:%i')< current_time() "
			+" and LevelFourEscalationDate< current_time()";
	
	String updateToLevelThreeEscalations="update issueassignment set  CurrentEscalationLevel=3 , LastEscalationTime = now() "
			+" where enddate is null and CurrentEscalationLevel =2 and  STR_TO_DATE(LevelThreeEscalationTime,'%H:%i')< current_time() "
			+" and LevelThreeEscalationDate< current_time()";
	
	String updateToLevelTwoEscalations="update issueassignment set  CurrentEscalationLevel=2 , LastEscalationTime = now() "
			+" where enddate is null and CurrentEscalationLevel =1 and  STR_TO_DATE(LevelTwoEscalationTime,'%H:%i')< current_time() "
			+" and LevelTwoEscalationDate< current_time()";
	
	String updateToLevelOneEscalations="update issueassignment set  CurrentEscalationLevel=1 , LastEscalationTime = now() "
			+" where enddate is null and CurrentEscalationLevel =0 and  STR_TO_DATE(LevelOneEscalationTime,'%H:%i')< current_time() "
			+" and LevelOneEscalationDate< current_time()";
	
	public void run(Connection connection) {
		// TODO Auto-generated method stub
		Statement stmt=null;
	
		try {
		stmt = connection.createStatement();
		
		int res =0;
			String updateEscalationsQuery =updateToLevelOneEscalations;
			res = stmt.executeUpdate(updateEscalationsQuery);
			
			updateEscalationsQuery =updateToLevelTwoEscalations;
			res = stmt.executeUpdate(updateEscalationsQuery);
			
			updateEscalationsQuery =updateToLevelThreeEscalations;
			res = stmt.executeUpdate(updateEscalationsQuery);
			
			updateEscalationsQuery =updateToLevelFourEscalations;
			res = stmt.executeUpdate(updateEscalationsQuery);
			
			System.out.println("getServiceRequestsList Sql:"+res);
			
			
		
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
