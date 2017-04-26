package alerts.sms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.*;

import alerts.utils.TargetUser;
import alerts.utils.Utils ;
import alerts.sms.*;

import org.json.JSONObject;
import org.json.JSONArray;
import org.apache.log4j.Logger;

import alerts.email.VinrMessageParser;
import alerts.email.VinrMessagesOutTable;
import alerts.utils.Constants;


/** This class contains the sendSMS() implementation for 
 *  for SmsCountry
 *
 *  @author Sushma
 *  @date July-08-2010
 *  @version 1.0
 */

public class SmsMSG91 implements SMSProvider {
	VinrMessagesOutTable msgouttab = new VinrMessagesOutTable();
	
	
	JSONArray ja = new JSONArray();
    static final Logger logger = Logger.getLogger(SmsMSG91.class);

    public static class MyHostnameVerifier implements HostnameVerifier {
    	public boolean verify(String hostname, SSLSession session) {
    	// verification of hostname is switched off
    	return true;
    	}
    	}
    
   public String sendSingleSMS(HashMap providerParams, String msgId, List<TargetUser> numbersList, String textMessage) { 
	

        try {

        	String userId = (String) providerParams.get(VinrMessageParser.PRIMARY_PROVIDER_USERID);
        	String mainURL = (String) providerParams.get(VinrMessageParser.PRIMARY_PROIVDER_URL) ;
    	    String password = (String) providerParams.get( VinrMessageParser.PRIMARY_PROIVDER_PASSWORD);
	    
	    
		    String authKey = Utils.decrypt( userId ) ;
		    String senderId = (String) providerParams.get(VinrMessageParser.PRIMARY_PROIVDER_SID);
		    String route = Utils.decrypt(password) ;
		    String mobile = null;
		    String message = null ;
	    
	    

		    for ( TargetUser tUser : numbersList ) {

		    	mobile = tUser.getMobileNumber() ;
		    	
		    	if ( mobile != null && mobile.isEmpty() == false && 
		    			textMessage != null && textMessage.isEmpty() == false) {	    	
		    		String replacedMessage = tUser.personalizeMessage(textMessage) ;
		    		//String replacedMessage = personalizedSMS ; //Utils.replaceCharacterWithSpacesAround( personalizedSMS, '&', 'n') ; 
		    		//replacedMessage = Utils.replaceCharacterWithWord( replacedMessage, '@', new String("(at)")) ; 
		    		message = URLEncoder.encode( replacedMessage, "UTF-8" ) ;
			    	StringBuilder sbPostData= new StringBuilder(mainURL);
			    	sbPostData.append("authkey="+authKey); 
			    	sbPostData.append("&mobiles="+mobile);
			    	sbPostData.append("&message="+message);
			    	sbPostData.append("&route="+route);
			    	sbPostData.append("&sender="+senderId);
		
			    	//final string
			    	String finalURL = sbPostData.toString();
	
			        URL url = new URL(finalURL);
			        URLConnection conn = url.openConnection();
			        conn.setDoOutput(true);
			        conn.connect();
			        BufferedReader reader= new BufferedReader(new InputStreamReader(conn.getInputStream()));
			        String response ;
			        response = reader.readLine();
			        while( (response) != null)
			        response += response ;
		            reader.close();         
		            System.out.println("The value of responseId in MSG91 CLASS IS ----========------> " + response);                
		            
			    }		    	
		    }
		    return Constants.SUCCESS_STRING;			
	        } catch(Exception e) {
	            e.printStackTrace();
	            return Constants.ERROR_STRING;
	        } 
	        //return Constants.SUCCESS_STRING;     
	    } 
   
