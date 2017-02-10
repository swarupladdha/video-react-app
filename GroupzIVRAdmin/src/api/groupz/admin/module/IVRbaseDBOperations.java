package api.groupz.admin.module;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import api.groupz.admin.config.ConnectionManager;
import api.groupz.admin.config.IVRAdminConfig;
import api.groupz.admin.config.IVRbaseAdminConfig;
import api.groupz.database.DBConnect;


public class IVRbaseDBOperations
{
	
      static final String insertSQL = "INSERT INTO ivrgroupzbase (ivrnumber, grpzWelcomeNotes, audioGrpzWelcomeUrl, selectionHangupNotes, audioSelectionHangupUrl, selectionEndNotes, selectionEndUrl, errorNotes, audioerrorUrl, memberWelcomeNotes, audioMemberWelcomeUrl, notRegGroupzNotes, notRegGroupzUrl, maintenanceNotes, maintenanceUrl, generalHangupNotes, generalHangupUrl, numbersUrllist, previousMenuSelectNotes, previousMenuSelectUrl, playspeed, settimeout, multiLanguageFlag, languageSelectionList, languageWelcomeURL, groupzBase) values(";
      static final String updateSQL = "UPDATE ivrgroupzbase SET ";
      static final String selectSQL = "SELECT * FROM ivrgroupzbase where ivrnumber = ";
	
