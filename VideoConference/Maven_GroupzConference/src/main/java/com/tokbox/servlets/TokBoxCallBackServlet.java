package com.tokbox.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.tokbox.task.TokBox;
import com.tokbox.utils.Layer;
import com.tokbox.utils.RestUtils;

@WebServlet("/callback")
public class TokBoxCallBackServlet extends HttpServlet {
	
	static final Logger logger = Logger.getLogger(TokBoxCallBackServlet.class);
	private static final long serialVersionUID = 1L;
    public TokBoxCallBackServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		logger.info(request);
		String mediaRequest = request.getParameter("request");
		logger.info("The callback request is : " + mediaRequest);
		logger.info("request.getRequestURL()"+request.getRequestURL());
		Layer edu = new TokBox();
		String data = IOUtils.toString(request.getInputStream(), "UTF-8");
		if (RestUtils.isEmpty(data)== false) {
			data = mediaRequest;
		}
		logger.info("------------------");
		logger.info(data);
		if (RestUtils.isEmpty(data)== true) {
			edu.callBack(data);
		}
		logger.info("--------------------");

	}

}

