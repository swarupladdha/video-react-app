package ivr.module;

import java.util.Date;
import com.gpzhibernate.DBCommonOpertion;

import ivr.tables.Conference1;
import ivr.tables.ConferenceCall;
import ivr.utils.DBConnect;


public class InsertValues
{
	
	public static String ConferenceCallvalues(String caller_id, String session_id, String callendStatus, String conferenceId, 
			String calling_no, Date start_time, String call_duration, Date end_time)
	{
		String response = "";
		
		DBConnect dbConnect = new DBConnect();
		boolean connectionStatus = dbConnect.establishConnection();
		System.out.println("Connection Status:" + connectionStatus);
			
		if (connectionStatus == true)
		{
			try
			{
				System.out.println("Connected to conferencecall");
				ConferenceCall con = new ConferenceCall();
				con.setModerator_no(calling_no);
				con.setConference_id(conferenceId);
				con.setSession_id(session_id);
				con.setCaller_id(caller_id);
				con.setCall_status(callendStatus);
				con.setStart_time(start_time);
				con.setDuration(call_duration);
				String File_name = caller_id + conferenceId;
				String record_location = "http://recordings.kookoo.in/groupz/"+File_name+".wav";
				con.setRecorded_call(record_location);
//				con.setRinging_time(ringing_time);		        		        
		        con.setEnd_time(end_time);
				con.save();
				System.out.println("Record Inserted into conferencecall");
				response = "Record Inserted into conferencecall";
			}
			catch(Exception e)
		    {
		    	e.printStackTrace();
		    	response = "Error in Inserting";
		    }
		}
		return response;	
	
	}

	public static String Conference1values(String caller_id, String session_id, String callendStatus, String conferenceId, String calling_no,
			Date start_time, String call_duration, Date end_time)
	{
		String response = "";
		
		DBConnect dbConnect = new DBConnect();
		boolean connectionStatus = dbConnect.establishConnection();
		System.out.println("Connection Status:" + connectionStatus);
			
		if (connectionStatus == true)
		{
			try
			{
				
				System.out.println("Connected");
				Conference1 con1 = new Conference1();
				con1.setParticipate_no(calling_no);
				con1.setCall_status(callendStatus);
				con1.setCaller_id(caller_id);
				con1.setSession_id(session_id);
				con1.setStart_time(start_time);
				con1.setDuration(call_duration);
//				con1.setRinging_time(ringing_time);
				ConferenceCall cc = (ConferenceCall) DBCommonOpertion.getSingleDatabaseObject(ConferenceCall.class, "conferenceId='"+conferenceId+"'");
				if(cc!=null)
				{
					System.out.println("Conference_id inside conference1 : "+cc);
					con1.setConferenceId(cc);
				}	        		        
		        con1.setEnd_time(end_time);
				con1.save();
				System.out.println("Record Inserted into conference1");
				response = "Record Inserted into conference1";
			}
			catch(Exception e)
		    {
		    	e.printStackTrace();
		    	response = "Error in Inserting";
		    }
		}
		return response;
	}
}