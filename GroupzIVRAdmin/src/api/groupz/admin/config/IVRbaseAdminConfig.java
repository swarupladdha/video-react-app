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
import api.groupz.admin.module.IVRbaseAdminService;

public class IVRbaseAdminConfig
{
	public static Properties prop = new Properties();
	
	public static String IVRAdminServiceType;
	public static String addIVRBaseFunctionType ;
	public static String updateIVRBaseFunctionType;
	//public static String deleteIVRFunctionType;
	public static String getIVRBaseFunctionType;
	
	static
	{
		try
		{
			InputStream in = IVRbaseAdminService.class.getResourceAsStream("/api.properties");
			prop.load(in);
			IVRAdminServiceType = prop.getProperty("ivradminservicetype");
			addIVRBaseFunctionType = prop.getProperty("addivrbasefunctiontype") ;
			updateIVRBaseFunctionType = prop.getProperty("updateivrbasefunctiontype");
//			deleteIVRFunctionType = prop.getProperty("deleteivrbasefunctiontype");
			getIVRBaseFunctionType = prop.getProperty("getivrbasefunctiontype");
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
	
	public static boolean checkvalidation(String ivrData, String[] mandatory_keys)
	{
		
		boolean flag = false;
		
		JSONObject json = (JSONObject) JSONSerializer.toJSON(ivrData);
		System.out.println("json : "+ json);
		
		for(int i=0; i<mandatory_keys.length; i++)
		{			
			if (json.containsKey(mandatory_keys[i].trim())) 
			{ 
				System.out.println("inside if : "+mandatory_keys[i]);
				flag = true;
				continue;
			}
		    else
		    {		 
		    	System.out.println("else : "+mandatory_keys[i]);
		    	flag = false; 
		    	break;
		    }
		}
		return flag;
	}
	
	public static String createResponse1(String statuscode, String statusmessage,Object jarray)
	{
		JSONObject json = new JSONObject();
		JSONObject resp = new JSONObject();
		JSONObject responseObj = new JSONObject();
		responseObj.put("statuscode", statuscode);
		responseObj.put("statusmessage", statusmessage);
		if(jarray instanceof JSONArray){
			responseObj.put("data", jarray);
		}else{
			responseObj.put("data", jarray);
		}		
		
		resp.put("response",responseObj);
		json.put("json",resp);
		return json.toString();
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
        		
        		if(IVRbaseAdminConfig.isEmptyOrNull(field_value) == true)
        		{
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
	
	public static String createResponse1(String statuscode, String statusmessage,JSONObject dataObj)
	{
		JSONObject json = new JSONObject();
		JSONObject resp = new JSONObject();
		JSONObject responseObj = new JSONObject();
		responseObj.put("statuscode", statuscode);
		responseObj.put("statusmessage", statusmessage);
		responseObj.put("data", dataObj);
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
	
	
}