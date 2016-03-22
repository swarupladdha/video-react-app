package alerts.sms;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.util.HashMap;

import alerts.utils.TargetUser;
import alerts.utils.Utils ;

import org.w3c.dom.Node;
import org.apache.log4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import alerts.email.VinrMessagesSentTable;
import alerts.email.VinrMessagesInTable;
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
        	String numbersString = new String() ;
    	    String password = (String) providerParams.get( VinrMessageParser.PRIMARY_PROIVDER_PASSWORD);
	    
	    
		    String authKey = Utils.decrypt( userId ) ;
		    String senderId = (String) providerParams.get(VinrMessageParser.PRIMARY_PROIVDER_SID);int count = 1  ;
		    String route = Utils.decrypt(password) ;
		    String mobile = null;
		    String message = null ;
	    
	    

		    for ( TargetUser tUser : numbersList ) {
		    	mobile = tUser.getMobileNumber() ;
		    	
		    	if ( mobile != null && mobile.isEmpty() == false) {	    	
		    		String personalizedSMS = tUser.personalizeMessage(textMessage) ;
		    		String replacedMessage = personalizedSMS ; //Utils.replaceCharacterWithSpacesAround( personalizedSMS, '&', 'n') ; 
		    		//replacedMessage = Utils.replaceCharacterWithWord( replacedMessage, '@', new String("(at)")) ; 
		    		message = URLEncoder.encode( replacedMessage, "UTF-8" ) ;
		    	}
		    	StringBuilder sbPostData= new StringBuilder(mainURL);
		    	sbPostData.append("authkey="+authKey); 
		    	sbPostData.append("&mobiles="+mobile);
		    	sbPostData.append("&message="+message);
		    	sbPostData.append("&route="+route);
		    	sbPostData.append("&sender="+senderId);
	
		    	//final string
		    	mainURL = sbPostData.toString();

		        URL url = new URL(mainURL);
		        URLConnection conn = url.openConnection();
		        conn.setDoOutput(true);
		        conn.connect();
		        BufferedReader reader= new BufferedReader(new InputStreamReader(conn.getInputStream()));
		        String response ;
		        while( (response = reader.readLine()) != null)
		        	response += response ;

	            reader.close();         
	            System.out.println("The value of responseId in VinrSMSCountry CLASS IS ----========------> " + response);                
		    }		    	
		    return Constants.SUCCESS_STRING;			
	        } catch(Exception e) {
	            e.printStackTrace();
	            return Constants.ERROR_STRING;
	        } 
	        //return Constants.SUCCESS_STRING;     
	    } 
}                  
