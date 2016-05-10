<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page contentType="application/xml" trimDirectiveWhitespaces="true" %>
<%@ page import="ivr.module.ModeratorCallbackValues" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="ivr.utils.DateFormat" %>


<c:choose>
  <c:when test='${param.event == "NewCall"}'>
        <%   
        
        ModeratorCallbackValues callbackvalues = new ModeratorCallbackValues();   
	        
	        String moderator = request.getParameter("moderator_no");
	    	//callbackvalues.getModerator(moderator);
	    	//System.out.println("moderator : "+moderator);
	    	//out.println("moderator : "+moderator);
	                
	    	String conference_id = request.getParameter("conference_id");
	    	String startTime = DateFormat.getLatestSynchTime();
	    	
	    	callbackvalues.getstartTimeModerator(startTime);
	    	System.out.println("startTime : "+startTime);
	        
	    	System.out.println("New call");
	        out.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><conference moderator=\"true\" caller_onhold_music=\"default\" record=\"true\">"+conference_id+"</conference></response>");
	        System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><conference moderator=\"true\" caller_onhold_music=\"default\" record=\"true\">"+conference_id+"</conference></response>");
	        session.setAttribute("state", "conferenceStarted");
	        String conferencestate = (String)request.getSession().getAttribute("state");
	        System.out.println("conferencestate : "+conferencestate);
        %>
    </c:when>
    <c:when test='${(param.event == "Conference" || param.event == "Hangup" || param.event == "Disconnect") || conferencestate == "conferenceStarted"}'>
        <%
        	System.out.println("url response");
        	        	
        ModeratorCallbackValues callbackvalues = new ModeratorCallbackValues();
        	       	
        	String sessionIdModerator = request.getParameter("sid");
        	callbackvalues.getSessionIdModerator(sessionIdModerator); 
        	System.out.println("sessionId_mod : "+sessionIdModerator);
        	
        	String conferenceIdModerator = request.getParameter("conference_id");
        	callbackvalues.getconferenceIdModerator(conferenceIdModerator); 
        	System.out.println("conferenceId_mod : "+conferenceIdModerator);
            
        	String callEndStatusModerator = request.getParameter("event");
            callbackvalues.getCallstatusModerator(callEndStatusModerator);
            System.out.println("status_mod : "+callEndStatusModerator);
            
            String calledNoModerator = request.getParameter("called_number");
        	callbackvalues.getcalledNoModerator(calledNoModerator);
        	System.out.println("calledNo_mod : "+calledNoModerator);
        	       	
        	String durationModerator = request.getParameter("callduration");
        	callbackvalues.getDurationModerator(durationModerator);
        	System.out.println("duration_mod : "+durationModerator);        	
		%>
    </c:when>
</c:choose>