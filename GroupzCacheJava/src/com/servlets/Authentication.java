package com.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.connection.Mongo_Connection;
import com.managers.AuthenticationManager;



//@WebServlet("/Authentication")
public class Authentication extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger(Authentication.class.getName());

    public Authentication() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			doPost(request, response);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String RegRequest = request.getParameter("request");
		Calendar start = Calendar.getInstance();
		log.info("The Request is- :" + RegRequest);
		AuthenticationManager am = new AuthenticationManager();
		String Response = am.getResponse(RegRequest);
		Mongo_Connection conn = new Mongo_Connection();
		conn.closeConnection();
		log.info("The Response is :" + Response);
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.append(Response);
		Calendar end = Calendar.getInstance();
		long processingTime = end.getTimeInMillis() - start.getTimeInMillis();
		log.info("Total Time Consumed  :" + processingTime);
	}

}
