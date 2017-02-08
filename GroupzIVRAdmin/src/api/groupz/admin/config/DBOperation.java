package api.groupz.admin.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class DBOperation {


		static final String selectSQL = "SELECT * FROM admin WHERE ";
		
		public static String connectDBandCheck(String emailId, String passwordId)
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
		      
		       System.out.println("Creating Query");
		       
		       	      
		       String sql =  "email='"+emailId+"' and password='"+passwordId+"';";
			   System.out.println("sql : "+sql);
			   String sql_query = selectSQL + sql;      
			   System.out.println("The select SQL : " + sql_query) ;
			   ResultSet rs = stmt.executeQuery(sql_query);
			  
			   if(rs!=null)
			   {
				  while (rs.next())
				  {
				 	  dataObj.put("username",rs.getString("username"));
				 	  dataObj.put("email", rs.getString("email"));
				 	  dataObj.put("password", rs.getString("password"));
				 	  jarray.add(dataObj);
				  }
			   }
		      
			    System.out.println("jarray : "+ jarray);
			    
			    if (jarray != null && jarray.size()>0)
			    {
			    	System.out.println("Records are in the table...");
				     String statuscode = PropertiesConfig.prop.getProperty("successcode");
				     String statusmessage = "Success";
				     response = PropertiesConfig.createResponse(statuscode, statusmessage);
				     return response;
			    }
			    else
			    {
			    	String statuscode = PropertiesConfig.prop.getProperty("errorcode");
					String statusmessage = PropertiesConfig.prop.getProperty("invaliddata");
					response = PropertiesConfig.createResponse(statuscode, statusmessage);  
				    return response;
			    }
		     } 
			 catch(Exception e)
		     {
			     //Handle errors for Class.forName
			     e.printStackTrace();
			     String statuscode = PropertiesConfig.prop.getProperty("errorcode");
				 String statusmessage = PropertiesConfig.prop.getProperty("errornotes");
				 response = PropertiesConfig.createResponse(statuscode, statusmessage);  
			     return response;
		      }
		   finally
		   {
		      try
		      {
		         if(stmt!=null)
		        	 ConnectionManager.closeConnection(conn);
		      }
		      catch(Exception e)
		      {
		    	  String statusmessage = PropertiesConfig.prop.getProperty("dberrordisconnection");
		    	  String statuscode = PropertiesConfig.prop.getProperty("errorcode");
				  response = PropertiesConfig.createResponse(statuscode, statusmessage);
				  return response;
		      }
		      try
		      {
		         if(conn!=null)
		        	 ConnectionManager.closeConnection(conn);
		      }
		      catch(Exception e)
		      {
		         e.printStackTrace();
		         String statusmessage = PropertiesConfig.prop.getProperty("dberrordisconnection");
		    	 String statuscode = PropertiesConfig.prop.getProperty("errorcode");
				 response = PropertiesConfig.createResponse(statuscode, statusmessage);
				 return response;
		      }
		   }
		}
}
