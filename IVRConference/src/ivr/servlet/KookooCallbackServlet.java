package ivr.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class KookooCallbackServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		PrintWriter out = response.getWriter();
				
		Enumeration<String> parameters = request.getParameterNames();
		
		out.print("<br> Callback from Kookoo");
		while (parameters.hasMoreElements())
		{
			String parameter = parameters.nextElement();
			String value = request.getParameter(parameter);
			String pair = " * " + parameter + " = " + value;
			out.println(pair);
			//session.setAttribute(parameter,value);
			System.out.println(pair);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException
	{
		doGet(request, response);
	}
}