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


public class IVRDBOperations
{
	
      static final String insertSQL = "INSERT INTO ivrgroupz (ivrnumber, groupzCode,groupzBase, welcomeNotes, audioWelcomeUrl, selectionlist, selectionlistUrl, groupzNameUrl, multiLanguageFlag, recmultilanguageSelectionList, recmultilanguageSelectionWelcomeURL, endDate, address)values(";
      static final String updateSQL = "UPDATE ivrgroupz SET ";
      static final String selectSQL = "SELECT * FROM ivrgroupz where ivrnumber = ";
	
	public static String connectDBandInsert(String ivrData)
	{
		Connection conn = null;
		Statement stmt = null;
		String response = "";
	   try
	   {
	     conn = ConnectionManager.getConnect();

	      System.out.println("Database connected successfully...");
	      stmt = conn.createStatement();
	      
	      System.out.println("Records inserting into the table...");
	      
	      String field_value = null;
	      String columnValues = "";
	      String[] columnNames = {"ivrnumber", "groupzCode" ,"groupzBase", "welcomeNotes", "audioWelcomeUrl", "selectionlist", "selectionlistUrl", "groupzNameUrl", "multiLanguageFlag", "recmultilanguageSelectionList", "recmultilanguageSelectionWelcomeURL", "endDate", "address"};
	      
	      JSONObject json = (JSONObject) JSONSerializer.toJSON(ivrData);
	      System.out.println(columnNames.length+"");
	      System.out.println(columnNames[2]);
	      
	      for(int i=0; i<columnNames.length; i++)
	      {
	    	  field_value = json.getString(columnNames[i]);
	    	  String value = field_value.trim();

	    	  
	    	  String key = columnNames[i];
	    	  
	    	  if (key.equalsIgnoreCase("welcomeNotes"))
			  {	
				 JSONObject jReq = json.getJSONObject(key) ;
				 String welcomeNotes_value = jReq.getString("welcomenotesList");
				 System.out.println("welcomeNotes_value : "+welcomeNotes_value);
				 System.out.println("==============================");
					
				 if (IVRAdminConfig.isEmptyOrNull(welcomeNotes_value.trim()) == false)
			      {
			    	   System.out.println("value: -> "+value);
			    	   columnValues += "'" + value + "'" + ",";
			    	   System.out.println("columnValues : "+ columnValues);
			      }
				  else
				  {
					  String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
					  String statusmessage = IVRAdminConfig.prop.getProperty("missingmandatoryfieldvalue") +" "+ key + " ie it is set to null value";
				      response = IVRAdminConfig.createResponse(statuscode, statusmessage);
					  return response;
				  }
			  }
				
	    	  else if (key.equalsIgnoreCase("selectionlist"))
			  {	
				  JSONObject jReq = json.getJSONObject(key) ;
				  String selectionList_value = jReq.getString("selectionList");
				  System.out.println("selectionList_value : "+selectionList_value);
				
				  if (IVRAdminConfig.isEmptyOrNull(selectionList_value.trim()) == false)
			      {
			    	 System.out.println("value: -> "+value);
			    	 columnValues += "'" + value + "'" + ",";
			    	 System.out.println("columnValues : "+ columnValues);
			      }
				  else
				  {
					  String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
					  String statusmessage = IVRAdminConfig.prop.getProperty("missingmandatoryfieldvalue") +" "+ key + " ie it is set to null value";
				      response = IVRAdminConfig.createResponse(statuscode, statusmessage);
					  return response;
				  }
			 }
	    	  
//	    	  else if (key.equalsIgnoreCase("recmultilanguageSelectionList"))
//			  {	 
//				 JSONObject jReq = json.getJSONObject(key) ;
//				 String selectionList_value = jReq.getString("selectionList");
//				 System.out.println("selectionList_value : "+selectionList_value);
//				 
//				 if (IVRAdminConfig.isEmptyOrNull(selectionList_value.trim()) == true)
//				 { 
//					 value = "NULL"; 
//			   		 columnValues += value + "," ;
//			   		 System.out.println("columnValues : "+ columnValues);
//				 }
//				 else
//				 {
//					 System.out.println("value: -> "+value);
//			    	   columnValues += "'" + value + "'" + ",";
//			    	   System.out.println("columnValues : "+ columnValues);
//				 }
//			  }
//	    	  
//	    	  else if (key.equalsIgnoreCase("recmultilanguageSelectionWelcomeURL"))
//			  {
//				 JSONObject jReq = json.getJSONObject(key) ;
//				 String selectionURL_value = jReq.getString("urlList");
//				 System.out.println("selectionURL_value : "+selectionURL_value);
//				 
//				 if (IVRAdminConfig.isEmptyOrNull(selectionList_value.trim()) == true)
//				 { 
//					 value = "NULL"; 
//			   		 columnValues += value + "," ;
//			   		 System.out.println("columnValues : "+ columnValues);
//				 }
//				 else
//				 {
//					 System.out.println("value: -> "+value);
//			    	   columnValues += "'" + value + "'" + ",";
//			    	   System.out.println("columnValues : "+ columnValues);
//			   	 }
//			 }
	    	  
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
		       String statuscode = IVRAdminConfig.prop.getProperty("successcode");
		       String statusmessage = "Successfully Inserted";
		       response = IVRAdminConfig.createResponse(statuscode, statusmessage);
		       return response;
	   }
	   catch(SQLException e)
	   {
	      //Handle errors for Class.forName
		   StringWriter errors = new StringWriter();
		   e.printStackTrace(new PrintWriter(errors));
		   String errormessage = errors.toString();
		   String word = "Duplicate key";
		   boolean duplicate_key = IVRAdminConfig.duplicateEntry(word, errormessage);
		   if(duplicate_key == true)
		   {
			   String statuscode = IVRAdminConfig.prop.getProperty("duplicatekeyerrorcode");
			   String statusmessage = "Duplicate entry";
			   response = IVRAdminConfig.createResponse(statuscode, statusmessage);
			   System.out.println("duplicatekeyerror "+ response);
			   return response;
		   }
		   else
		   {
			   String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
			   String statusmessage = IVRAdminConfig.prop.getProperty("adderrornotes");
			   response = IVRAdminConfig.createResponse(statuscode, statusmessage);
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
	    	  String statusmessage = IVRAdminConfig.prop.getProperty("dberrordisconnection");
	    	  String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
			  response = IVRAdminConfig.createResponse(statuscode, statusmessage);
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
	         String statusmessage = IVRAdminConfig.prop.getProperty("dberrordisconnection");
	    	 String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
			 response = IVRAdminConfig.createResponse(statuscode, statusmessage);
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
	      
			//System.out.println("Records submitted into the table...");
	      
//		      String field_value = null;
//		      String columnValues = "";
//		      String[] columnNames = {"ivrnumber", "groupzCode", "welcomeNotes", "audioWelcomeUrl", "selectionlist", "selectionlistUrl", "groupzNameUrl", "multiLanguageFlag", "recmultilanguageSelectionList", "recmultilanguageSelectionWelcomeURL", "endDate", "address"};
//		      
//		      JSONObject json = (JSONObject) JSONSerializer.toJSON(ivrData);
		      
//		      if((json != null) &&(json.size()>0))
//		      {
//		      
//			      Iterator jsonObj = json.keys();
//				  List<String> keysList = new ArrayList<String>();
//				  
//				  while(jsonObj.hasNext())
//				  {
//					  String ivrDatakeys = (String) jsonObj.next();
//					  keysList.add(ivrDatakeys);
//					  System.out.println("keyList : "+ keysList);
//				  }
//				  String[] ivrData_keys = keysList.toArray(new String[keysList.size()]);
//				  System.out.println("ivrData_keys.length : "+ivrData_keys.length);
//	
//			      for(int i=0; i<ivrData_keys.length; i++)
//				  {
//			    	  if (Arrays.asList(columnNames).contains(ivrData_keys[i]))
//				      {
//				    	  System.out.println("ivrData_keys[i] : "+ivrData_keys[i]);
//						  field_value = json.getString(ivrData_keys[i]);
//					      String value = field_value.trim();
//			
//					      if((ivrData_keys[i].equalsIgnoreCase("ivrnumber")==false))
//					      {
//					    	  if(ivrData_keys[i].equalsIgnoreCase("groupzCode")==false)
//					    	  {
//					    	    if(value!=null && value.length()>0 && value.equalsIgnoreCase("")==false)
//								{
//									System.out.println("value: -> "+value);
//									columnValues += "'" + value + "'" + ",";
//									System.out.println("columnValues : "+ columnValues);
//								}
//								else
//								{
//									value = "NULL"; 
//						   		  	columnValues += value + "," ;
//						   		 System.out.println("columnValues : "+ columnValues);
//						   	  	}
//						      }  
//					      }
//				    	    if(columnValues.endsWith(",")== true)
//				  	      {
//				  	    	  columnValues = columnValues.substring(0, columnValues.length()-1);
//				  	      }
				    	  					
//						  String ivrNumber = json.getString("ivrnumber");
//						  String ivrnumber = ivrNumber.trim();
//						  String grpzcode = json.getString("groupzCode");
//						  String groupzcode = grpzcode.trim();
						  
//						  System.out.println("Column names:"+ivrData_keys[i]);
						 
//						  if((ivrData_keys[i].equalsIgnoreCase("ivrnumber")==false) && (ivrData_keys[i].equalsIgnoreCase("groupzCode")==false) && (ivrData_keys[i].equalsIgnoreCase("scope") == false) && (ivrData_keys[i].equalsIgnoreCase("type") == false))
//						  {
//					    	  String sql = ivrData_keys[i] + "=" + columnValues;				
//							  System.out.println("sql : "+sql);
//							  String sql_query = selectSQL + sql;      
//							  System.out.println("The select SQL : " + sql_query) ;
							 //stmt.execute(sql_query);
							  
							 //String ret_list_Qry= selectSQL + sql;
//							  ResultSet rs = stmt.executeQuery(sql_query);
							  
//							  if(rs!=null)
//							  {
//								 while (rs.next())
//								 {
//									 dataObj.put("ivrnumber",rs.getString("ivrnumber"));
//									 dataObj.put("groupZCode",rs.getString("groupZCode"));
//									 dataObj.put("welcomeNotes",rs.getString("welcomeNotes"));
//									 dataObj.put("audioWelcomeUrl",rs.getString("audioWelcomeUrl"));
//									 dataObj.put("selectionlist",rs.getString("selectionlist"));
//									 dataObj.put("selectionlistUrl",rs.getString("selectionlistUrl"));
//									 dataObj.put("groupzNameUrl",rs.getString("groupzNameUrl"));
//									 dataObj.put("multiLanguageFlag", rs.getInt("multiLanguageFlag"));
//									 dataObj.put("recmultilanguageSelectionList",rs.getString("recmultilanguageSelectionList"));
//									 dataObj.put("recmultilanguageSelectionWelcomeURL",rs.getString("recmultilanguageSelectionWelcomeURL"));
//									 dataObj.put("endDate",rs.getString("endDate"));
//									 dataObj.put("address",rs.getString("address"));
//									 jarray.add(dataObj);
//								 }
//							 }
//					      }
//				  }  
//			  }
//		      else
//	          {
	    		  String sql_query = selectSQL +ivrnumber+" and groupzBase = '"+groupzBase+ "' ;";
	    		  System.out.println("The select SQL : " + sql_query) ;
//				  stmt.execute(sql_query);
					  
//				  String ret_list_Qry= sql_query;
				  ResultSet rs = stmt.executeQuery(sql_query);
					  
					  
				  if(rs!=null)
				  {
					 						  
					 while (rs.next())
					 {
						 dataObj = new JSONObject();
						 dataObj.put("ivrnumber",rs.getString("ivrnumber"));
						 dataObj.put("groupZCode",rs.getString("groupZCode"));
						 dataObj.put("welcomeNotes",rs.getString("welcomeNotes"));
						 dataObj.put("audioWelcomeUrl",rs.getString("audioWelcomeUrl"));
						 dataObj.put("selectionlist",rs.getString("selectionlist"));
						 dataObj.put("selectionlistUrl",rs.getString("selectionlistUrl"));
						 dataObj.put("groupzNameUrl",rs.getString("groupzNameUrl"));
						 dataObj.put("multiLanguageFlag", rs.getInt("multiLanguageFlag"));
						 dataObj.put("recmultilanguageSelectionList",rs.getString("recmultilanguageSelectionList"));
						 dataObj.put("recmultilanguageSelectionWelcomeURL",rs.getString("recmultilanguageSelectionWelcomeURL"));
						 dataObj.put("endDate",rs.getString("endDate"));
						 dataObj.put("address",rs.getString("address"));
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
		     String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
			 String statusmessage = IVRAdminConfig.prop.getProperty("getdataerrornotes");
			 response = IVRAdminConfig.createResponse(statuscode, statusmessage);  
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
		    	  String statusmessage = IVRAdminConfig.prop.getProperty("dberrordisconnection");
		    	  String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
				  response = IVRAdminConfig.createResponse(statuscode, statusmessage);
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
		         String statusmessage = IVRAdminConfig.prop.getProperty("dberrordisconnection");
		    	 String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
				 response = IVRAdminConfig.createResponse(statuscode, statusmessage);
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
	      
			System.out.println("Records submitted into the table...");
	      
		      String field_value = null;
		      String columnValues = "";
		      String[] columnNames = {"ivrnumber", "groupzCode", "welcomeNotes", "audioWelcomeUrl", "selectionlist", "selectionlistUrl", "groupzNameUrl", "multiLanguageFlag", "recmultilanguageSelectionList", "recmultilanguageSelectionWelcomeURL", "endDate", "address"};
		      
		      JSONObject json = (JSONObject) JSONSerializer.toJSON(ivrData);
		      
		      Iterator jsonObj = json.keys();
			  List<String> keysList = new ArrayList<String>();
			  
			  while(jsonObj.hasNext())
			  {
				  String ivrDatakeys = (String) jsonObj.next();
				  keysList.add(ivrDatakeys);
				  System.out.println("keysList : "+keysList);
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
			    	  
				      if (key.equalsIgnoreCase("welcomeNotes"))
					  {	
						 JSONObject jReq = json.getJSONObject(key) ;
						 String welcomeNotes_value = jReq.getString("welcomenotesList");
						 System.out.println("welcomeNotes_value : "+welcomeNotes_value);
							
						 if (IVRAdminConfig.isEmptyOrNull(welcomeNotes_value.trim()) == false)
					      {
					    	   System.out.println("value: -> "+value);
					    	   columnValues = "'" + value + "'" + ",";
					    	   System.out.println("columnValues : "+ columnValues);
					      }
						  else
						  {
							  String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
							  String statusmessage = IVRAdminConfig.prop.getProperty("missingmandatoryfieldvalue") +" "+ key + " ie it is set to null value";
						      response = IVRAdminConfig.createResponse(statuscode, statusmessage);
							  return response;
						  }
					  }
						
			    	  else if (key.equalsIgnoreCase("selectionlist"))
					  {	
						  JSONObject jReq = json.getJSONObject(key) ;
						  String selectionList_value = jReq.getString("selectionList");
						  System.out.println("selectionList_value : "+selectionList_value);
						
						  if (IVRAdminConfig.isEmptyOrNull(selectionList_value.trim()) == false)
					      {
					    	 System.out.println("value: -> "+value);
					    	 columnValues = "'" + value + "'" + ",";
					    	 System.out.println("columnValues : "+ columnValues);
					      }
						  else
						  {
							  String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
							  String statusmessage = IVRAdminConfig.prop.getProperty("missingmandatoryfieldvalue") +" "+ key + " ie it is set to null value";
						      response = IVRAdminConfig.createResponse(statuscode, statusmessage);
							  return response;
						  }
					 }
				      
//				      else if (key.equalsIgnoreCase("recmultilanguageSelectionList"))
//					  {	 
//						 JSONObject jReq = json.getJSONObject(key) ;
//						 String selectionList_value = jReq.getString("selectionList");
//						 System.out.println("selectionList_value : "+selectionList_value);
//						 
//						 if (IVRAdminConfig.isEmptyOrNull(selectionList_value.trim()) == true)
//						 { 
//							 value = "NULL"; 
//					   		 columnValues = value + "," ;
//					   		 System.out.println("columnValues : "+ columnValues);
//						 }
//						 else
//						 {
//							 System.out.println("value: -> "+value);
//					    	   columnValues = "'" + value + "'" + ",";
//					    	   System.out.println("columnValues : "+ columnValues);
//						 }
//					  }
//			    	  
//			    	  else if (key.equalsIgnoreCase("recmultilanguageSelectionWelcomeURL"))
//					  {
//						 JSONObject jReq = json.getJSONObject(key) ;
//						 String selectionURL_value = jReq.getString("urlList");
//						 System.out.println("selectionURL_value : "+selectionURL_value);
//						 
//						 if(value!=null && value.length()>0 && value.equalsIgnoreCase("") == false)
//						 { 
//							 value = "NULL"; 
//					   		 columnValues = value + "," ;
//					   		 System.out.println("columnValues : "+ columnValues);
//						 }
//						 else
//						 {
//							 System.out.println("value: -> "+value);
//					    	   columnValues = "'" + value + "'" + ",";
//					    	   System.out.println("columnValues : "+ columnValues);
//					   	 }
//					  }
			    	  
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
				  String grpzcode = json.getString("groupzCode");
				  String groupzcode = grpzcode.trim();
				  
				  System.out.println("Column names:"+ivrData_keys[i]);
				 
				  if((ivrData_keys[i].equalsIgnoreCase("ivrnumber")==false) && (ivrData_keys[i].equalsIgnoreCase("groupzCode")==false) && (ivrData_keys[i].equalsIgnoreCase("scope") == false) && (ivrData_keys[i].equalsIgnoreCase("type") == false))
				  {
			    	  sql = ivrData_keys[i] + "=" + columnValues;
						
					  System.out.println("sql : "+sql);
					  String sql_query = updateSQL + sql + " WHERE ivrnumber='" +ivrnumber+ "' and groupzCode='" +groupzcode+ "';";      
					  System.out.println("The update SQL : " + sql_query) ;
					  stmt.executeUpdate(sql_query); 
					  String ret_list_Qry = selectSQL +" WHERE ivrnumber='"+ivrnumber+"' and groupzCode='"+groupzcode+"';";
					  rs = stmt.executeQuery(ret_list_Qry);
				  }
			  }
			  if(rs!=null)
			  {
				 while (rs.next())
				 {
					 dataObj.put("ivrnumber",rs.getString("ivrnumber"));
					 dataObj.put("groupZCode",rs.getString("groupZCode"));
					 dataObj.put("welcomeNotes",rs.getString("welcomeNotes"));
					 dataObj.put("audioWelcomeUrl",rs.getString("audioWelcomeUrl"));
					 dataObj.put("selectionlist",rs.getString("selectionlist"));
					 dataObj.put("selectionlistUrl",rs.getString("selectionlistUrl"));
					 dataObj.put("groupzNameUrl",rs.getString("groupzNameUrl"));
					 dataObj.put("multiLanguageFlag", rs.getInt("multiLanguageFlag"));
					 dataObj.put("recmultilanguageSelectionList",rs.getString("recmultilanguageSelectionList"));
					 dataObj.put("recmultilanguageSelectionWelcomeURL",rs.getString("recmultilanguageSelectionWelcomeURL"));
					 dataObj.put("endDate",rs.getString("endDate"));
					 dataObj.put("address",rs.getString("address"));
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
		     String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
			 String statusmessage = IVRAdminConfig.prop.getProperty("updateerrornotes");
			 response = IVRAdminConfig.createResponse(statuscode, statusmessage);  
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
		    	  String statusmessage = IVRAdminConfig.prop.getProperty("dberrordisconnection");
		    	  String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
				  response = IVRAdminConfig.createResponse(statuscode, statusmessage);
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
		         String statusmessage = IVRAdminConfig.prop.getProperty("dberrordisconnection");
		    	 String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
				 response = IVRAdminConfig.createResponse(statuscode, statusmessage);
				 return response;
		      }
	   }	
	}
}