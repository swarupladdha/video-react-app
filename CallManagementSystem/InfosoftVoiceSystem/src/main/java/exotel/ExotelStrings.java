package exotel;

public interface ExotelStrings {	
	  String CONNECT_TO_AGENT_URL         = "https://api.exotel.com/v1/Accounts/%s/Calls/connect.json";
	  String CONNECT_CUSTOMER_TO_FLOW_URL = "https://api.exotel.com/v1/Accounts/%s/Calls/connect.json";
	  String SEND_UNICODE_URL             = "https://api.exotel.com/v1/Accounts/%s/Sms/send.json";	 
	  String BRING_BACK_RESPONSE          = "https://api.exotel.com/v1/Accounts/\" + exotelSid + \"/Calls/\" + callSid + \".json\"";
}
