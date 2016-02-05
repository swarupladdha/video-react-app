package alerts.email;

import java.io.FileInputStream;

import java.sql.ResultSet;
import java.sql.Blob;

import java.util.List;
import java.util.Set;
import java.util.Iterator;
import java.util.Properties;
import java.util.HashMap;

import javax.activation.DataHandler;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.Multipart;

import javax.mail.util.ByteArrayDataSource;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.w3c.dom.Node;

import org.apache.log4j.Logger;

import alerts.sms.SMSProvider;
import alerts.sms.SMSProviderFactory;
import alerts.utils.Constants;
import alerts.utils.TargetUser;
import alerts.utils.Utils;

/** The thread or probe that looks for messages in the messagesintable
 *  and sends the message via email or sms based on the messageType.
 *  Once the message is sent, if it is an sms, it is stored in the
 *  messages_sent_table along with the responseId.  Later on the 
 *  MessagesSentTableProbe probes the messages_sent_table and comes
 *  up with the status of the message.  The messages are deleted from
 *  the messagesintable by this thread.
 *  @author Sushma P
 *  @date July-19-2010
 *  @version 1.0
 */

public class MessagesInTableProbe implements Runnable {

    private int id = 0;
    private HashMap<String, String> defaultProviderParameters ;    
    static final Logger logger = Logger.getLogger(MessagesInTableProbe.class);

    public MessagesInTableProbe(int id) {
        this.id = id; 
    }
    
    public HashMap<String , String> getSMSProviderParameters( VinrMessageParser parser) {
    	
    	String providerCode ;
    	HashMap<String, String> providerParameters  = null ;
    	try {

	    	String fileName = System.getenv("DefaultSmsProvider_CONFIG_FILE");      			
	      	FileInputStream propFile = new FileInputStream(fileName);
	       	Properties p = new Properties(System.getProperties());
	      	p.load(propFile);
	        String senderId = parser.getSenderId();	
	       	
	    	
	        if (parser.isForceDefaultProvider() == false) {
	        		providerCode = parser.getPrimaryProviderCode();  		     
	        		providerParameters = parser.getPrimaryProviderParameters();
	        }
	        else {
	        		providerCode = p.getProperty("default_provider_code"); // This is the default provider code set in defaultProvider.ini
	        		String userName = p.getProperty("default_username"); 
	        		if (senderId != null && senderId.isEmpty() == false) {
	        			// retain the sender id
	        		}else {
		        		senderId = p.getProperty("default_sid") ;	        			
	        		}
	        		String passwd = p.getProperty("default_password");
	        		providerParameters = new HashMap<String, String>();
	        		providerParameters.put(VinrMessageParser.PRIMARY_PROVIDER_CODE, providerCode) ;
	        		providerParameters.put(VinrMessageParser.PRIMARY_PROVIDER_USERID, Utils.encrypt(userName) );
	        		providerParameters.put(VinrMessageParser.PRIMARY_PROIVDER_PASSWORD, Utils.encrypt(passwd));
	        }
	        
	   	 	providerParameters.put(VinrMessageParser.PRIMARY_PROIVDER_SID, senderId);
	   	 	String urlCode = providerCode.toUpperCase() + "_URL" ;
	   	 	String url = p.getProperty( urlCode ) ;	
	      	if (url == null) {
	       		System.out.println("URL not found for provider code : " + urlCode + " in " + fileName ) ;
	       	}
		 
	       	if (senderId != null && senderId.isEmpty() == false) {
	       		senderId = p.getProperty( VinrMessageParser.PRIMARY_PROIVDER_SID);           		 		
	       	}
	
	   	 	providerParameters.put(VinrMessageParser.PRIMARY_PROIVDER_URL, url) ;
    	}
    	
    	catch(Exception e) {
    		e.printStackTrace() ;    		
    	}
   	 	return providerParameters ;
    	
    }

