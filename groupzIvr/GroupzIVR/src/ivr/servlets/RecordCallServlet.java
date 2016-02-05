package ivr.servlets;


import ivr.modules.recordRequest.DownloadURLAndPublishRecordXML;
import ivr.modules.recordRequest.ProcessRecordrequest;


import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


public class RecordCallServlet extends HttpServlet {



	/**
	 * 
	 */
	private static final long serialVersionUID = -2639387647848787186L;

	public static Logger logger =  Logger.getLogger("recordLogger");

	static Properties prop = new Properties();
	public static boolean maintenanceFlag = false;
	static {

		try {

			System.setProperty("Hibernate-Url",
					RecordCallServlet.class.getResource("/Hibernate.cfg.xml").toString());

			Properties logProperties = null;
			logProperties = new Properties(System.getProperties());

			InputStream inlog = RecordCallServlet.class
					.getResourceAsStream("/log4j.properties");
			logProperties.load(inlog);
			
			InputStream in = ProcessRecordrequest.class
					.getResourceAsStream("/ivr.properties");
			prop.load(in);

			System.out.println("inside log properties" + logProperties);
			logger.info("Log  initialized in RecordCallServlet class ");

		} catch (Exception e) {
			logger.info(
					"Exception occured in load property files in record request class.",
					e);
			e.printStackTrace();
		}
	}

	private static final String IVRSTATE = "event";

	private static final String IVRCALLER = "cid";
	private static final String IVRDATA = "data";
	private static final String IVRSENDERID = "sid";

	private static final String IVRNUMBER = "called_number";

	public PrintWriter out;
	
	public void init(ServletConfig config) throws ServletException{
		System.out.println("init started");
				try {
					super.init(config);
					
					
					String tsize = prop.getProperty("thread_pool_size");
					
					int THREAD_POOL_SIZE = Integer.parseInt(tsize);
					
					// ExecutorService recordTPExecSvc = Executors
					//	.newFixedThreadPool(THREAD_POOL_SIZE);
					 
					 
				 
					// for (int i = 0; i < THREAD_POOL_SIZE; i++) {

					//recordTPExecSvc.execute(new DownloadURLAndPublishRecordXML(i));

					//	}
					
				
					
				}catch (Exception e) {

					e.printStackTrace();
					logger.info(
							"Exception occured in recordcall initialize method .",
							e);

				}
	}
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("Inside IVR");
		try {
			out = response.getWriter();

			String callerId = request.getParameter(IVRCALLER);
			String ivrNum = request.getParameter(IVRNUMBER);
			String sid = request.getParameter(IVRSENDERID);
			String event = request.getParameter(IVRSTATE);
			String selection = request.getParameter(IVRDATA);
			String retString = new String();
			
			
			
		
			

			ProcessRecordrequest inqObj = new ProcessRecordrequest();
			if (event != null && event.trim().equalsIgnoreCase("NewCall")) {
				logger.info("IvrNumber,caller id,session id NEWCALL--  " + ivrNum + " , "+ callerId+ " , "+ sid);
				retString = inqObj.processNewCall(callerId, ivrNum, sid);
			}

			if (event != null && event.trim().equalsIgnoreCase("Record")) {
				
				logger.info("The data received and session id --  " + selection + " , "+ sid);
				retString = inqObj.processRecord(sid,selection);
			}
			
			if (event != null && event.trim().equalsIgnoreCase("GotDTMF")) {
				
				logger.info("The data received and session id --  " + selection + " , "+ sid);
				retString = inqObj.processContinuesRecordCall(sid,selection);
			}

			System.out.println(retString);

			out.print(retString);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception occured in Groupz IVR Record ", e);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
