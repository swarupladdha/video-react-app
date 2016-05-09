package api.groupz.admin.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import api.groupz.admin.config.IVRAdminConfig;


public class IVRAdminService
{

	public String process(String ivrData, String servicetype, String functiontype)
	{
		String response = "";

		try
		{
			if (IVRAdminConfig.isEmptyOrNull(servicetype) == true)
			{
				String statuscode = IVRAdminConfig.prop.getProperty("errortypecode");
			    String statusmessage = IVRAdminConfig.prop.getProperty("errortype");
			    response = IVRAdminConfig.createResponse(statuscode, statusmessage);
				return response;
			}
			System.out.println("service Type : "+servicetype);
			System.out.println("function Type : "+functiontype);
			if((IVRAdminConfig.IVRAdminServiceType).equalsIgnoreCase(servicetype)==false)
			{
				System.out.println("service invalid");
				String statuscode = IVRAdminConfig.prop.getProperty("invalidtypecode");
			    String statusmessage = IVRAdminConfig.prop.getProperty("invalidtype");
			    response = IVRAdminConfig.createResponse(statuscode, statusmessage);
				return response;
			}
			else
			{
				
				if (IVRAdminConfig.isEmptyOrNull(functiontype) == true)
				{
					String statuscode = IVRAdminConfig.prop.getProperty("errortypecode");
				    String statusmessage = IVRAdminConfig.prop.getProperty("errortype");
				    response = IVRAdminConfig.createResponse(statuscode, statusmessage);
					return response;
				}
				else
				{
					if (functiontype.equalsIgnoreCase(IVRAdminConfig.addIVRFunctionType))
					{
						String insertValue = processAddIVRBase(ivrData);
						response = insertValue;
						return response;
					}
					else if (functiontype.equalsIgnoreCase(IVRAdminConfig.updateIVRFunctionType))
					{
						String updateValues = processUpdateIVRBase(ivrData);
						response = updateValues;
						return response;
					}
					else if (functiontype.equalsIgnoreCase(IVRAdminConfig.getIVRFunctionType))
					{
						String getSelectedValues = processGETIVRBase(ivrData);
						response = getSelectedValues;
						return response;
					}
					else
					{
						System.out.println("function invalid");
						String statuscode = IVRAdminConfig.prop.getProperty("invalidtypecode");
					    String statusmessage = IVRAdminConfig.prop.getProperty("invalidtype");
					    response = IVRAdminConfig.createResponse(statuscode, statusmessage);
						return response;
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
		    String statusmessage = IVRAdminConfig.prop.getProperty("updatetableerror");
		    response = IVRAdminConfig.createResponse(statuscode, statusmessage);
			return response;
		}
	}

	private String processAddIVRBase(String ivrData)
	{
		String response = "";
		String[] mandatory_keys = { "ivrnumber", "groupzCode", "welcomeNotes", "selectionlist", "groupzNameUrl", "multiLanguageFlag"};
		boolean processvalidate = IVRAdminConfig.checkvalidation(ivrData, mandatory_keys);

		String value = "";
		
		if (processvalidate == false)
		{
			String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
		    String statusmessage = IVRAdminConfig.prop.getProperty("missingfield");
		    response = IVRAdminConfig.createResponse(statuscode, statusmessage);
			return response;
		}
		else
		{
			
			String field_value = null;
			String key = null;
			JSONObject json = (JSONObject) JSONSerializer.toJSON(ivrData);
			
			for (int i = 0; i < mandatory_keys.length; i++)
			{
				key = mandatory_keys[i];
				field_value = json.getString(mandatory_keys[i]);
				
				if (key.equalsIgnoreCase("ivrnumber"))
				{
					String ivrNumber = json.getString(key);
					
					if (IVRAdminConfig.isEmptyOrNull(ivrNumber.trim()) == true)
					{ 
						String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
					    String statusmessage = IVRAdminConfig.prop.getProperty("missingmandatoryfieldvalue") + " " + key + " ie it is set to null value";
					    response = IVRAdminConfig.createResponse(statuscode, statusmessage);
						return response;
					}
					
					if(IVRAdminConfig.isNumber(ivrNumber)== false)
					{
						String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
						String statusmessage = "Ivrnumber is not a number";
						response = IVRAdminConfig.createResponse(statuscode, statusmessage);
						return response;
					}
				}
				
				if (key.equalsIgnoreCase("welcomeNotes"))
				{	
					//String checkwelcomeNotes = IVRAdminConfig.checkwelcomenotes(ivrData, key);
					
					JSONObject jReq = json.getJSONObject(key) ;
					String welcomeNotes_value = jReq.getString("welcomenotesList");
					System.out.println("welcomeNotes_value : "+welcomeNotes_value);
					if (IVRAdminConfig.isEmptyOrNull(welcomeNotes_value.trim()) == true)
					{ 
						String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
					    String statusmessage = IVRAdminConfig.prop.getProperty("missingmandatoryfieldvalue") + " " + key + " ie it is set to null value";
					    response = IVRAdminConfig.createResponse(statuscode, statusmessage);
						return response;
					}
				}
				
				if (key.equalsIgnoreCase("selectionlist"))
				{	
					//String checkselectionlist = IVRAdminConfig.checkselectionlist(ivrData, key);
					
					JSONObject jReq = json.getJSONObject(key) ;
					String selectionList_value = jReq.getString("selectionList");
					System.out.println("selectionList_value : "+selectionList_value);
					if (IVRAdminConfig.isEmptyOrNull(selectionList_value.trim()) == true)
					{ 
						String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
					    String statusmessage = IVRAdminConfig.prop.getProperty("missingmandatoryfieldvalue") + " " + key + " ie it is set to null value";
					    response = IVRAdminConfig.createResponse(statuscode, statusmessage);
						return response;
					}
				}
				
				if (IVRAdminConfig.isEmptyOrNull(field_value) == true)
				{ 
					String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
				    String statusmessage = IVRAdminConfig.prop.getProperty("missingmandatoryfieldvalue") + " " + mandatory_keys[i] + " ie it is set to null value";
				    System.out.println("statusmessage : "+statusmessage);
				    response = IVRAdminConfig.createResponse(statuscode, statusmessage);
					return response;
				}
				else
				{
					value = mandatory_keys[i] + " is : " + field_value;
					System.out.println("value of mandatory : "+ value);
				}
			}
		}
		String insertvalues = IVRDBOperations.connectDBandInsert(ivrData);
		response = insertvalues;
		return response;
	}

	private String processUpdateIVRBase(String ivrData)
	{
		String response = "";
		String[] mandatory_keys = { "ivrnumber", "groupzCode", "welcomeNotes", "selectionlist", "groupzNameUrl", "multiLanguageFlag"};
		boolean processvalidate = IVRAdminConfig.checkvalidate(ivrData, mandatory_keys);

		String value = "";
		
		if (processvalidate == false)
		{
			String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
		    String statusmessage = IVRAdminConfig.prop.getProperty("missingfield");
		    response = IVRAdminConfig.createResponse(statuscode, statusmessage);
			return response;
		} 
		else
		{	
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
			String key = null;
						       
			for(int i = 0; i < update_keys.length; i++)
	        {
	        	if (Arrays.asList(mandatory_keys).contains(update_keys[i]))
	        	{
	        		String mandatory_key = update_keys[i];
	        		System.out.println("available_mandatory_column : "+mandatory_key);
	        		
	        		field_value = json.getString(mandatory_key);
	        		System.out.println("field_value : "+field_value );
	        		
	        		key = update_keys[i].trim();
					System.out.println("key : " + update_keys[i]);
					
					if (key.equalsIgnoreCase("ivrnumber"))
					{
						String ivrNumber = json.getString(key);
						
						if (IVRAdminConfig.isEmptyOrNull(ivrNumber.trim()) == true)
						{ 
							String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
						    String statusmessage = IVRAdminConfig.prop.getProperty("missingmandatoryfieldvalue") + " " + key + " ie it is set to null value";
						    response = IVRAdminConfig.createResponse(statuscode, statusmessage);
							return response;
						}
						
						if(IVRAdminConfig.isNumber(ivrNumber)== false)
						{
							String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
							String statusmessage = "Ivrnumber is not an number";
							response = IVRAdminConfig.createResponse(statuscode, statusmessage);
							return response;
						}
					}
					
					if (key.equalsIgnoreCase("welcomeNotes"))
					{	
						//String checkwelcomeNotes = IVRAdminConfig.checkwelcomenotes(ivrData, key);
						
						JSONObject jReq = json.getJSONObject(key) ;
						String welcomeNotes_value = jReq.getString("welcomenotesList");
						System.out.println("welcomeNotes_value : "+welcomeNotes_value);
						if (IVRAdminConfig.isEmptyOrNull(welcomeNotes_value.trim()) == true)
						{ 
							String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
						    String statusmessage = IVRAdminConfig.prop.getProperty("missingmandatoryfieldvalue") + " " + key + " ie it is set to null value";
						    response = IVRAdminConfig.createResponse(statuscode, statusmessage);
							return response;
						}
					}
	        				
					if (key.equalsIgnoreCase("selectionlist"))
					{	
						//String checkselectionlist = IVRAdminConfig.checkselectionlist(ivrData, key);
						
						JSONObject jReq = json.getJSONObject(key) ;
						String selectionList_value = jReq.getString("selectionList");
						System.out.println("selectionList_value : "+selectionList_value);
						if (IVRAdminConfig.isEmptyOrNull(selectionList_value.trim()) == true)
						{ 
							String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
						    String statusmessage = IVRAdminConfig.prop.getProperty("missingmandatoryfieldvalue") + " " + key + " ie it is set to null value";
						    response = IVRAdminConfig.createResponse(statuscode, statusmessage);
							return response;
						}
					}
					
					if (IVRAdminConfig.isEmptyOrNull(field_value) == true)
					{ 
						String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
					    String statusmessage = IVRAdminConfig.prop.getProperty("missingmandatoryfieldvalue") + " " + mandatory_keys[i] + " ie it is set to null value";
					    System.out.println("statusmessage : "+statusmessage);
					    response = IVRAdminConfig.createResponse(statuscode, statusmessage);
						return response;
					}
					else
					{
						value = key + " is : " + field_value;
						System.out.println("value of mandatory : "+ value);
					}
				}
			}
			String updateValues = IVRDBOperations.connectDBandUpdate(ivrData);
			response = updateValues;
			return response;
		}
	}

	private String processGETIVRBase(String ivrData)
	{
		String response = "";
		
		String[] mandatory_keys = { "ivrnumber", "groupzCode", "welcomeNotes", "selectionlist", "groupzNameUrl", "multiLanguageFlag"};

		boolean processvalidate = IVRAdminConfig.checkvalidate(ivrData, mandatory_keys);

		String value = "";
		
		if (processvalidate == false)
		{
			String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
		    String statusmessage = IVRAdminConfig.prop.getProperty("missingfield");
		    response = IVRAdminConfig.createResponse(statuscode, statusmessage);
			return response;
		}
		else
		{

			String getValues = IVRDBOperations.connectDBandGet(ivrData);
			response = getValues;
			return response;
		}

	}
}