package api.groupz.admin.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class DBOperation {


		static final String selectSQL = "SELECT * FROM admin WHERE ";
		static final String selectSQL1 = "SELECT ivrnumber ,groupzBase  FROM ivrgroupzbase ";
		static final String selectSQL2 = "SELECT id  FROM ivrgroupzbase WHERE ";

		
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
				 	  System.out.println(rs.getString(1));
				 	  dataObj.put("email", rs.getString("email"));
				 	 System.out.println(rs.getString(2));
				 	  dataObj.put("password", rs.getString(3));
				 	 System.out.println(rs.getString("password"));
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
		
		public static String getList()
		{
			Connection conn = null;
			Statement stmt = null;
			String response = "";
		   try
		   {
			   JSONObject dataObj = new JSONObject();
			   JSONArray jarray1 = new JSONArray();
			   //JSONArray jarray2 = new JSONArray();
			   
		       conn = ConnectionManager.getConnect();

		       System.out.println("Database connected successfully...");
		       stmt = conn.createStatement();
		      
		       System.out.println("Creating Query");
		       
		       	          
			   System.out.println("The select SQL : " + selectSQL1) ;
			   ResultSet rs = stmt.executeQuery(selectSQL1);
			  
			   if(rs!=null)
			   {
				  while (rs.next())
				  {
					  
				 	  dataObj.put("ivrnumber",rs.getString("ivrnumber"));
				 	 System.out.println(rs.getString("ivrnumber"));
				 	  dataObj.put("groupzBase", rs.getString("groupzBase"));
				 	System.out.println(rs.getString("groupzBase"));
				 	  jarray1.add(dataObj);
				  }
				  //jarray2.add(jarray1);
			   }
		      
			    System.out.println("jarray1 : "+ jarray1);
			    
			    if (jarray1 != null && jarray1.size()>0)
			    {
			    	System.out.println("Records are in the table...");
				     String statuscode = PropertiesConfig.prop.getProperty("successcode");
				     String statusmessage = "Success";
				     response = PropertiesConfig.createResponse1(statuscode, statusmessage,jarray1);
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

		public static String getDetailList(String ivrData) {
			JSONObject json = (JSONObject) JSONSerializer.toJSON(ivrData);
			String ivrnumber = json.getString("ivrnumber");
			String groupzBase = json.getString("groupzBase");
			
			Connection conn = null;
			Statement stmt = null;
			String response = "";
		   try
		   {
			   JSONObject dataObj = new JSONObject();
			   JSONArray jarray1 = new JSONArray();
			   //JSONArray jarray2 = new JSONArray();
			   
		       conn = ConnectionManager.getConnect();

		       System.out.println("Database connected successfully...");
		       stmt = conn.createStatement();
		      
		       System.out.println("Creating Query");
		       
		       String sql =	selectSQL2+" ivrnumber = "+ivrnumber+ " and groupzBase= "+groupzBase+";";          
			   System.out.println("The select SQL : " + sql) ;
			   ResultSet rs = stmt.executeQuery(sql);
			  
			   if(rs!=null)
			   {
				  while (rs.next())
				  {
					 int id = rs.getInt("Id");
					 System.out.println(id);
				  }
				  String sql1= "SELECT ivrnumber ,groupzBase,groupZCode  FROM ivrgroupz where ivrnumber ="+ivrnumber+"  and groupzBase ="+groupzBase+";";
				  ResultSet rs1 = stmt.executeQuery(sql1);
				  
				  while(rs1.next())
				  {
				 
				 	  dataObj.put("ivrnumber",rs1.getString("ivrnumber"));
				 	 System.out.println(rs1.getString("ivrnumber"));
				 	  dataObj.put("groupzBase", rs1.getString("groupzBase"));
				 	System.out.println(rs1.getString("groupzBase"));
				 	  dataObj.put("groupZCode", rs1.getString("groupZCode"));
					 	System.out.println(rs1.getString("groupzBase"));
				 	  jarray1.add(dataObj);
				  }
				  //jarray2.add(jarray1);
			   }
		      
			    System.out.println("jarray1 : "+ jarray1);
			    
			    if (jarray1 != null && jarray1.size()>0)
			    {
			    	System.out.println("Records are in the table...");
				     String statuscode = PropertiesConfig.prop.getProperty("successcode");
				     String statusmessage = "Success";
				     response = PropertiesConfig.createResponse1(statuscode, statusmessage,jarray1);
				     return response;
			    }
			    else
			    {
			    	System.out.println("No match found"+PropertiesConfig.prop.getProperty("nomatcherror"));
			    	String statuscode = PropertiesConfig.prop.getProperty("errorcode");
					String statusmessage = PropertiesConfig.prop.getProperty("nomatcherror");
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