    public void run() {
        
        VinrMessagesInTable mesgInTable = new VinrMessagesInTable();
        int msgid=-1 ;
        while (true) {
            try {
            	Thread.sleep( 1000 ) ;
                mesgInTable.setConnection();
                ResultSet rs1 = mesgInTable.readNewMessages(this.id);
                 while (rs1.next()) {
                     String sms_response = null;
                     String email = null;
                     boolean is_Insert_Success = false;

                     msgid = rs1.getInt(1);
                     int msgtype = rs1.getInt(2);
                     String address = rs1.getString(3);
                     String message = rs1.getString(4);
				     String versionColumn = rs1.getString(5);	
				     String provider = rs1.getString(6);	
				     Blob blobContent = rs1.getBlob(7);	
                     String date = rs1.getString(8);
                     String customData = rs1.getString(9);
                     String msgIdString = "" + msgid;
                     boolean bodyType ;
              
				     String xmlMessage = new StringBuffer("<message>").append(rs1.getString(3)).append(rs1.getString(4)).append(rs1.getString(5)).append(rs1.getString(6)).append("</message>").toString();
				     logger.debug("The value of XMLMessage in MessagesInTableProbe is ::::::---> "+ xmlMessage);	
		   	        
       
                     VinrMessageParser parser = new VinrMessageParser();
                     parser.parse(xmlMessage);	

                     String sender = parser.getFromList();
                     List<TargetUser> recipients = parser.getToList();
                     List<TargetUser> informList = parser.getInformUsers() ;
                     String cc = parser.getCCList();
                     String subject = parser.getSubject();
                     String body = parser.getBody();
                     String attachmentName = parser.getAttachmentName();		     
                     String version = parser.getVersion();
                     String numbersList = parser.getMobileList();
                     String textMessage = parser.getShortText();	
		     
                     String primaryProviderCode ;
		     
                     bodyType = parser.getBodyContentType() ;

                     if (msgtype == Constants.EMAIL_CODE) {                         
                    	
                         if (attachmentName == null) {
                             email = VinrEmailDispatcher.sendPersonalPlainMail(subject, body, sender, recipients, cc, msgid, bodyType);
                         } else {
                             email = VinrEmailDispatcher.sendPersonalAttachmentMail(subject, body, sender,recipients,cc, msgid, blobContent, attachmentName, bodyType);
                         }
                         
                         if (email.equalsIgnoreCase(Constants.SUCCESS_STRING)) {
                        	 if (informList != null && informList.isEmpty() == false) {
                            	 VinrEmailDispatcher.sendConfirmation(subject, body, sender, recipients, informList , msgid, blobContent, attachmentName ) ;
                        	 }
                             mesgInTable.deleteMessage(msgid);
                             //is_Insert_Success = messagesSentTable.insertDataIntoTable(msgid, msgtype, sms_response, address, message, provider, date, customData);    
                         }
                         if (email.equalsIgnoreCase( Constants.ERROR_STRING ) ) {
                        	 mesgInTable.touchMessage(msgid, 3600) ;
                         }
            		Thread.sleep( 150000 ) ;
                     }

                     if (msgtype == Constants.SMS_CODE) {
                    	 SMSProvider smsProvider ;
                         HashMap<String, String> providerParameters = getSMSProviderParameters( parser ) ;
                         String providerCode = providerParameters.get( VinrMessageParser.PRIMARY_PROVIDER_CODE) ;
 
                         smsProvider = SMSProviderFactory.getSMSProviderInstance(providerCode);
                             logger.debug("The value of txtMsg before passing to sendSMSroutine is  ----> " + textMessage);	
                             sms_response = smsProvider.sendSMS(providerParameters, msgIdString, recipients, textMessage); //gets the response back from the call to sendSMS()

                         if (sms_response.equalsIgnoreCase(Constants.SUCCESS_STRING)) {
                             mesgInTable.deleteMessage(msgid);
                             //is_Insert_Success = messagesSentTable.insertDataIntoTable(msgid, msgtype, sms_response, address, message, provider, date, customData);
                         }
                         if (sms_response.equalsIgnoreCase( Constants.ERROR_STRING ) ) {
                        	 mesgInTable.touchMessage(msgid, 3600) ;
                         }
                     }
                     

    
                 }//end of while
                 rs1.close();
             } catch(Exception e) {     
            	 logger.error("Excepton Caught in MessagesinTableProbe Class") ;
            	 logger.error(e.getMessage()) ;
                 e.printStackTrace();     
                 mesgInTable.touchMessage(msgid, 3600) ;
                 mesgInTable.releaseConnection();
                 //messagesSentTable.releaseConnection();
                 //System.exit(0) ;
             } finally {
                 mesgInTable.releaseConnection();
                 //messagesSentTable.releaseConnection();
             }
             
             Thread.yield();
        } 
    } 
}

/*
if (msgtype == Constants.EMAIL_AND_SMS_CODE) {

SMSProviderFactory factory = new SMSProviderFactory();
SMSProvider smsProvider = null;

if (attachmentName == null) {
    email = VinrEmailDispatcher.sendPersonalPlainMail(subject, body, sender, recipients, cc, msgid, bodyType);
} else {
    email = VinrEmailDispatcher.sendPersonalAttachmentMail(subject, body, sender,recipients,cc, msgid, blobContent, attachmentName, bodyType);                              
}        
if (email.equalsIgnoreCase( Constants.ERROR_STRING ) ) {
	 mesgInTable.touchMessage(msgid, 300) ;
}


if((version != null) && !(version.equals(""))) {
    smsProvider = factory.getSMSProviderInstance(primaryProvider);                                                              
    sms_response = smsProvider.sendSMS(primaryProviderParameters, msgIdString, recipients, textMessage);
} else {
    String defaultProvider = Constants.DEFAULT_SMS_PROVIDER;

    smsProvider = factory.getSMSProviderInstance(defaultProvider);
    sms_response = smsProvider.sendSMS(defaultProviderParameters, msgIdString, recipients, textMessage);
}
if (sms_response.equalsIgnoreCase( Constants.ERROR_STRING ) ) {
	 mesgInTable.touchMessage(msgid, 300) ;
}

// SMS Unicel code
if ((sms_response.equalsIgnoreCase(Constants.SUCCESS_STRING)) || (email.equalsIgnoreCase(Constants.SUCCESS_STRING) && sms_response.equalsIgnoreCase(Constants.SUCCESS_STRING))) {
    mesgInTable.deleteMessage(msgid);
    //is_Insert_Success = messagesSentTable.insertDataIntoTable(msgid, msgtype, sms_response, address, message, provider, date, customData);
}
//mesgInTable.deleteMessage(msgid);
}                      

//                             String defaultLowProvider = Constants.DEFAULT_SMS_PROVIDER;
                             SMSProvider defaultHighProvider = SMSProviderFactory.getSMSProviderInstance(new String ("unicel")) ;
                             //smsProvider = SMSProviderFactory.getSMSProviderInstance(defaultHighProvider);
                             HashMap defaultUnicelProviderParameters = new HashMap();
                			 defaultUnicelProviderParameters.put("userid", "RaosIn");
                			 defaultUnicelProviderParameters.put("password", "56R2Q");
                			 defaultUnicelProviderParameters.put("sid", "groupz");            			
                             
                             sms_response = defaultHighProvider.sendSMS(defaultUnicelProviderParameters, msgIdString, recipients, textMessage);				
                         }

*
*/
