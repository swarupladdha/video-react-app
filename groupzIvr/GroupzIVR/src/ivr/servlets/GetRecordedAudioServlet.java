package ivr.servlets;

import ivr.modules.recordRequest.ProcessAudioRequest;
import ivr.utils.StaticUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;

public class GetRecordedAudioServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	public static Logger loggerrec = Logger.getLogger("recordLogger");

	public static boolean maintenanceFlag = false;

	static {

		try {

			System.setProperty("Hibernate-Url", GetRecordedAudioServlet.class
					.getResource("/Hibernate.cfg.xml").toString());

			Properties logProperties = null;
			logProperties = new Properties(System.getProperties());

			InputStream inlog = GetRecordedAudioServlet.class
					.getResourceAsStream("/log4j.properties");
			logProperties.load(inlog);

			System.out.println("inside log properties" + logProperties);
			loggerrec.info("Log  initialized in GroupzIVR - get audio class ");

		} catch (Exception e) {
			loggerrec
					.info("Exception occured in load property files in getaudio request class.",
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
	boolean landLineFlag = false;

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

			ProcessAudioRequest proObj = new ProcessAudioRequest();

			if (event != null && event.trim().equalsIgnoreCase("NewCall")) {

				loggerrec
						.info("The data received are caller_id,selection,ivrnumber,sid -- "
								+ callerId
								+ " , "
								+ selection
								+ " , "
								+ ivrNum
								+ " , " + sid);

				String ipAddress = request.getHeader("X-FORWARDED-FOR");

				if (StaticUtils.isEmptyOrNull(ipAddress)) {
					ipAddress = request.getRemoteAddr();
					loggerrec.info("IP Address 2nd:  " + ipAddress);
				}
				retString = proObj.processNewCall(callerId, ivrNum, sid,
						ipAddress);
			}

			if (event != null && event.trim().equalsIgnoreCase("GotDTMF")) {

				loggerrec.info("The data received are selection,sid -- "
						+ selection + " , " + sid);

				retString = proObj.processContinuousCall(sid, selection);
			}
			
		

			System.out.println(retString);

			out.print(retString);
		} catch (Exception e) {
			e.printStackTrace();
			loggerrec.info("Exception occured in get audio service ", e);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
