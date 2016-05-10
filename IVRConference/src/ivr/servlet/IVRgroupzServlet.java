package ivr.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import ivr.module.IVRTest;


public class IVRgroupzServlet extends HttpServlet
{    

	private static final long serialVersionUID = 1L;
	
	public static Logger loggerServ = Logger.getLogger("serviceLogger");
	public static boolean maintenanceFlag = false;
	
	public PrintWriter out;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		System.out.println("Inside IVR_Servlet");
		
		try
		{
			out = response.getWriter();
			String jSonParameter = request.getParameter("request") ; //JSONREQUEST ;
			JSONObject jsonObj = new JSONObject(jSonParameter);
			JSONObject json = (JSONObject) jsonObj.get("json");
			JSONObject jReq = (JSONObject) json.get("request");
			Object ivrObj = jReq.get("data");
			String ivrData = String.valueOf(ivrObj);
			
			IVRTest service = new IVRTest();
			String Response = service.process(ivrData);
			
			System.out.println("The response is : " + Response);
			out.append(Response);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doPost(request, response);
	}

}
