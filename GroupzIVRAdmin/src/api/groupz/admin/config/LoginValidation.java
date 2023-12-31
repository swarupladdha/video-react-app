package api.groupz.admin.config;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class LoginValidation {
	public String process(String data, String servicetype, String functiontype)
	{
		String response = "";

		try
		{
			if (PropertiesConfig.isEmptyOrNull(servicetype) == true)
			{
				System.out.println("a");
				String statuscode = PropertiesConfig.prop.getProperty("errortypecode");
			    String statusmessage = PropertiesConfig.prop.getProperty("errortype");
			    response = PropertiesConfig.createResponse(statuscode, statusmessage);
				return response;
			}
			System.out.println("service Type : "+servicetype);
			System.out.println("function Type : "+functiontype);
			if((PropertiesConfig.AdminServiceType).equalsIgnoreCase(servicetype)==false)
			{
				System.out.println("b");
				System.out.println("service invalid");
				String statuscode = PropertiesConfig.prop.getProperty("invalidtypecode");
			    String statusmessage = PropertiesConfig.prop.getProperty("invalidtype");
			    response = PropertiesConfig.createResponse(statuscode, statusmessage);
				return response;
			}
			else
			{
				System.out.println("c");
				
				if (PropertiesConfig.isEmptyOrNull(functiontype) == true)
				{
					System.out.println("d");
					String statuscode = PropertiesConfig.prop.getProperty("errortypecode");
				    String statusmessage = PropertiesConfig.prop.getProperty("errortype");
				    response = PropertiesConfig.createResponse(statuscode, statusmessage);
					return response;
				}
				else if (functiontype.equalsIgnoreCase(PropertiesConfig.AdminFunctionType))
				{
					System.out.println("e");
					String validate = processvalidate(data);
					response = validate;
					return response;
				}
				
				else
				{
					System.out.println("function invalid");
					String statuscode = PropertiesConfig.prop.getProperty("invalidtypecode");
				    String statusmessage = PropertiesConfig.prop.getProperty("invalidtype");
				    response = PropertiesConfig.createResponse(statuscode, statusmessage);
					return response;
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			String statuscode = PropertiesConfig.prop.getProperty("errorcode");
		    String statusmessage = PropertiesConfig.prop.getProperty("updatetableerror");
		    response = PropertiesConfig.createResponse(statuscode, statusmessage);
			return response;
		}
	}

	private String processvalidate(String data)
	{
		String response = "";
		
		JSONObject json = (JSONObject) JSONSerializer.toJSON(data);
		String email = json.getString("email");
		String password = json.getString("password");
		String emailId = "";
		String passwordId = "";
		if(PropertiesConfig.isEmptyOrNull(email)==true)
		{
			String statuscode = PropertiesConfig.prop.getProperty("errorcode");
		    String statusmessage = PropertiesConfig.prop.getProperty("missingemailid");
		    response = PropertiesConfig.createResponse(statuscode, statusmessage);
			return response;
		}
		else
		{
			emailId = email.trim();
		}
		
		if(PropertiesConfig.isEmptyOrNull(password)==true)
		{
			String statuscode = PropertiesConfig.prop.getProperty("errorcode");
		    String statusmessage = PropertiesConfig.prop.getProperty("missingpassword");
		    response = PropertiesConfig.createResponse(statuscode, statusmessage);
			return response;
		}
		else
		{
			passwordId = password.trim();
		}
		String checkAvailability = DBOperation.connectDBandCheck(emailId, passwordId);
		response = checkAvailability;
		System.out.println("Response is"+response.length());
		return response;
		
	}
	
	

}
