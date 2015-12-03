package ivr.servlets;

import ivr.modules.inquiryRequest.InquiryRequest;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class Inquiry extends HttpServlet {

	private static final long serialVersionUID = -1085881920110448447L;

	public static Logger logger = Logger.getLogger("inquiryLogger");

	public static boolean maintenanceFlag = false;

	static {

		try {

			System.setProperty("Hibernate-Url",
					Inquiry.class.getResource("/Hibernate.cfg.xml").toString());

			Properties logProperties = null;
			logProperties = new Properties(System.getProperties());

			InputStream inlog = Inquiry.class
					.getResourceAsStream("/log4j.properties");
			logProperties.load(inlog);

			System.out.println("inside log properties" + logProperties);
			logger.info("Log  initialized in Inquiry class ");

		} catch (Exception e) {
			logger.info(
					"Exception occured in load property files in Inquiry request class.",
					e);
			e.printStackTrace();
		}
	}

	private static final String IVRSTATE = "event";

	private static final String IVRCALLER = "cid";
	private static final String IVRDATA = "data";
	private static final String IVRSENDERID = "sid";

	private static final String IVRNUMBER = "called_number";

	public static String TERMINALCHAR = "#";

	public PrintWriter out;

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

			InquiryRequest inqObj = new InquiryRequest();
			if (event != null && event.trim().equalsIgnoreCase("NewCall")) {
				retString = inqObj.processNewCall(callerId, ivrNum, sid);
			}

			if (event != null && event.trim().equalsIgnoreCase("GotDTMF")) {
				logger.info("The data received and session id --  " + selection + " , "+ sid);
				retString = inqObj.processContinuousCall(sid, selection);
			}
	

			System.out.println(retString);

			out.print(retString);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception occured in Groupz SMS service ", e);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
