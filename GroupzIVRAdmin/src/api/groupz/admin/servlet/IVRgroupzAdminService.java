package api.groupz.admin.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import api.groupz.admin.config.IVRAdminConfig;
import api.groupz.admin.module.IVRAdminService;



public class IVRgroupzAdminService extends HttpServlet
{
		
    
	private static final long serialVersionUID = 1L;
	
	public PrintWriter out;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		System.out.println("Inside IVR_Servlet");
		
		try
		{
			out = response.getWriter();
			String jSonParameter = request.getParameter("request") ; //JSONREQUEST ;
			System.out.println(request);			
			JSONObject jsonReq = (JSONObject) JSONSerializer.toJSON(jSonParameter);
					
			JSONObject json = jsonReq.getJSONObject("json") ;
			JSONObject jReq = json.getJSONObject("request") ;
			String servicetype = jReq.getString("servicetype");
			String functiontype = jReq.getString("functiontype");
			String ivrData = jReq.getString("data");
			
			IVRAdminService adminService = new IVRAdminService();
			String Response = adminService.process(ivrData, servicetype, functiontype);
			
			System.out.println("The response is : " + Response);
			out.append(Response);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			String statuscode = IVRAdminConfig.prop.getProperty("errorcode");
		    String statusmessage = IVRAdminConfig.prop.getProperty("requesterror");
		    String Response = IVRAdminConfig.createResponse(statuscode, statusmessage);
		    System.out.println(Response);
		    out.append(Response);
		}
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doPost(request, response);
	}

}
