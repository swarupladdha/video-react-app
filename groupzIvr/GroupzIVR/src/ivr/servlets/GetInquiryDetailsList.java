package ivr.servlets;

import ivr.modules.inquiryRequest.InquiryRequest;
import ivr.modules.inquiryRequest.MigrationTasks;
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

public class GetInquiryDetailsList extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static Logger logger = Logger.getLogger("inquiryLogger");
	static Properties prop = new Properties();
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

			InputStream in = InquiryRequest.class
					.getResourceAsStream("/ivr.properties");

			prop.load(in);

		} catch (Exception e) {
			logger.info(
					"Exception occured in load property files in Inquiry Details list request class.",
					e);
			e.printStackTrace();
		}
	}

	public PrintWriter out;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		out = response.getWriter();
		String result = null;
		boolean wrondataFlag = false;
		try {

			String startlimit = request.getParameter("start");

			String functype = request.getParameter("functiontype");

			String data = request.getParameter("data");

			if (functype != null && functype.isEmpty() == false) {

				if (functype.equals("migrateTrue")
						|| functype.equals("migrateFalse")) {

					int start = 0;
					if (startlimit != null && startlimit.isEmpty() == false) {
						start = Integer.parseInt(startlimit);
					}

					result = MigrationTasks.listOfMigration(functype, start);
				} else if (functype.equals("update")) {

					if (data != null && data.isEmpty() == false) {
						result = MigrationTasks.UpdateMigratedList(data);
					} else {
						wrondataFlag = true;
					}

				} else {
					wrondataFlag = true;
				}

			} else {

				wrondataFlag = true;

			}

			if (wrondataFlag) {
				String stswrngdatacode = prop.getProperty("wrongdatacode");
				String dataerrorMsg = "wrongData";
				result = StaticUtils.Createrespobject(dataerrorMsg,
						stswrngdatacode);
			}

			out.print(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.info("Exception occured in GetMigrationList.", e);

			String resultstr = "Technical Error Occured.";

			String stserror = prop.getProperty("errorcode");

			result = StaticUtils.Createrespobject(resultstr, stserror);
			out.print(result);
		}

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
