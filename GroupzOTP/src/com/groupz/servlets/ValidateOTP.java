package com.groupz.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.groupz.manager.OtpManager;

public class ValidateOTP extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String otpRequest = request.getParameter("request");

		System.out.println("The request is : " + otpRequest);
		OtpManager otp = new OtpManager();
		// String otpResponse = "";
		String otpResponse = otp.validateOTP(otpRequest);
		System.out.println("The response is : " + otpResponse);

		response.setContentType("application/json; charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.append(otpResponse);
	}
}
