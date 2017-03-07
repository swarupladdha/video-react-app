package com.jobztop.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jobztop.manager.EmployerManager;
import com.jobztop.manager.ProfileManager;
import com.jobztop.utils.DBConnect;


public class EmployerProfile extends HttpServlet {
	static Logger log = Logger.getLogger(Profile.class.getName());

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void init() throws ServletException {
		// super.init(config);
		try {
			DBConnect.establishConnection();
		} catch (Exception e) {
		}
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String profieRequest = request.getParameter("request");
		Calendar start = Calendar.getInstance();
		log.info("The Request is :" + profieRequest);
		EmployerManager em = new EmployerManager();
		String Response = em.profileResponse(profieRequest, request);
		log.info("The Response is :" + Response);
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.append(Response);
		Calendar end = Calendar.getInstance();
		long processingTime = end.getTimeInMillis() - start.getTimeInMillis();
		log.info("Total Time Consumed  :" + processingTime);

	}
}
