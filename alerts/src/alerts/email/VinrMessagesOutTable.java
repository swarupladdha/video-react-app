package alerts.email;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.log4j.Logger;

import alerts.sms.SmsMSG91;

//import alerts.utils.Constants;

/** This class connects the code for getting the connection from the connection pool
 *  and query the messagesouttable.
 *  @author Sushma P
 *  @date July-2-2010
 *  @version 1.0
 */

public class VinrMessagesOutTable {
     Connection conn = null;
   
    static final Logger logger = Logger.getLogger(VinrMessageParser.class);
  
    
    public void setConnection(JSONArray ja2) {
        
    		try{
    		conn = ConnectDatabase.establishConnection();
    		//System.out.println("--------------B"+ja2+"---connection--"+conn);
            writeNewMessages(ja2);
			}catch(Exception e)
    		{
				e.printStackTrace();
			}
        
    }

    public boolean writeNewMessages(JSONArray ja2) {
        logger.debug("Is connection null? " + conn);        
        if( conn == null){
        	return false;
        }
        try {
        
        	Statement stmt = null;
        	
            stmt = conn.createStatement();	                	
            for(int i=0;i<ja2.length();i++)
        	{
            JSONObject jo1 = ja2.getJSONObject(i);
            String msgid = jo1.getString("MsgId");
            String mobile = jo1.getString("Mobile");
            String message = jo1.getString("Message");
			String response = jo1.getString("Response ID");
			String providerCode = jo1.getString("ProviderCode");
			//String deliveryStatus = jo1.getString("DeliveryStatus");
			String sql = "insert into messagesouttable(msgId,Address,message,resid,providercode) values ('"
					+msgid+ "','"+mobile+ "','"+message+"','"+response+"','"+ providerCode +/*"','"+deliveryStatus+*/"')";
			stmt.executeUpdate(sql);
        	}
            
//            System.out.println("CHECK WRITENEWMESSAGES");
            return true;
        	
            
        } catch (Exception e) {
            //System.out.println("Unable to connect to database.");
            e.printStackTrace();
            return false;
            
        }
    }

}


