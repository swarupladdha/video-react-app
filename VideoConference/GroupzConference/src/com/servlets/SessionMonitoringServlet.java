package com.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.task.TokBox;
import com.utils.Layer;
import com.utils.RestUtils;

public class SessionMonitoringServlet extends HttpServlet {
	
	static final Logger logger = Logger.getLogger(SessionMonitoringServlet.class);
	private static final long serialVersionUID = 1L;
    public SessionMonitoringServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
//		logger.info(request);
//		String mediaRequest = request.getParameter("request");
//		logger.info("The callback request is : " + mediaRequest);
//		logger.info("request.getRequestURL()"+request.getRequestURL());
		Layer edu = new TokBox();
		String data = IOUtils.toString(request.getInputStream(), "UTF-8");
		logger.info("------------------");
		logger.info(data);
		if (RestUtils.isEmpty(data)== true) {
			edu.monitor(data);
		}
		logger.info("--------------------");
//		String mediaResponse = edu.getEduResponse(mediaRequest);
//		logger.info("The response is : " + mediaResponse);
//		response.setContentType("application/json; charset=UTF-8");
//		PrintWriter writer = response.getWriter();
//		writer.append(mediaResponse);
//		response.setStatus(200);
	}

}
