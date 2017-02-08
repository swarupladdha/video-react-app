package api.groupz.admin.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

import javax.servlet.ServletException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import api.groupz.admin.config.LoginValidation;
import api.groupz.admin.config.PropertiesConfig;
import net.sf.json.JSONObject;

public class Login extends HttpServlet {

	private static final long serialVersionUID = 1L;

	PrintWriter out = null;
	LoginValidation admin = new LoginValidation();
	public static Properties prop = new Properties();

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("Inside Login Servlet");
		try {
			String requestJson = request.getParameter("request");
			JSONObject jsonReq = new JSONObject();

			jsonReq = JSONObject.fromObject(requestJson);
			String servicetype = jsonReq.getJSONObject("json").getJSONObject("request").getString("servicetype");
			String functiontype = jsonReq.getJSONObject("json").getJSONObject("request").getString("functiontype");
			String email = jsonReq.getJSONObject("json").getJSONObject("request").getJSONObject("data")
					.getString("email");
			String password = jsonReq.getJSONObject("json").getJSONObject("request").getJSONObject("data")
					.getString("password");

			System.out.println(servicetype + functiontype + email + password);
			System.out.println(servicetype + "   " + functiontype + "  " + email + "  " + password + "  " + admin);
			String resp = admin.process(email, password, servicetype, functiontype);

			System.out.println("The response is : " + resp);
			response.setContentType("application/json");
			out = response.getWriter();
			out.append(resp);

		} catch (Exception e) {
			e.printStackTrace();
			String statuscode = PropertiesConfig.prop.getProperty("errorcode");
			String statusmessage = PropertiesConfig.prop.getProperty("requesterror");
			String Response = PropertiesConfig.createResponse(statuscode, statusmessage);
			System.out.println(Response);

		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}
