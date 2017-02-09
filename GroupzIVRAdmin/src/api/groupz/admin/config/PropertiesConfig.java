package api.groupz.admin.config;

import java.io.InputStream;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class PropertiesConfig {

	public static Properties prop = new Properties();
	
	public static String AdminServiceType;
	public static String AdminFunctionType;
	public static String GetListFunctionType;
		
	static
	{
		try
		{
			InputStream in = PropertiesConfig.class.getResourceAsStream("/api.properties");
			prop.load(in);
			AdminServiceType = prop.getProperty("adminservicetype");
			AdminFunctionType = prop.getProperty("adminfunctiontype");
			GetListFunctionType = prop.getProperty("getlistfunctiontype");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static boolean isEmptyOrNull(String str)
	{
		if (str == null || str.trim().isEmpty() == true || str.equalsIgnoreCase("[]")==true || str.equalsIgnoreCase("[\"\"]")==true || str.equalsIgnoreCase("{}")==true || str.equalsIgnoreCase("{\"\"}")==true || str.equalsIgnoreCase("")==true || str.length() == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public static String createResponse1(String statuscode, String statusmessage,Object jarray2)
	{
		JSONObject json = new JSONObject();
		JSONObject resp = new JSONObject();
		JSONObject responseObj = new JSONObject();
		responseObj.put("statuscode", statuscode);
		responseObj.put("statusmessage", statusmessage);
		if(jarray2 instanceof JSONArray){
			responseObj.put("data", jarray2);
		}else{
			responseObj.put("data", jarray2);
		}		
		
		resp.put("response",responseObj);
		json.put("json",resp);
		return json.toString();
	}
	
	public static String createResponse(String statuscode, String statusmessage)
	{
		JSONObject json = new JSONObject();
		JSONObject resp = new JSONObject();
		JSONObject dataObj = new JSONObject();
		JSONObject responseObj = new JSONObject();
		
		responseObj.put("statuscode", statuscode);
		responseObj.put("statusmessage", statusmessage);
		System.out.println("responseObj : "+responseObj);
		dataObj.put("data", responseObj);
		resp.put("response",dataObj);
		json.put("json",resp);
		return json.toString();
	}	
}

