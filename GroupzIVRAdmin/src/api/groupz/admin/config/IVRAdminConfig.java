package api.groupz.admin.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import api.groupz.admin.module.IVRAdminService;

public class IVRAdminConfig
{
	public static Properties prop = new Properties();
	
	public static String IVRAdminServiceType;
	public static String addIVRFunctionType ;
	public static String updateIVRFunctionType;
	//public static String deleteIVRFunctionType;
	public static String getIVRFunctionType;
	public static String adminservicetype;
	public static String adminfuncttiontype;
	
	static
	{
		try
		{
			InputStream in = IVRAdminService.class.getResourceAsStream("/api.properties");
			prop.load(in);
			IVRAdminServiceType = prop.getProperty("ivradminservicetype");
			addIVRFunctionType = prop.getProperty("addivrfunctiontype") ;
			updateIVRFunctionType = prop.getProperty("updateivrfunctiontype");
//			deleteIVRFunctionType = prop.getProperty("deleteivrfunctiontype");
			getIVRFunctionType = prop.getProperty("getivrfunctiontype");
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

	public static boolean checkvalidation(String ivrData, String[] mandatory_keys)
	{
		
		boolean flag = false;
		for(int i=0; i<mandatory_keys.length; i++)
		{
			JSONObject json = (JSONObject) JSONSerializer.toJSON(ivrData);
			
			if (json.has(mandatory_keys[i])) 
			{ 
				flag = true;
				continue;
			}
		    else
		    {
		    	flag = false; 
		    	break;
		    }
		}
		return flag;
	}

	public static boolean checkvalidate(String ivrData, String[] mandatory_keys)
	{
		boolean response = true;
			
		JSONObject json = (JSONObject) JSONSerializer.toJSON(ivrData);
		
		Iterator jsonObj = json.keys();
		List<String> keysList = new ArrayList<String>();
		while(jsonObj.hasNext())
		{
		    String updatekey = (String) jsonObj.next();
		    keysList.add(updatekey);
		}
		String[] update_keys = keysList.toArray(new String[keysList.size()]);
		System.out.println("update_keys: "+Arrays.toString(update_keys));
		
		
		String field_value = null;
		String value = "";
		       
		for(int i = 0; i < update_keys.length; i++)
        {
        	if (Arrays.asList(mandatory_keys).contains(update_keys[i]))
        	{
        		String available_mandatory_column = update_keys[i];
        		System.out.println("available_mandatory_column : "+available_mandatory_column);
        		
        		field_value = json.getString(available_mandatory_column);
        		System.out.println("field_value : "+field_value );
        		if(IVRAdminConfig.isEmptyOrNull(field_value) == true)
        		{
					 //String response = "missingFieldValue for " + available_mandatory_column;
					System.out.println("Error: missing Field Value for mandatory column " + "\""  + available_mandatory_column + "\"" + "ie it is set to null value " );
					response=false;
					break;
				}
				else
				{
					value = available_mandatory_column + "  is : " + field_value;
					System.out.println("Value for " +value);
				}
        	}
        	else
        	{
        		System.out.println(update_keys[i] + " is not available in mandatory_keys");
        	}
        }
		System.out.println("response : "+response);
        return response;
	}
	
	public static String createResponse1(String statuscode, String statusmessage,Object dataObj)
	{
		JSONObject json = new JSONObject();
		JSONObject resp = new JSONObject();
		JSONObject responseObj = new JSONObject();
		responseObj.put("statuscode", statuscode);
		responseObj.put("statusmessage", statusmessage);
		if(dataObj instanceof JSONArray){
			responseObj.put("data", dataObj);
		}else{
			responseObj.put("data", dataObj);
		}		
		
		resp.put("response",responseObj);
		json.put("json",resp);
		return json.toString();
	}
	
	public static String createResponse(String statuscode, String statusmessage)
	{
		JSONObject json = new JSONObject();
		JSONObject resp = new JSONObject();
		JSONObject responseObj = new JSONObject();
		responseObj.put("statuscode", statuscode);
		responseObj.put("statusmessage", statusmessage);
		System.out.println("responseObj : "+responseObj);
	//	responseObj.put("data", dataObj);
		resp.put("response",responseObj);
		json.put("json",resp);
		return json.toString();
	}

	public static boolean duplicateEntry(String word, String errormessage)
	{
		if(errormessage.indexOf(word) >= 0)
		{
	        return true;
		}
	    else
	    {
	        return false;
	    }
	}
	
	public static boolean isNumber(String str)
	{
		if (str == null || str.length() == 0)
		{
			return false;
		}
		for(int i=0; i<str.length(); i++)
		{
			char ch = str.charAt(i);
			if(!Character.isDigit(ch))
			{
				return false;
			}
		}
		return true;
		
	}

	public static String checkwelcomenotes(String ivrData, String key)
	{
		JSONObject json = (JSONObject) JSONSerializer.toJSON(ivrData);
		
		JSONObject jReq = json.getJSONObject(key) ;
		String welcomeNotes_value = jReq.getString("welcomenotesList");
		System.out.println("welcomeNotes_value : "+welcomeNotes_value);
		if (IVRAdminConfig.isEmptyOrNull(welcomeNotes_value.trim()) == true)
		{ 
			String statuscode = IVRAdminConfig.prop.getProperty("errorCode");
		    String statusmessage = IVRAdminConfig.prop.getProperty("missingFieldValue " + key + " ie it is set to null value");
		    String response = IVRAdminConfig.createResponse(statuscode, statusmessage);
			return response;
		}
		return null;
	}

	public static String checkselectionlist(String ivrData, String key)
	{
		JSONObject json = (JSONObject) JSONSerializer.toJSON(ivrData);
		
		JSONObject jReq = json.getJSONObject(key) ;
		String selectionList_value = jReq.getString("selectionList");
		System.out.println("selectionList_value : "+selectionList_value);
		if (IVRAdminConfig.isEmptyOrNull(selectionList_value.trim()) == true)
		{ 
			String statuscode = IVRAdminConfig.prop.getProperty("errorCode");
		    String statusmessage = IVRAdminConfig.prop.getProperty("missingFieldValue " + key + " ie it is set to null value");
		    String response = IVRAdminConfig.createResponse(statuscode, statusmessage);
			return response;
		}
		return null;
	}
	
	
}