    public String sendSMS(HashMap providerParams, String msgId, List<TargetUser> numbersList, String textMessage) { 
    	

        try {

        	String userId = (String) providerParams.get(VinrMessageParser.PRIMARY_PROVIDER_USERID);
        	String mainURL = (String) providerParams.get(VinrMessageParser.PRIMARY_PROIVDER_URL) ;
    	    String password = (String) providerParams.get( VinrMessageParser.PRIMARY_PROIVDER_PASSWORD);
	    
	    
		    String authKey = Utils.decrypt( userId ) ;
		    String senderId = (String) providerParams.get(VinrMessageParser.PRIMARY_PROIVDER_SID);
		    String route = Utils.decrypt(password) ;
		    String mobile = null;
		    String message = null ;
	    
	    	StringBuilder sbPostData= new StringBuilder(mainURL);
	    	String xmlData = "<AUTHKEY>"+authKey+"</AUTHKEY>" ;
	    	xmlData = xmlData + "<SENDER>"+senderId+"</SENDER>" ;
	    	xmlData = xmlData + "<ROUTE>" + route + "</ROUTE>" ;
	    

		    for ( TargetUser tUser : numbersList ) {

		    	mobile = tUser.getMobileNumber() ;
		    	
		    	if ( mobile != null && mobile.isEmpty() == false && 
		    			textMessage != null && textMessage.isEmpty() == false) {	    	
		    		String replacedMessage = tUser.personalizeMessage(textMessage) ;
		    		//String replacedMessage = personalizedSMS ; //Utils.replaceCharacterWithSpacesAround( personalizedSMS, '&', 'n') ; 
		    		//replacedMessage = Utils.replaceCharacterWithWord( replacedMessage, '@', new String("(at)")) ; 
		    		
		    		message = URLEncoder.encode( replacedMessage, "UTF-8" ) ;
		    		
		    		String smsContent = "<SMS TEXT=\"" + replacedMessage +"\">" ;
		    		smsContent = smsContent + "<ADDRESS TO=\"" + mobile + "\">"+"</ADDRESS>" ;
		    		smsContent = smsContent + "</SMS>" ;
		    		 xmlData = xmlData + smsContent ;
			    
		    
		    	}
		    	
		    }
		   
		    xmlData = "<MESSAGE>" + xmlData + "</MESSAGE>" ;
		    //System.out.println("XML posted = " + xmlData) ;
		    sbPostData.append( "data=" + xmlData) ;
		   
			    	//final string
			    	String finalURL = sbPostData.toString();
	
			        URL url = new URL(finalURL);
			        HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
			        ((HttpsURLConnection) conn).setHostnameVerifier(new MyHostnameVerifier());
			        conn.setDoOutput(true);
			        conn.connect();
			        BufferedReader reader= new BufferedReader(new InputStreamReader(conn.getInputStream()));
			        String response ;
			       
			        while( (response = reader.readLine()) != null)
			    {	  
			        SentSMS(providerParams,msgId,numbersList,textMessage,response,message);
			        response += response ;
			        System.out.println("The value of responseId in MSG91 CLASS IS ----========------> " +response); 
			        //SentSMS(providerParams,msgId,numbersList,textMessage,response,message);
			    }
			        System.out.println("XML posted = " + xmlData) ;
			        reader.close();
			        
		                          
			        
		    return Constants.SUCCESS_STRING;			
	        } catch(Exception e) {
	            e.printStackTrace();
	            return Constants.ERROR_STRING;
	        } 
	        //return Constants.SUCCESS_STRING;     
	    } 
    public String SentSMS( HashMap providerParams, String msgId, List<TargetUser> numbersList, 
    		String textMessage,String response,String message)
    {
    
    	JSONObject jo=null;

        try {
        	/*String userId = (String) providerParams.get(VinrMessageParser.PRIMARY_PROVIDER_USERID);
    	    String password = (String) providerParams.get( VinrMessageParser.PRIMARY_PROIVDER_PASSWORD);

    	    String authKey = Utils.decrypt( userId ) ;
		    String senderId = (String) providerParams.get(VinrMessageParser.PRIMARY_PROIVDER_SID);
		    String route = Utils.decrypt(password) ;*/
		    String mobile = null;
		   // String message = null ;
	    	
		    String providerCode = "QKONCT";
		    String DeliveryStatus = "Sent";
		    
		    for ( TargetUser tUser : numbersList ) {

		    	mobile = tUser.getMobileNumber() ;
		    	
		    	if ( mobile != null && mobile.isEmpty() == false && 
		    		textMessage != null && textMessage.isEmpty() == false) 
		    		{	    	
		    		String replacedMessage = tUser.personalizeMessage(textMessage) ;
		    	    jo = getData(response, msgId, numbersList, textMessage,mobile, providerCode,DeliveryStatus,message);
		        	addJSON(jo);
		    	    }
		    }
		    printArray();
		   // System.out.println("-------------A"+ja);
		    msgouttab.setConnection(ja);
		   // System.out.println("CHECK 1");
		    return Constants.SUCCESS_STRING;			
        	} catch(Exception e) {
            e.printStackTrace();
            return Constants.ERROR_STRING;
        } 
    }
    
    public JSONObject getData( String response,String msgId, List<TargetUser> numbersList,
    		String textMessage,String mobile,String providerCode, String DeliveryStatus,String message)
    {
    	JSONObject obj = new JSONObject();
    	obj.put("MsgId", msgId);
    	obj.put("Response ID",response);
    	obj.put("Mobile", mobile);	 
    	obj.put("Message", message );
    	obj.put("ProviderCode", providerCode);
    	obj.put("DeliveryStatus", DeliveryStatus );
    	//System.out.println("CHECK 2");
    	//msgouttab.writeNewMessages( mobile, message, response, providerCode,DeliveryStatus);
    	return obj;
    	
		
    	}
    public void addJSON(JSONObject jo)
    {
    	System.out.println("AddJson Method");
    	ja.put(jo);
    }
    
    public JSONArray printArray(){
    	System.out.println("JSON Array is :" + ja.toString());
    	JSONArray ar = ja;
    	return ar;
    }  

}    
