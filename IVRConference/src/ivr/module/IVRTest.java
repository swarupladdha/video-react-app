package ivr.module;

import java.util.Date;

import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;


public class IVRTest
{
	
	public String process(String ivrData) throws JSONException
	{
		String response = "";
		String caller_id = "918030860779";
		String conference_id = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		
		JSONObject jsonObj = new JSONObject(ivrData);
		Object moderator_name = jsonObj.get("moderator");
		String moderator_no = String.valueOf(moderator_name);
		System.out.println("moderator_no : "+moderator_no);
		Object participant_name = jsonObj.get("participant");
		String participant_no = String.valueOf(participant_name);
		System.out.println("participant_no : "+participant_no);
		System.out.println("conference_id : "+conference_id);
		
		if (moderator_no == null)
		{
			response = "Error : moderator number is null";
			return response;
			
		}
		
		if (participant_no == null)
		{
			response = "Error : participant number is null";
			return response;
		}

		String moderator_call_output =  ConferenceAPICall.ConferenceAPImoderatorCallconnect(moderator_no, caller_id, conference_id);
		String participant_call_output =  ConferenceAPICall.ConferenceAPIparticipantCallconnect(participant_no, caller_id, conference_id, moderator_no);
		
		try
		{
			Thread.sleep(60000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		String insertmoderatorValues =ModeratorCallbackValues.insertModeratorValues(caller_id, moderator_no);
		String insertparticipantValues =ParticipantCallbackValues.insertParticipantValues(caller_id, participant_no);	
		response = "Success";
		return response;	
	}
}
