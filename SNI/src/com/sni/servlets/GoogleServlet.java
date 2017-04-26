package com.sni.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.sni.manager.SNIManager;



public class GoogleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static final Logger logger = Logger.getLogger(GoogleServlet.class);

    protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String registrationRequest = request.getParameter("request");
		String idTokenString = request.getParameter("token");
		logger.info("The request is : " + registrationRequest);
		SNIManager ofdmanager = new SNIManager();
		String registrationResponse = ofdmanager.getRegistrationResponse(registrationRequest,idTokenString);
		logger.info("The response is : " + registrationResponse);
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.append(registrationResponse);

	}
}
