package api.groupz.admin.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;



import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

public class DBOperation {


		static final String selectSQL = "SELECT * FROM admin WHERE ";
		static  String selectSQL1 = "SELECT ivrnumber ,groupzBase  FROM ivrgroupzbase ";
		static final String selectSQL2 = "SELECT ivrnumber,groupzBase,groupZCode  FROM ivrgroupz WHERE ";

		
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
		
		public static String getList(String ivrData)
		{
			Connection conn = null;
			Statement stmt = null;
		//	int limit=100;
		//	int offset=0;
			String response = "";
		   try
		   {
			   JSONObject json = (JSONObject) JSONSerializer.toJSON(ivrData);
			   int limit = json.getInt("limit");
				int offset =json.getInt("offset");
			   
			   JSONObject dataObj = new JSONObject();
			   JSONArray jarray1 = new JSONArray();
			   //JSONArray jarray2 = new JSONArray();
			   
		       conn = ConnectionManager.getConnect();

		       System.out.println("Database connected successfully...");
		       stmt = conn.createStatement();
		      
		       System.out.println("Creating Query");
		       
		       String sql=selectSQL1 + paginationQry(limit, offset )+";";
		       	          
			   System.out.println("The select SQL : " + sql) ;
			   
			   ResultSet rs = stmt.executeQuery(sql);
			  
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
					response = PropertiesConfig.createResponse1(statuscode, statusmessage,null);  
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
			
			Connection conn = null;
			Statement stmt = null;
			//int limit=100;
			//int offset=-1;
			String response = "";
			
			JSONObject json = (JSONObject) JSONSerializer.toJSON(ivrData);
			String ivrnumber = json.getString("ivrnumber");
			String groupzBase = json.getString("groupzBase");
			int limit = json.getInt("limit");
			int offset =json.getInt("offset");
			
			
		   try
		   {
			   JSONObject dataObj = new JSONObject();
			   JSONArray jarray1 = new JSONArray();
			   //JSONArray jarray2 = new JSONArray();
			   
		       conn = ConnectionManager.getConnect();

		       System.out.println("Database connected successfully...");
		       stmt = conn.createStatement();
		      
		       System.out.println("Creating Query");
		       System.out.println(ivrnumber);
		       System.out.println();
		       String sql1=paginationQry(limit ,offset);
		       String sql =	selectSQL2+" ivrnumber = "+ivrnumber+ " and groupzBase= '"+groupzBase+"'"+sql1+";"; 
		       
			   System.out.println("The select SQL : " + sql) ;
			   ResultSet rs = stmt.executeQuery(sql);
			  
			   if(rs!=null)
			   {
				  while (rs.next())
				  {
					  dataObj.put("ivrnumber",rs.getString("ivrnumber"));
					 	 System.out.println(rs.getString("ivrnumber"));
					 	  dataObj.put("groupzBase", rs.getString("groupzBase"));
					 	System.out.println(rs.getString("groupzBase"));
					 	  dataObj.put("groupZCode", rs.getString("groupZCode"));
						 	System.out.println(rs.getString("groupzBase"));
					 	  jarray1.add(dataObj);
				  }
	
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
					System.out.println(statuscode+"   "+statusmessage);
					response = PropertiesConfig.createResponse1(statuscode, statusmessage,null);  
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
		
		public static  String paginationQry(int limit, int offset) {
			String paginationQry = "";
			
			if (limit != -1 && offset != -1) {
				paginationQry = " limit " + limit + " offset " + offset;

			}
			if (limit != -1 && offset == -1) {
				paginationQry = " limit " + limit;

			}
			if (limit == -1 && offset != -1) {
				paginationQry = " offset " + offset;

			}
			return paginationQry;
		}
}
