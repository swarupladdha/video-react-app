package api.groupz.admin.module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import api.groupz.admin.config.DBOperation;
import api.groupz.admin.config.IVRAdminConfig;
import api.groupz.admin.config.IVRbaseAdminConfig;
import api.groupz.admin.config.PropertiesConfig;

public class IVRbaseAdminService
{

	public String process(String ivrData, String servicetype, String functiontype)
	{
		System.out.println("a");
		String response = "";

		try
		{
			if (IVRbaseAdminConfig.isEmptyOrNull(servicetype) == true)
			{
				String statuscode = IVRbaseAdminConfig.prop.getProperty("errortypecode");
			    String statusmessage = IVRbaseAdminConfig.prop.getProperty("errortype");
			    response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
				return response;
			}
			System.out.println("service Type : "+servicetype);
			System.out.println("function Type : "+functiontype);
			
			if((IVRbaseAdminConfig.IVRAdminServiceType).equalsIgnoreCase(servicetype)==false)
			{
				
				String statuscode = IVRbaseAdminConfig.prop.getProperty("invalidtypecode");
			    String statusmessage = IVRbaseAdminConfig.prop.getProperty("invalidtype");
			    response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
				return response;
			}
			else
			{
				
				if (IVRbaseAdminConfig.isEmptyOrNull(functiontype) == true)
				{
					String statuscode = IVRbaseAdminConfig.prop.getProperty("errortypecode");
				    String statusmessage = IVRbaseAdminConfig.prop.getProperty("errortype");
				    response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
					return response;
				}
				else
				{
					if (functiontype.equalsIgnoreCase(IVRbaseAdminConfig.addIVRBaseFunctionType))
					{
						System.out.println("b");
						String insertValue = processAddIVRBase(ivrData);
						response = insertValue;
						return response;
					}
					else if (functiontype.equalsIgnoreCase(IVRbaseAdminConfig.updateIVRBaseFunctionType))
					{
						String updateValues = processUpdateIVRBase(ivrData);
						response = updateValues;
						return response;
					}
					else if (functiontype.equalsIgnoreCase(IVRbaseAdminConfig.getIVRBaseFunctionType))
					{
						System.out.println("Inside IVRFunctionType");
						String getSelectedValues = processGETIVRBase(ivrData);
						response = getSelectedValues;
						return response;
					}
					else if (functiontype.equalsIgnoreCase(PropertiesConfig.GetListFunctionType))
					{
						String list = DBOperation.getList(ivrData);
						response=list;
						return response;
						
					}
					else
					{
						String statuscode = IVRbaseAdminConfig.prop.getProperty("invalidtypecode");
					    String statusmessage = IVRbaseAdminConfig.prop.getProperty("invalidtype");
					    response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
						return response;
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
		    String statusmessage = IVRbaseAdminConfig.prop.getProperty("updatetableerror");
		    response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
			return response;
		}
	}

	private String processAddIVRBase(String ivrData)
	{
		System.out.println("c");
		String response = "";
		String[] mandatory_keys = {"ivrnumber", "grpzWelcomeNotes", "selectionHangupNotes", "selectionEndNotes", "errorNotes", "memberWelcomeNotes", "notRegGroupzNotes", "generalHangupNotes", "groupzBase", "numbersUrllist", "previousMenuSelectNotes"};
		boolean processvalidate = IVRbaseAdminConfig.checkvalidation(ivrData, mandatory_keys);

		String value = "";
		
		if (processvalidate == false)
		{
			String statuscode = IVRbaseAdminConfig.prop.getProperty("missingfieldcode");
		    String statusmessage = IVRbaseAdminConfig.prop.getProperty("missingfield");
		    response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
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
						String statusmessage = "Ivrnumber is not an number";
						response = IVRAdminConfig.createResponse(statuscode, statusmessage);
						return response;
					}
				}
				
//				if ((key == "grpzWelcomeNotes") ||(key == "selectionHanupNotes") || (key == "audioSelectionHangupUrl") || (key == "selectionEndNotes") || (key == "memberWelcomeNotes") || (key == "audiomemberWelcomeUrl") || (key == "notRegGroupzNotes") || (key == "maintenaceNotes") || (key == "generalHangupNotes") || (key == "previousMenuSelectNotes") || (key == "previousMenuSelectUrl"))
				if ((key.equalsIgnoreCase("grpzWelcomeNotes")) ||(key.equalsIgnoreCase("selectionHangupNotes")) || (key.equalsIgnoreCase("selectionEndNotes")) || (key.equalsIgnoreCase("memberWelcomeNotes")) || (key.equalsIgnoreCase("notRegGroupzNotes")) || (key.equalsIgnoreCase("generalHangupNotes")) || (key.equalsIgnoreCase("previousMenuSelectNotes")) || (key.equalsIgnoreCase("errorNotes")))
				{						
					JSONObject jReq = json.getJSONObject(key) ;
					String dataList_value = jReq.getString("dataList");
					System.out.println("dataList_value : "+dataList_value);
					if (IVRbaseAdminConfig.isEmptyOrNull(dataList_value.trim()) == true)
					{ 
						String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
					    String statusmessage = IVRbaseAdminConfig.prop.getProperty("missingmandatoryfieldvalue") + " " + key + " ie it is set to null value";
					    response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
						return response;
					}
				}
				
				if(key.equalsIgnoreCase("numbersUrlList"))
				{
					JSONObject jReq = json.getJSONObject(key) ;
					String urlList_value = jReq.getString("urlList");
					System.out.println("urlList_value : "+urlList_value);
					if (IVRbaseAdminConfig.isEmptyOrNull(urlList_value.trim()) == true)
					{ 
						String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
					    String statusmessage = IVRbaseAdminConfig.prop.getProperty("missingmandatoryfieldvalue") + " " + key + " ie it is set to null value";
					    response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
						return response;
					}
				}
				
				if (IVRbaseAdminConfig.isEmptyOrNull(field_value) == true)
				{ 
					String statuscode = IVRbaseAdminConfig.prop.getProperty("missingfieldvaluecode");
				    String statusmessage = IVRbaseAdminConfig.prop.getProperty("missingmandatoryfieldvalue " + mandatory_keys[i] + " ie it is set to null value");
				    response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
					return response;
				}
				else
				{
					value = mandatory_keys[i] + " is : " + field_value;
					System.out.println("value of mandatory : "+ value);
				}
			}
		}
		String insertvalues = IVRbaseDBOperations.connectDBandInsert(ivrData);
		response = insertvalues;
		return response;
	}

