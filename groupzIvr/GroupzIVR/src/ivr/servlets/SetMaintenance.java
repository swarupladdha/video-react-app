package ivr.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SetMaintenance
 */
public class SetMaintenance extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	public SetMaintenance()
	{
		super();

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String maintencVal = request.getParameter("value");
		String className = request.getParameter("className");
		System.out.println(maintencVal);
		System.out.println("Before value bool :" + ServiceRequest.maintenanceFlag);

		if (className.equals("ServiceRequest"))
		{
			if (maintencVal.equals("true"))
			{
				ServiceRequest.maintenanceFlag = true;
			}
		}
//		else
//		{
//			if (maintencVal.equals("true"))
//		{
//
//				Inquiry.maintenanceFlag = true;
//			}
//		}

		System.out.println("after value bool :" + ServiceRequest.maintenanceFlag);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		doGet(request, response);
	}
}
