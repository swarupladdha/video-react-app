<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@page contentType="application/xml" trimDirectiveWhitespaces="true" %>
<%@ page import="ivr.module.ParticipantCallbackValues" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="ivr.utils.DateFormat" %>


<c:choose>
  <c:when test='${param.event == "NewCall"}'>
        <%   
        
        ParticipantCallbackValues callbackvalues = new ParticipantCallbackValues();
	        
	    	String participant_no = request.getParameter("participant_no");
	        
	    	String conference_id = request.getParameter("conference_id");
	    	String startTime = DateFormat.getLatestSynchTime();
	    	
	    	callbackvalues.getstartTimeParticipant(startTime);
	    	System.out.println("startTime : "+startTime);
	        
	    	System.out.println("New call");
	        out.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><conference moderator=\"false\" caller_onhold_music=\"default\" record=\"false\">"+conference_id+"</conference></response>");
	        System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?><response><conference moderator=\"false\" caller_onhold_music=\"default\" record=\"false\">"+conference_id+"</conference></response>");
	        session.setAttribute("state", "conferenceStarted");
        	String conferencestate = (String)request.getSession().getAttribute("state");
        	System.out.println("conferencestate : "+conferencestate);
        %>
    </c:when>
    <c:when test='${(param.event == "Conference" || param.event == "Hangup" || param.event == "Disconnect") || conferencestate == "conferenceStarted"}'>
        <%
        	System.out.println("url response");
        	
        ParticipantCallbackValues callbackvalues = new ParticipantCallbackValues();
        	
        	String sessionIdParticipant = request.getParameter("sid");
        	callbackvalues.getSessionIdParticipant(sessionIdParticipant); 
        	System.out.println("sessionId_par : "+sessionIdParticipant);
        	        	
        	String conferenceIdParticipant = request.getParameter("conference_id");
        	callbackvalues.getconferenceIdParticipant(conferenceIdParticipant); 
        	System.out.println("conferenceId_par : "+conferenceIdParticipant);
        	            
        	String callEndStatusParticipant = request.getParameter("event");
            callbackvalues.getCallstatusParticipant(callEndStatusParticipant);
            System.out.println("status_par : "+callEndStatusParticipant);
                        
            String calledNoParticipant = request.getParameter("called_number");
        	callbackvalues.getcalledNoParticipant(calledNoParticipant);
        	System.out.println("calledNo_par : "+calledNoParticipant);
        	        	       	
        	String durationParticipant = request.getParameter("callduration");
        	callbackvalues.getDurationParticipant(durationParticipant);
        	System.out.println("duration_par : "+durationParticipant);        	
		%>
    </c:when>
</c:choose>