	private String processUpdateIVRBase(String ivrData)
	{
		String response = "";
		String[] mandatory_keys = {"ivrnumber", "grpzWelcomeNotes", "selectionHangupNotes", "selectionEndNotes", "errorNotes", "memberWelcomeNotes", "notRegGroupzNotes", "generalHangupNotes", "numbersUrllist", "previousMenuSelectNotes", "groupzBase"};
		boolean processvalidate = IVRbaseAdminConfig.checkvalidate(ivrData, mandatory_keys);

		String value = "";
		
		if (processvalidate == false)
		{
			String statuscode = IVRbaseAdminConfig.prop.getProperty("missingfieldvaluecode");
		    String statusmessage = IVRbaseAdminConfig.prop.getProperty("missingmandatoryfieldvalue");
		    response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
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
					
//				    if ((key == "grpzWelcomeNotes") ||(key == "selectionHanupNotes") || (key == "audioSelectionHangupUrl") || (key == "selectionEndNotes") || (key == "memberWelcomeNotes") || (key == "audiomemberWelcomeUrl") || (key == "notRegGroupzNotes") || (key == "maintenaceNotes") || (key == "generalHangupNotes") || (key == "previousMenuSelectNotes") || (key == "previousMenuSelectUrl"))
					if ((key.equalsIgnoreCase("grpzWelcomeNotes")) ||(key.equalsIgnoreCase("selectionHangupNotes")) || (key.equalsIgnoreCase("selectionEndNotes")) || (key.equalsIgnoreCase("memberWelcomeNotes")) || (key.equalsIgnoreCase("notRegGroupzNotes")) || (key.equalsIgnoreCase("generalHangupNotes")) || (key.equalsIgnoreCase("previousMenuSelectNotes")) || (key.equalsIgnoreCase("errorNotes")))
					{						
						JSONObject jReq = json.getJSONObject(key) ;
						String dataList_value = jReq.getString("dataList");
						System.out.println("dataList_value : "+dataList_value);
						if (IVRbaseAdminConfig.isEmptyOrNull(dataList_value.trim()) == true)
						{ 
							String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
						    String statusmessage = IVRbaseAdminConfig.prop.getProperty("missingmandatoryfieldvalue") + " " + key + " ie it is set to null value";
						    response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
							return response;
						}
					}
					
					if(key.equalsIgnoreCase("numbersUrlList"))
					{
						JSONObject jReq = json.getJSONObject(key) ;
						String urlList_value = jReq.getString("urlList");
						System.out.println("urlList_value : "+urlList_value);
						if (IVRbaseAdminConfig.isEmptyOrNull(urlList_value.trim()) == true)
						{ 
							String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
						    String statusmessage = IVRbaseAdminConfig.prop.getProperty("missingmandatoryfieldvalue") + " " + key + " ie it is set to null value";
						    response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
							return response;
						}
					}
					
					if (IVRbaseAdminConfig.isEmptyOrNull(field_value) == true)
					{ 
						String statuscode = IVRbaseAdminConfig.prop.getProperty("missingfieldvaluecode");
					    String statusmessage = IVRbaseAdminConfig.prop.getProperty("missingmandatoryfieldvalue " + mandatory_keys[i] + " ie it is set to null value");
					    response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
						return response;
					}
					else
					{
						value = key + " is : " + field_value;
						System.out.println("value of mandatory : "+ value);
					}
				}
			}
			String updateValues = IVRbaseDBOperations.connectDBandUpdate(ivrData);
			response = updateValues;
			return response;
		}
	}

	private String processGETIVRBase(String ivrData)
	{
		System.out.println("GETIVRBase");
		String response = "";
		

			String getValues = IVRbaseDBOperations.connectDBandGet(ivrData);
			response = getValues;
			return response;

	}
}