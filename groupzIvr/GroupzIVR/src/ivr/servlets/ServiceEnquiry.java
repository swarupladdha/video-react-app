package ivr.servlets;

import ivr.modules.serviceRequest.ProcessEnquiry;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class ServiceEnquiry extends HttpServlet
{

	private static final long serialVersionUID = -1085881920110448447L;

	public static Logger loggerServ = Logger.getLogger("serviceLogger");
	public static boolean maintenanceFlag = false;

	static
	{

		try
		{
			System.setProperty("Hibernate-Url", ServiceRequest.class.getResource("/Hibernate.cfg.xml").toString());
			Properties logProperties = null;
			logProperties = new Properties(System.getProperties());
			InputStream inlog = ServiceRequest.class.getResourceAsStream("/log4j.properties");
			logProperties.load(inlog);

			System.out.println("inside log properties" + logProperties);
			loggerServ.info("Log  initialized in GroupzIVR class ");
		}
		catch (Exception e)
		{
			loggerServ.info("Exception occured in load property files in service request class.", e);
			e.printStackTrace();
		}
	}

	private static final String IVRSTATE = "event";
	private static final String IVRCALLER = "cid";
	private static final String IVRDATA = "data";
	private static final String IVRSENDERID = "sid";
	private static final String IVRNUMBER = "called_number";
	
	public PrintWriter out;
	boolean landLineFlag = false;

	public void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
	{
		System.out.println("Inside IVR_Servlet");
		
		try
		{
			out = response.getWriter();
			
			String callerId = request.getParameter(IVRCALLER);
			String ivrNum = request.getParameter(IVRNUMBER);
			String sid = request.getParameter(IVRSENDERID);
			String event = request.getParameter(IVRSTATE);
			String selection = request.getParameter(IVRDATA);
			String retString = new String();
			
			loggerServ.info("The data received are caller_id,selection,ivrnumber,sid -- " + callerId + " , " + selection + " , " + ivrNum
					+ " , " + sid); 

			ProcessEnquiry enquiry = new ProcessEnquiry();
			if (event != null && event.trim().equalsIgnoreCase("NewCall"))
			{
				retString = enquiry.processNewCall(callerId, ivrNum, sid);
			}

			if (event != null && event.trim().equalsIgnoreCase("GotDTMF"))
			{	
				retString = enquiry.processContinuousCall(sid, selection);
			}
			System.out.println("*************************");
			System.out.println(retString);
			out.print(retString);
		}
		catch (Exception e)
		{
			loggerServ.info("Exception occured in service request  ", e);
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
	{
		doGet(request, response);
	}

}
