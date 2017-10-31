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
import com.managers.Add_ServiceManager;


//@WebServlet("/Add_Service")
public class Add_Service extends HttpServlet {
	private static final long serialVersionUID = 1L;
	static Logger log = Logger.getLogger(Add_Service.class.getName());

    public Add_Service() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
}


protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
	String RegRequest = request.getParameter("request");
	Calendar start = Calendar.getInstance();
	log.info("The Request is- :" + RegRequest);
	Add_ServiceManager am = new Add_ServiceManager();
	String Response = am.getResponse(RegRequest);
	Mongo_Connection.closeConnection();
	log.info("The Response is :" + Response);
	response.setContentType("application/json; charset=UTF-8");
	PrintWriter writer = response.getWriter();
	writer.append(Response);
	Calendar end = Calendar.getInstance();
	long processingTime = end.getTimeInMillis() - start.getTimeInMillis();
	log.info("Total Time Consumed  :" + processingTime);
}

}
