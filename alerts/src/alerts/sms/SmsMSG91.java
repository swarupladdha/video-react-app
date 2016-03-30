package alerts.sms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import alerts.utils.TargetUser;
import alerts.utils.Utils ;

import org.apache.log4j.Logger;

import alerts.email.VinrMessageParser;
import alerts.utils.Constants;


/** This class contains the sendSMS() implementation for 
 *  for SmsCountry
 *
 *  @author Sushma
 *  @date July-08-2010
 *  @version 1.0
 */

public class SmsMSG91 implements SMSProvider {
    static final Logger logger = Logger.getLogger(SmsMSG91.class);
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
			        while( (response = reader.readLine()) != null)
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
}                  