	public static String connectDBandInsert(String ivrData)
	{
		Connection conn = null;
		Statement stmt = null;
		String response = "";
	   try
	   {
	    // conn = DBConnect.establishConnection();
		   conn=ConnectionManager.getConnect();

	      System.out.println("Database connected successfully...");
	      stmt = conn.createStatement();
	      
	      System.out.println("Records inserting into the table...");
	      
	      String field_value = null;
	      String columnValues = "";
	      String[] columnNames = {"ivrnumber", "grpzWelcomeNotes", "audioGrpzWelcomeUrl", "selectionHangupNotes", "audioSelectionHangupUrl", "selectionEndNotes", "selectionEndUrl", "errorNotes", "audioerrorUrl", "memberWelcomeNotes", "audioMemberWelcomeUrl", "notRegGroupzNotes", "notRegGroupzUrl", "maintenanceNotes", "maintenanceUrl", "generalHangupNotes", "generalHangupUrl", "numbersUrllist", "previousMenuSelectNotes", "previousMenuSelectUrl", "playspeed", "settimeout", "multiLanguageFlag", "languageSelectionList", "languageWelcomeURL", "groupzBase"};
	      
	      JSONObject json = (JSONObject) JSONSerializer.toJSON(ivrData);
	     
	      for(int i=0; i<columnNames.length; i++)
	      {
	    	  field_value = json.getString(columnNames[i]);
	    	  String value = field_value.trim();
	    	  
	    	  
	    	  String key = columnNames[i];
	    	  System.out.println("key : " + key);

	    	  if ((key.equalsIgnoreCase("grpzWelcomeNotes")) ||(key.equalsIgnoreCase("selectionHangupNotes")) || (key.equalsIgnoreCase("selectionEndNotes")) || (key.equalsIgnoreCase("memberWelcomeNotes")) || (key.equalsIgnoreCase("notRegGroupzNotes")) || (key.equalsIgnoreCase("generalHangupNotes")) || (key.equalsIgnoreCase("previousMenuSelectNotes")) || (key.equalsIgnoreCase("errorNotes")))
				{						
					JSONObject jReq = json.getJSONObject(key) ;
					String dataList_value = jReq.getString("dataList");
					System.out.println("dataList_value : "+dataList_value);
					if(IVRbaseAdminConfig.isEmptyOrNull(dataList_value.trim()) == false)
				    {
				    	System.out.println("value: -> "+value);
				    	columnValues += "'" + value + "'" + ",";
				    	System.out.println("columnValues : "+ columnValues);
				    }
					else
					{
						String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
						String statusmessage = IVRbaseAdminConfig.prop.getProperty("missingmandatoryfieldvalue") +" "+ key + " ie it is set to null value";
					    response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
						return response;
					}
				}
				
	    	    else if (key.equalsIgnoreCase("numbersUrlList"))
				{
					JSONObject jReq = json.getJSONObject(key) ;
					String urlList_value = jReq.getString("urlList");
					System.out.println("urlList_value : "+urlList_value);
					if(IVRbaseAdminConfig.isEmptyOrNull(urlList_value.trim()) == false)
				    {
				    	 System.out.println("value: -> "+value);
				    	 columnValues += "'" + value + "'" + ",";
				    	 System.out.println("columnValues : "+ columnValues);
				    }
					else
					{
						String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
						String statusmessage = IVRbaseAdminConfig.prop.getProperty("missingmandatoryfieldvalue") +" "+ key + " ie it is set to null value";
					    response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
						return response;
					}
				}
	    	  
	    	    else if ((key.equalsIgnoreCase("audioSelectionHangupUrl")) || (key.equalsIgnoreCase("audiomemberWelcomeUrl")) || (key.equalsIgnoreCase("maintenanceNotes")) || (key.equalsIgnoreCase("previousMenuSelectUrl")))
	    	    {
	    	    	JSONObject jReq = json.getJSONObject(key) ;
					String dataList_value = jReq.getString("dataList");
					System.out.println("dataList_value : "+dataList_value);
					
	    	    	if(IVRbaseAdminConfig.isEmptyOrNull(dataList_value.trim()) == false)
					 { 
	    	    		 System.out.println("value: -> "+value);
				    	 columnValues += "'" + value + "'" + ",";
				    	 System.out.println("columnValues : "+ columnValues);
					 }
					 else
					 {
						 value = "NULL"; 
				   		 columnValues += value + "," ;
				   		 System.out.println("columnValues : "+ columnValues);
				   	 }
	    	    }
	    	    else if (key.equalsIgnoreCase("maintenanceUrl"))
	    	    {
	    	    	JSONObject jReq = json.getJSONObject(key) ;
					String urlList_value = jReq.getString("urlList");
					System.out.println("urlList_value : "+urlList_value);
					
					if(IVRbaseAdminConfig.isEmptyOrNull(urlList_value.trim()) == false)
					{ 
	    	    		System.out.println("value: -> "+value);
				    	columnValues += "'" + value + "'" + ",";
				    	System.out.println("columnValues : "+ columnValues);
					}
					else
					{
						value = "NULL"; 
				   		columnValues += value + "," ;
				   		System.out.println("columnValues : "+ columnValues);
				   	}
	    	    }
//	    	    else if ((key.equalsIgnoreCase("languageSelectionList")))
//	    	    {
//	    	    	JSONObject jReq = json.getJSONObject(key) ;
//					String selectionList_value = jReq.getString("selectionList");
//					System.out.println("selectionList_value : "+selectionList_value);
//					
//					if(IVRbaseAdminConfig.isEmptyOrNull(selectionList_value.trim()) == false)
//					{ 
//	    	    		System.out.println("value: -> "+value);
//				    	columnValues += "'" + value + "'" + ",";
//				    	System.out.println("columnValues : "+ columnValues);
//					}
//					else
//					{
//						value = "NULL"; 
//				   		columnValues += value + "," ;
//				   		System.out.println("columnValues : "+ columnValues);
//				   	}
//	    	    }
	    	  
	    	    else
		    	{
		    	    if(value!=null && value.length()>0 && value.equalsIgnoreCase("")==false)
					{
						System.out.println("value: -> "+value);
						columnValues += "'" + value + "'" + ",";
						System.out.println("columnValues : "+ columnValues);
					}
					else
					{
						value = "NULL"; 
			   		  	columnValues += value + "," ;
			   		 System.out.println("columnValues : "+ columnValues);
			   		
			   	  	}
		        }
	      }
	      if(columnValues.endsWith(",")== true)
	      {
	    	  columnValues = columnValues.substring(0, columnValues.length()-1);
	      }
	      String sql = columnValues + ");";
	      String sql_query = insertSQL+sql;    
	     
	      System.out.println("The Insert SQL : " + sql_query) ;
	     
	      stmt.execute(sql_query);
	     
	      
	      System.out.println("Records inserted into the table...");
	      String statuscode = IVRbaseAdminConfig.prop.getProperty("successcode");
	      String statusmessage = "Successfully Inserted";
	      response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
	     
	      return response;
	   }
	   catch(SQLException e)
	   {
		   System.out.println(e);
	      //Handle errors for Class.forName
		   StringWriter errors = new StringWriter();
		   e.printStackTrace(new PrintWriter(errors));
		   String errormessage = errors.toString();
		   String word = "Duplicate key";
		   boolean duplicate_key = IVRbaseAdminConfig.duplicateEntry(word, errormessage);
		   if(duplicate_key == true)
		   {
			   String statuscode = IVRbaseAdminConfig.prop.getProperty("duplicatekeyerrorcode");
			   String statusmessage = "Duplicate entry";
			   response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
			   System.out.println("duplicatekeyerror "+ response);
			   return response;
		   }
		   else
		   {
			   String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
			   String statusmessage = IVRbaseAdminConfig.prop.getProperty("adderrornotes");
			   response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
			   System.out.println("errorCode no duplicate key found "+response);
			   return response;
		   }
	   }
	   finally
	   {
	      try
	      {
	         if(stmt!=null)
	        	 stmt.close();
	        	 conn.close();
	      }
	      catch(Exception e)
	      {
	    	  String statusmessage = IVRbaseAdminConfig.prop.getProperty("dberrordisconnection");
	    	  String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
			  response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
			  System.out.println("DBerrorDisconnection : "+ response);
			  return response;
	      }
	      try
	      {
	         if(conn!=null)
	        	 DBConnect.closeConnection(conn);
	      }
	      catch(Exception e)
	      {
	         e.printStackTrace();
	         String statusmessage = IVRbaseAdminConfig.prop.getProperty("dberrordisconnection");
	    	 String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
			 response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
			 System.out.println("DBerrorDisconnection : "+ response);
			 return response;
	      }
	   }
	}
	
