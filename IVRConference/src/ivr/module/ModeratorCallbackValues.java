package ivr.module;

import ivr.utils.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ModeratorCallbackValues
{
	public static String session_Id_m = "";
	public static String callendStatus_m = "";
	public static String conference_Id_m = "";
	public static String calling_No_m = "";
	public static String start_Time_m = "";
	public static String call_Duration_m = "";

	static String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static String getSessionIdModerator(String sessionIdModerator) 
	{ 
		session_Id_m = sessionIdModerator;
//		System.out.println("sessionId_m : "+sessionIdModerator);
		return sessionIdModerator; 
	} 
	
	public static String getconferenceIdModerator(String conferenceIdModerator) 
	{ 
		conference_Id_m = conferenceIdModerator;
//		System.out.println("conferenceId_m : "+conferenceIdModerator);
		return conferenceIdModerator; 
	} 
	
	public static String getCallstatusModerator(String callEndStatusModerator) 
	{ 
		callendStatus_m = callEndStatusModerator;
//		System.out.println("status_m : "+callEndStatusModerator);
		return callEndStatusModerator; 
	}
	 
	public static String getcalledNoModerator(String calledNoModerator) 
	{ 
		calling_No_m = calledNoModerator;
//		System.out.println("calledNo_m : "+calledNoModerator);
		return calledNoModerator; 
	}
	
	public static String getDurationModerator(String durationModerator) 
	{ 
		call_Duration_m = durationModerator;
//		System.out.println("duration_m : "+durationModerator);
		return durationModerator; 
	}
	
	public static String getstartTimeModerator(String startTimeModerator) 
	{ 
		start_Time_m = startTimeModerator;
//		System.out.println("startTime_m : "+startTimeModerator);
		return startTimeModerator; 
	}

	public static String insertModeratorValues(String caller_id, String moderator_no)
	{
		String session_id = session_Id_m;
		System.out.println("session_id_m : "+session_id);
		String callendstatus = callendStatus_m;
		System.out.println("callendStatus_m : "+callendstatus);
		String calling_no = calling_No_m;
		System.out.println("calling_no_m : "+calling_no);
		String startTime = start_Time_m;
		System.out.println("start_time_m : "+startTime);
		String call_duration = call_Duration_m;
		System.out.println("call_duration_m : "+call_duration);
		String conferenceId = conference_Id_m;
		System.out.println("conferenceId_m : "+conferenceId);
		
		String start_Time = DateFormat.getLatestSynchTime();
        SimpleDateFormat formatter = new SimpleDateFormat(DATEFORMAT);
		Date start_time = DateFormat.StringDateToDate(start_Time);
        System.out.println("start_time_m : "+ start_time);
			
		String time = call_duration; 
		int seconds = 0;
		try
		{
			seconds = Integer.parseInt(time);
		}
		catch(NumberFormatException e)
		{
			e.printStackTrace();
			seconds=0;
		}
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(start_time);
	    cal.add(Calendar.SECOND, seconds);
	    Date date1 = cal.getTime();
	    String end_Time = formatter.format(date1);
	    
	    Date end_time = DateFormat.StringDateToDate(end_Time);		
		System.out.println("end_time_m : "+end_time);        
       		
		System.out.println("calling_no before loop : "+calling_no );
		System.out.println("moderator_no before loop : "+moderator_no );
		
		if(calling_no.equalsIgnoreCase(moderator_no.trim()))
		{
			System.out.println("Passing value to conferencecall");
			String send_values = InsertValues.ConferenceCallvalues(caller_id, session_id, callendstatus, conferenceId, calling_no, start_time, call_duration, end_time);
		}
		String response = "Inserted into conferencecall";
		return response;
	}
}
