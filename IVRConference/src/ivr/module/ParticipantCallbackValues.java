package ivr.module;

import ivr.utils.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ParticipantCallbackValues
{
	public static String session_Id_p = "";
	public static String callendStatus_p = "";
	public static String conference_Id_p = "";
	public static String calling_No_p = "";
	public static String start_Time_p = "";
	public static String call_Duration_p = "";
	
	static String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
	
	public static String getSessionIdParticipant(String sessionIdParticipant) 
	{ 
		session_Id_p = sessionIdParticipant;
//		System.out.println("sessionId_p : "+sessionIdParticipant);
		return sessionIdParticipant; 
	} 
	
	public static String getconferenceIdParticipant(String conferenceIdParticipant) 
	{ 
		conference_Id_p = conferenceIdParticipant;
//		System.out.println("conferenceId_p : "+conferenceIdParticipant);
		return conferenceIdParticipant; 
	} 
	
	public static String getCallstatusParticipant(String callEndStatusParticipant) 
	{ 
		callendStatus_p = callEndStatusParticipant;
//		System.out.println("status_p : "+callEndStatusParticipant);
		return callEndStatusParticipant; 
	}
	 
	public static String getcalledNoParticipant(String calledNoParticipant) 
	{ 
		calling_No_p = calledNoParticipant;
//		System.out.println("calledNo_p : "+calledNoParticipant);
		return calledNoParticipant; 
	}
	
	public static String getDurationParticipant(String durationParticipant) 
	{ 
		call_Duration_p = durationParticipant;
//		System.out.println("duration_p : "+durationParticipant);
		return durationParticipant; 
	}
	
	public static String getstartTimeParticipant(String startTimeParticipant) 
	{ 
		start_Time_p = startTimeParticipant;
//		System.out.println("startTime_p : "+startTimeParticipant);
		return startTimeParticipant; 
	}
	
	
	public static String insertParticipantValues(String caller_id, String participant_no)
	{
		String session_id = session_Id_p;
		System.out.println("session_id_p : "+session_id);
		String callendStatus = callendStatus_p;
		System.out.println("callendStatus_p : "+callendStatus);
		String calling_no = calling_No_p;
		System.out.println("calling_no_p : "+calling_no);
		String startTime = start_Time_p;
		System.out.println("start_time_p : "+startTime);
		String call_duration = call_Duration_p;
		System.out.println("call_duration_p : "+call_duration);
		String conferenceId = conference_Id_p;
		System.out.println("conferenceId_p : "+conferenceId);
		
		String start_Time = DateFormat.getLatestSynchTime();
        SimpleDateFormat formatter = new SimpleDateFormat(DATEFORMAT);
		Date start_time = DateFormat.StringDateToDate(start_Time);
        System.out.println("start_time_p : "+ start_time);
			
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
		System.out.println("end_time_p : "+end_time);        
       
		System.out.println("calling_no before loop : "+calling_no );
		System.out.println("participant_no before loop : "+participant_no );
		
		if(calling_no.equalsIgnoreCase(participant_no.trim()))
		{
			System.out.println("Passing value to conference1");
			String send_values = InsertValues.Conference1values(caller_id, session_id, callendStatus, conferenceId, calling_no, start_time, call_duration, end_time);
		}
		String response = "Inserted into conference1";
		return response;
	}
}
