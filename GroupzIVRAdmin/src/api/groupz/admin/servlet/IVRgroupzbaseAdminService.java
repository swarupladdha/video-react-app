package api.groupz.admin.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import api.groupz.admin.config.IVRbaseAdminConfig;
import api.groupz.admin.module.IVRbaseAdminService;



public class IVRgroupzbaseAdminService extends HttpServlet
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
			JSONObject jsonReq = (JSONObject) JSONSerializer.toJSON(jSonParameter);
						
			JSONObject json = jsonReq.getJSONObject("json") ;
			JSONObject jReq = json.getJSONObject("request") ;
			String servicetype = jReq.getString("servicetype");
			System.out.println("service Type : " + servicetype);
			String functiontype = jReq.getString("functiontype");
			String ivrData = jReq.getString("data");
			
			IVRbaseAdminService adminService = new IVRbaseAdminService();
			String Response = adminService.process(ivrData, servicetype, functiontype);
			
			System.out.println("The response is : " + Response);
			out.append(Response);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			String statuscode = IVRbaseAdminConfig.prop.getProperty("errorcode");
		    String statusmessage = IVRbaseAdminConfig.prop.getProperty("requesterror");
		    String Response = IVRbaseAdminConfig.createResponse(statuscode, statusmessage);
		    System.out.println(Response);
		    out.append(Response);
		}
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doPost(request, response);
	}

}