	public static String connectDBandGet(String ivrData)
	{
		Connection conn = null;
		Statement stmt = null;
		String response = "";
		
		JSONObject json = (JSONObject) JSONSerializer.toJSON(ivrData);
		String ivrnumber = json.getString("ivrnumber");
		String groupzBase = json.getString("groupzBase");
		System.out.println(ivrnumber);
		System.out.println(groupzBase);
	   
		try
	    {
			JSONObject dataObj = new JSONObject();
			JSONArray jarray = new JSONArray();
			
			conn = ConnectionManager.getConnect();

			System.out.println("Database connected successfully...");
			stmt = conn.createStatement();
	      
//			System.out.println("Records inserting into the table...");
	      
//		      String field_value = null;
//		      String columnValues = "";
//		      String[] columnNames = {"ivrnumber", "grpzWelcomeNotes", "audioGrpzWelcomeUrl", "selectionHangupNotes", "audioSelectionHangupUrl", "selectionEndNotes", "selectionEndUrl", "errorNotes", "audioerrorUrl", "memberWelcomeNotes", "audioMemberWelcomeUrl", "notRegGroupzNotes", "notRegGroupzUrl", "maintenanceNotes", "maintenanceUrl", "generalHangupNotes", "generalHangupUrl", "numbersUrllist", "previousMenuSelectNotes", "previousMenuSelectUrl", "playspeed", "settimeout", "multiLanguageFlag", "languageSelectionList", "languageWelcomeURL", "groupzBase"};
//		      
//		      JSONObject json = (JSONObject) JSONSerializer.toJSON(ivrData);
		      
//		      Iterator jsonObj = json.keys();
//			  List<String> keysList = new ArrayList<String>();
//			  
//			  while(jsonObj.hasNext())
//			  {
//				  String ivrDatakeys = (String) jsonObj.next();
//				  keysList.add(ivrDatakeys);
//			  }
//			  String[] ivrData_keys = keysList.toArray(new String[keysList.size()]);
//			  System.out.println("ivrData_keys.length : "+ivrData_keys.length);
//
//		      for(int i=0; i<ivrData_keys.length; i++)
//			  {
//			      if (Arrays.asList(columnNames).contains(ivrData_keys[i]))
//			      {
//			    	  System.out.println("ivrData_keys[i] : "+ivrData_keys[i]);
//					  field_value = json.getString(ivrData_keys[i]);
//				      String value = field_value.trim();
//		
//			    	  if(value!=null && value.length()>0 && value.equalsIgnoreCase("")==false)
//			    	  {
//			    	      columnValues = "'" + value + "'";
//			    	  }
//			    	  else
//			    	  {
//			    	      value = "NULL";
//				   		  columnValues = value;
//				   	  }  
//						
//			    	  String sql = ivrData_keys[i] + "=" + columnValues + ";";
//					  System.out.println("sql : "+sql);
					  System.out.println("---------------------------------------------------------");
					  String sql_query = selectSQL +ivrnumber+" and groupzBase ='"+groupzBase+"';";      
					  System.out.println(sql_query) ;
					  ResultSet rs = stmt.executeQuery(sql_query);
					  
					  if(rs!=null)
					  {
						 while (rs.next())
						 {
							 dataObj.put("ivrnumber",rs.getString("ivrnumber"));
							 dataObj.put("grpzWelcomeNotes",rs.getString("grpzWelcomeNotes"));
							 dataObj.put("audioGrpzWelcomeUrl",rs.getString("audioGrpzWelcomeUrl"));
							 dataObj.put("selectionHangupNotes",rs.getString("selectionHangupNotes"));
							 dataObj.put("audioSelectionHangupUrl",rs.getString("audioSelectionHangupUrl"));
							 dataObj.put("selectionEndNotes",rs.getString("selectionEndNotes"));
							 dataObj.put("selectionEndUrl",rs.getString("selectionEndUrl"));
							 dataObj.put("errorNotes", rs.getString("errorNotes"));
							 dataObj.put("audioerrorUrl",rs.getString("audioerrorUrl"));
							 dataObj.put("memberWelcomeNotes",rs.getString("memberWelcomeNotes"));
							 dataObj.put("audiomemberWelcomeUrl",rs.getString("audiomemberWelcomeUrl"));
							 dataObj.put("notRegGroupzNotes",rs.getString("notRegGroupzNotes"));
							 dataObj.put("notRegGroupzUrl",rs.getString("notRegGroupzUrl"));
							 dataObj.put("maintenanceNotes",rs.getString("maintenanceNotes"));
							 dataObj.put("maintenanceUrl",rs.getString("maintenanceUrl"));
							 dataObj.put("generalHangupNotes",rs.getString("generalHangupNotes"));
							 dataObj.put("generalHangupUrl",rs.getString("generalHangupUrl"));
							 dataObj.put("numbersUrlList",rs.getString("numbersUrlList"));
							 dataObj.put("previousMenuSelectNotes",rs.getString("previousMenuSelectNotes"));
							 dataObj.put("previousMenuSelectUrl",rs.getString("previousMenuSelectUrl"));
							 dataObj.put("playspeed",rs.getInt("playspeed"));
							 dataObj.put("settimeout",rs.getInt("settimeout"));
							 dataObj.put("multilanguageFlag",rs.getInt("multilanguageFlag"));
							 dataObj.put("languageSelectionList",rs.getString("languageSelectionList"));
							 dataObj.put("languageWelcomeURL",rs.getString("languageWelcomeURL"));
							 dataObj.put("groupzBase",rs.getString("groupzBase"));
							 jarray.add(dataObj);
						 }
					  }
					  
					  if (jarray != null && jarray.size()>0)
					  {
						  System.out.println("Records selected from the table...");
					      String statuscode = IVRbaseAdminConfig.prop.getProperty("successcode");
					      String statusmessage = "Success";
					      response = IVRbaseAdminConfig.createResponse1(statuscode, statusmessage, jarray);
					      return response;
					  }
					  else
					  {
						  String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
						  String statusmessage = IVRbaseAdminConfig.prop.getProperty("getdataerrornotes");
						  response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);  
						  return response;
					  }
		     } 
			 catch(Exception e)
		     {
			     //Handle errors for Class.forName
			     e.printStackTrace();
			     String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
				 String statusmessage = IVRbaseAdminConfig.prop.getProperty("getdataerrornotes");
				 response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);  
			     return response;
		      }
		   finally
		   {
		      try
		      {
		         if(stmt!=null)
		        	 DBConnect.closeConnection(conn);
		      }
		      catch(Exception e)
		      {
		    	  String statusmessage = IVRbaseAdminConfig.prop.getProperty("dberrordisconnection");
		    	  String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
				  response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
				  return response;
		      }
		      try
		      {
		         if(conn!=null)
		        	 DBConnect.closeConnection(conn);
		      }
		      catch(Exception e)
		      {
		         e.printStackTrace();
		         String statusmessage = IVRbaseAdminConfig.prop.getProperty("dberrordisconnection");
		    	 String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
				 response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
				 return response;
		      }
		   }
	}

	public static String connectDBandUpdate(String ivrData)
	{
		Connection conn = null;
		Statement stmt = null;
		String response = "";
	   
		try
	    {
			JSONObject dataObj = new JSONObject();
			JSONArray jarray = new JSONArray();
			
			conn = ConnectionManager.getConnect();

			System.out.println("Database connected successfully...");
			stmt = conn.createStatement();
	      
			System.out.println("Records inserting into the table...");
	      
		    String field_value = null;
		    String columnValues = "";
		    String[] columnNames = {"ivrnumber", "grpzWelcomeNotes", "audioGrpzWelcomeUrl", "selectionHangupNotes", "audioSelectionHangupUrl", "selectionEndNotes", "selectionEndUrl", "errorNotes", "audioerrorUrl", "memberWelcomeNotes", "audioMemberWelcomeUrl", "notRegGroupzNotes", "notRegGroupzUrl", "maintenanceNotes", "maintenanceUrl", "generalHangupNotes", "generalHangupUrl", "numbersUrllist", "previousMenuSelectNotes", "previousMenuSelectUrl", "playspeed", "settimeout", "multiLanguageFlag", "languageSelectionList", "languageWelcomeURL", "groupzBase"};
		      
		    JSONObject json = (JSONObject) JSONSerializer.toJSON(ivrData);
		      
		    Iterator jsonObj = json.keys();
			List<String> keysList = new ArrayList<String>();
			  
			while(jsonObj.hasNext())
			{
				String ivrDatakeys = (String) jsonObj.next();
				keysList.add(ivrDatakeys);
			}
			String[] ivrData_keys = keysList.toArray(new String[keysList.size()]);
				
			ResultSet rs = null;
			
		    for(int i=0; i<ivrData_keys.length; i++)
			{
			   if (Arrays.asList(columnNames).contains(ivrData_keys[i]))
			   {
				   field_value = json.getString(ivrData_keys[i]);
				   String value = field_value.trim();
		
				   String key = ivrData_keys[i];

				   if ((key.equalsIgnoreCase("grpzWelcomeNotes")) ||(key.equalsIgnoreCase("selectionHangupNotes")) || (key.equalsIgnoreCase("selectionEndNotes")) || (key.equalsIgnoreCase("memberWelcomeNotes")) || (key.equalsIgnoreCase("notRegGroupzNotes")) || (key.equalsIgnoreCase("generalHangupNotes")) || (key.equalsIgnoreCase("previousMenuSelectNotes")) || (key.equalsIgnoreCase("errorNotes")))
				   {						
						JSONObject jReq = json.getJSONObject(key) ;
						System.out.println("jReq : "+ jReq);
						String dataList_value = jReq.getString("dataList");
						System.out.println("dataList_value : "+dataList_value);
						if(IVRbaseAdminConfig.isEmptyOrNull(dataList_value.trim()) == false)
					    {
					    	System.out.println("value: -> "+value);
					    	columnValues = "'" + value + "'" + ",";
					    	System.out.println("columnValues : "+ columnValues);
					    }
						else
						{
							String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
							String statusmessage = IVRbaseAdminConfig.prop.getProperty("missingmandatoryfieldvalue") +" "+ key + " ie it is set to null value";
						    response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
							return response;
						} 
					}
					
		    	    else if (key.equalsIgnoreCase("numbersUrlList"))
					{
						JSONObject jReq = json.getJSONObject(key) ;
						String urlList_value = jReq.getString("urlList");
						System.out.println("urlList_value : "+urlList_value);
						if(IVRbaseAdminConfig.isEmptyOrNull(urlList_value.trim()) == false)
					    {
					    	 System.out.println("value: -> "+value);
					    	 columnValues = "'" + value + "'" + ",";
					    	 System.out.println("columnValues : "+ columnValues);
					    }
						else
						{
							String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
							String statusmessage = IVRbaseAdminConfig.prop.getProperty("missingmandatoryfieldvalue") +" "+ key + " ie it is set to null value";
						    response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
							return response;
						}
					}
		    	  
		    	    else if ((key.equalsIgnoreCase("audioSelectionHangupUrl")) || (key.equalsIgnoreCase("audiomemberWelcomeUrl")) || (key.equalsIgnoreCase("maintenanceNotes")) || (key.equalsIgnoreCase("previousMenuSelectUrl")))
		    	    {
		    	    	JSONObject jReq = json.getJSONObject(key) ;
						String dataList_value = jReq.getString("dataList");
						System.out.println("dataList_value : "+dataList_value);
						
						if(IVRbaseAdminConfig.isEmptyOrNull(dataList_value.trim()) == false)
						{ 
							System.out.println("value: -> "+value);
					    	columnValues = "'" + value + "'" + ",";
					    	System.out.println("columnValues : "+ columnValues);
						 }
						 else
						 {
							 value = "NULL"; 
					   		 columnValues = value + "," ;
					   		 System.out.println("columnValues : "+ columnValues);
					   	 }
		    	    }
		    	    else if (key.equalsIgnoreCase("maintenanceUrl"))
//			    	    	 || (key.equalsIgnoreCase("languageWelcomeURL")))
		    	    {
		    	    	JSONObject jReq = json.getJSONObject(key) ;
						String urlList_value = jReq.getString("urlList");
						System.out.println("urlList_value : "+urlList_value);
						
						if(IVRbaseAdminConfig.isEmptyOrNull(urlList_value.trim()) == false)
						{ 
							System.out.println("value: -> "+value);
					    	columnValues = "'" + value + "'" + ",";
					    	System.out.println("columnValues : "+ columnValues);
						}
						else
						{
							value = "NULL"; 
					   		columnValues = value + "," ;
					   		System.out.println("columnValues : "+ columnValues);
					   	}
		    	    }
//			    	    else if ((key.equalsIgnoreCase("languageSelectionList")))
//			    	    {
//			    	    	JSONObject jReq = json.getJSONObject(key) ;
//							String selectionList_value = jReq.getString("selectionList");
//							System.out.println("selectionList_value : "+selectionList_value);
//							
//			    	    	if(IVRbaseAdminConfig.isEmptyOrNull(selectionList_value.trim()) == true)
//							 { 
//								 value = "NULL"; 
//						   		 columnValues = value + "," ;
//						   		 System.out.println("columnValues : "+ columnValues);
//							 }
//							 else
//							 {
//								 System.out.println("value: -> "+value);
//						    	   columnValues = "'" + value + "'" + ",";
//						    	   System.out.println("columnValues : "+ columnValues);
//						   	 }
//			    	    }
		    	  
		    	    else
			    	{
			    	    if(value!=null && value.length()>0 && value.equalsIgnoreCase("")==false)
						{
							System.out.println("value: -> "+value);
							columnValues = "'" + value + "'" + ",";
							System.out.println("columnValues : "+ columnValues);
						}
						else
						{
							value = "NULL"; 
				   		  	columnValues = value + "," ;
				   		    System.out.println("columnValues : "+ columnValues);
				   	  	}
			        }
		        }
		        if(columnValues.endsWith(",")== true)
		        {
		    	    columnValues = columnValues.substring(0, columnValues.length()-1);
		        }
		      
		        String sql="";
		      
		        String ivrNumber = json.getString("ivrnumber");
			    String ivrnumber = ivrNumber.trim();
				
		        if ((ivrData_keys[i].equalsIgnoreCase("ivrnumber")==false) && (ivrData_keys[i].equalsIgnoreCase("scope") == false) && (ivrData_keys[i].equalsIgnoreCase("type") == false))
		        {
		    	    sql = ivrData_keys[i] + "=" + columnValues;
				    System.out.println("sql : "+sql);
				    String sql_query = updateSQL + sql + " WHERE ivrnumber='" +ivrnumber+ "';";
				    System.out.println("The update SQL : " + sql_query) ;
				    stmt.executeUpdate(sql_query); 
				    String ret_list_Qry = selectSQL +" WHERE ivrnumber='"+ivrnumber+"';";
				    rs = stmt.executeQuery(ret_list_Qry);
			    }
		    }
		    if(rs!=null)
		    {
			   while (rs.next())
			   {
				   dataObj.put("ivrnumber",rs.getString("ivrnumber"));
				   dataObj.put("grpzWelcomeNotes",rs.getString("grpzWelcomeNotes"));
				   dataObj.put("audioGrpzWelcomeUrl",rs.getString("audioGrpzWelcomeUrl"));
				   dataObj.put("selectionHangupNotes",rs.getString("selectionHangupNotes"));
				   dataObj.put("audioSelectionHangupUrl",rs.getString("audioSelectionHangupUrl"));
				   dataObj.put("selectionEndNotes",rs.getString("selectionEndNotes"));
				   dataObj.put("selectionEndUrl",rs.getString("selectionEndUrl"));
				   dataObj.put("errorNotes", rs.getString("errorNotes"));
				   dataObj.put("audioerrorUrl",rs.getString("audioerrorUrl"));
				   dataObj.put("memberWelcomeNotes",rs.getString("memberWelcomeNotes"));
				   dataObj.put("audiomemberWelcomeUrl",rs.getString("audiomemberWelcomeUrl"));
				   dataObj.put("notRegGroupzNotes",rs.getString("notRegGroupzNotes"));
				   dataObj.put("notRegGroupzUrl",rs.getString("notRegGroupzUrl"));
				   dataObj.put("maintenanceNotes",rs.getString("maintenanceNotes"));
				   dataObj.put("maintenanceUrl",rs.getString("maintenanceUrl"));
				   dataObj.put("generalHangupNotes",rs.getString("generalHangupNotes"));
				   dataObj.put("generalHangupUrl",rs.getString("generalHangupUrl"));
				   dataObj.put("numbersUrlList",rs.getString("numbersUrlList"));
				   dataObj.put("previousMenuSelectNotes",rs.getString("previousMenuSelectNotes"));
				   dataObj.put("previousMenuSelectUrl",rs.getString("previousMenuSelectUrl"));
				   dataObj.put("playspeed",rs.getInt("playspeed"));
				   dataObj.put("settimeout",rs.getInt("settimeout"));
				   dataObj.put("multilanguageFlag",rs.getString("multilanguageFlag"));
//				   dataObj.put("languageSelectionList",rs.getString("languageSelectionList"));
//				   dataObj.put("languageWelcomeURL",rs.getString("languageWelcomeURL"));
				   dataObj.put("groupzBase",rs.getString("groupzBase"));
				   jarray.add(dataObj);
			   }
			   System.out.println("jarray : "+jarray);
		    } 
	        System.out.println("Records updated in the table...");
	        String statuscode = IVRAdminConfig.prop.getProperty("successcode");
	        String statusmessage = "Successfully Updated";
	        response = IVRAdminConfig.createResponse1(statuscode, statusmessage,jarray);
	        return response;
		}
		catch(Exception e)
	     {
		     //Handle errors for Class.forName
		     e.printStackTrace();
		     String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
			 String statusmessage = IVRbaseAdminConfig.prop.getProperty("updateerrornotes");
			 response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);  
		     return response;
	     }
		 finally
		 {
		    try
		    {
		       if(stmt!=null)
		         stmt.close();
		       	 conn.close();
		    }
		    catch(Exception e)
		    {
		        String statusmessage = IVRbaseAdminConfig.prop.getProperty("dberrordisconnection");
		    	String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
				response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
				return response;
		    }
		    try
		    {
		       if(conn!=null)
		    	   DBConnect.closeConnection(conn);
		    }
		    catch(Exception e)
		    {
		        e.printStackTrace();
		        String statusmessage = IVRbaseAdminConfig.prop.getProperty("dberrordisconnection");
		    	String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
				response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
				return response;
		    }
	   }	
	}
}