package com.groupz.sendsms;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.log4j.Logger;
import com.groupz.sendsms.report.GetSmsDeliveryReport;
import com.groupz.sendsms.report.MergeSMSStatusReportManager;

//Class creates threadpools, listens ActiveMQ Queue for messages and once it receives messages it will insert into DB.
public class GroupzSmsService extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4050785619823247926L;
	private PrintWriter out;
	static String tsize = null;

	private static Logger logger = Logger.getLogger("smsLogger");

	static Properties prop = new Properties();

	static ExecutorService messagesInTableTPExecSvc = null;
	static ExecutorService getreportTPExecSvc = null;
	static ExecutorService reportmergeTPExecSvc = null;

	static int THREAD_POOL_SIZE = 0;

	//public static void createThreads() {
	
	public void init(ServletConfig config) throws ServletException{
System.out.println("init started");
		try {
			super.init(config);

			System.setProperty("Hibernate-Url", GroupzSmsService.class
					.getResource("/Hibernate.cfg.xml").toString());

			Properties logProperties = null;
			logProperties = new Properties(System.getProperties());

			InputStream in = GroupzSmsService.class
					.getResourceAsStream("/log4j.properties");
			logProperties.load(in);

			InputStream insmpp = GroupzSmsService.class
					.getResourceAsStream("/smppConf.properties");
			prop.load(insmpp);

			tsize = prop.getProperty("thread_pool_size");

			logger.info("Log and hibernate url initialized in GroupzSmsmService class ");

			System.out.println("entered config");

			THREAD_POOL_SIZE = Integer.parseInt(tsize);

			messagesInTableTPExecSvc = Executors
					.newFixedThreadPool(THREAD_POOL_SIZE);
			getreportTPExecSvc = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
			reportmergeTPExecSvc = Executors
					.newFixedThreadPool(THREAD_POOL_SIZE);

			for (int i = 0; i < THREAD_POOL_SIZE; i++) {

			messagesInTableTPExecSvc.execute(new ProcessMessagesInTable(i));

			}
			System.out.println("Threads started");
		} catch (Exception e) {

			e.printStackTrace();
			logger.info(
					"Exception occured in GroupzSmsmService initialize method .",
					e);

		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		System.out.println("Inside Groupz sms");
		String result = "Please provide the data";
		out = response.getWriter();
		final Future<String> task;
		String dataString = request.getParameter("xmlString");
		String functionType = request.getParameter("functiontype");
	
		if (dataString != null && dataString.isEmpty() == false
				&& functionType != null && functionType.isEmpty() == false) {
			
			String smsfunctiontype = prop.getProperty("smsfunctyp");
			String reportfunctiontype=prop.getProperty("reporttyp");
			String mergfunctiontype=prop.getProperty("mergtype");
			

			try {

				if (functionType.equals(smsfunctiontype)) {
System.out.println("xml : " +dataString);
					InsertSmsData insObj = new InsertSmsData();
					result = insObj.InsertData(dataString);

				}

				else if (functionType.equals(reportfunctiontype)) {

					task = getreportTPExecSvc.submit(new GetSmsDeliveryReport(
							dataString));
					result = task.get();
					System.out.println(result);

				}

				else if (functionType.equals(mergfunctiontype)) {
					task = reportmergeTPExecSvc
							.submit(new MergeSMSStatusReportManager());
					result = task.get();
					System.out.println(result);
				}
				System.out.println("Final Result--- " + result);

				System.out.println(result);

			} catch (Exception e) {
				e.printStackTrace();
				logger.info("Exception occured in Groupz SMS service ", e);
			}
		}
		
		 JSONObject respo = (JSONObject) JSONSerializer.toJSON(result);

		out.print(respo);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
