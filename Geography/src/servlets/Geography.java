package servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import manager.GeographyManager;

public class Geography extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Calendar start = Calendar.getInstance();
		String req = request.getParameter("request");
		System.out.println("Request is :" + req);
		GeographyManager gm = new GeographyManager();
		String res = gm.getGeographyResponse(req);
		System.out.println("The Response is :" + res);
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.append(res);
		Calendar end = Calendar.getInstance();
		long processingTime = end.getTimeInMillis() - start.getTimeInMillis();
		System.out.println("End Time :" + end.getTime()
				+ " and Total Time Consumed  :" + processingTime);

	}
}
