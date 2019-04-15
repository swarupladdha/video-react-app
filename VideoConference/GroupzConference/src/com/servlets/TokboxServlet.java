package com.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.manager.TokBoxManager;


public class TokboxServlet extends HttpServlet {
	
	static final Logger logger = Logger.getLogger(TokboxServlet.class);
	private static final long serialVersionUID = 1L;
    public TokboxServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String mediaRequest = request.getParameter("request");
		logger.info("The request is : " + mediaRequest);
		TokBoxManager edu = new TokBoxManager();
		logger.info("calling tokboxmanager");
		String mediaResponse = edu.getResponse(mediaRequest);
		logger.info("The response is : " + mediaResponse);
		response.setContentType("application/json; charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.append(mediaResponse);
	}

}
