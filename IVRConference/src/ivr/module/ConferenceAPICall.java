package ivr.module;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;


public class ConferenceAPICall
{

	public static String ConferenceAPImoderatorCallconnect(String moderator_no, String caller_id, String conference_id)
    {
        try
        {
			String url = "http://www.kookoo.in/outbound/outbound.php";
						
			URIBuilder uribuilder = new URIBuilder(url);
			
			uribuilder.addParameter("api_key", "KK12744fd00ade494d5010b080d19be78a");
			uribuilder.addParameter("caller_id", caller_id);
			uribuilder.addParameter("phone_no", moderator_no);
			uribuilder.addParameter("url", "http://prod1.groupz.in:7070/KookooAPI/KookooModeratorConference.jsp?called_number="+moderator_no+"&conference_id="+conference_id);
			uribuilder.addParameter("callback_url","http://prod1.groupz.in:7070/KookooAPI/KookooCallbackServlet?api_key=KK12744fd00ade494d5010b080d19be78a&caller_id="+caller_id+"&calling_no="+ moderator_no+"&conference_id="+conference_id);    
			
			URI uri = uribuilder.build();
			System.out.println("Final Outboud API url " + uri);
			
			HttpGet httpget = new HttpGet(uri);
			HttpClient  client = HttpClientBuilder.create().build();
			HttpResponse outboundResponse = client.execute(httpget);
			String responseString = new BasicResponseHandler().handleResponse(outboundResponse);
			System.out.println(responseString);
            return responseString;        	
        } 
        catch (Exception e)
        {
            e.printStackTrace();
			return null;
        }
        
    }

	public static String ConferenceAPIparticipantCallconnect(String participant_no, String caller_id, String conference_id, String moderator_no)
	{
		try
        {
			String url = "http://www.kookoo.in/outbound/outbound.php";
						
			URIBuilder uribuilder = new URIBuilder(url);
			
			uribuilder.addParameter("api_key", "KK12744fd00ade494d5010b080d19be78a");
			uribuilder.addParameter("caller_id", caller_id);
			uribuilder.addParameter("phone_no", participant_no);
			uribuilder.addParameter("url", "http://prod1.groupz.in:7070/KookooAPI/KookooParticipantConference.jsp?called_number="+participant_no+"&conference_id="+conference_id);
			uribuilder.addParameter("callback_url","http://prod1.groupz.in:7070/KookooAPI/KookooCallbackServlet?api_key=KK12744fd00ade494d5010b080d19be78a&caller_id="+caller_id+"&calling_no="+ participant_no+"&conference_id="+conference_id);    
			
			URI uri = uribuilder.build();
			System.out.println("Final Outboud API url " + uri);
			
			HttpGet httpget = new HttpGet(uri);
			HttpClient  client = HttpClientBuilder.create().build();
			HttpResponse outboundResponse = client.execute(httpget);
			String responseString = new BasicResponseHandler().handleResponse(outboundResponse);
			System.out.println(responseString);
            return responseString;        	
        } 
        catch (Exception e)
        {
            e.printStackTrace();
			return null;
        }
	}
}
