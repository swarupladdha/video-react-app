package com.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.connection.ConnectionPooling;

public class ConnectionServlet extends HttpServlet {
	static final Logger logger = Logger.getLogger(ConnectionServlet.class);
	private static final long serialVersionUID = 1L;
    public ConnectionServlet() {
        super();
    }

	@Override
	public void init() throws ServletException {
		ConnectionPooling connectionPooling = new ConnectionPooling();
	}

}

