<%@page import="java.util.Enumeration"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.InputStreamReader"%>
<%@page import="java.net.HttpURLConnection"%>
<%@page import="java.net.URL"%>

<%@page import="java.util.Properties" %>
<%@page import="java.io.InputStream" %>
<%@page import="org.apache.log4j.Logger" %>
<%@page import="java.util.ResourceBundle" %>

<%@page import="net.sf.json.JSONObject"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
	
	<%!
	private static Logger logger = Logger.getLogger("serviceLogger");
	//static Properties prop = new Properties();
	ResourceBundle resource = ResourceBundle.getBundle("payu");
	
	
	public void PayUParameters(String transactionId, String state, String mihpayid, String bank)
    {		
			try{    
				System.out.println("TransactionId : "+transactionId);
				System.out.println("STATUS : "+state);
				int status = 0;
				if(state.equals("success")) {
					status = 2;
				} else if (state.equals("failure")) {
					status = 3;
				}
				
			
				JSONObject mainObj = new JSONObject();
				JSONObject jsonObj = new JSONObject();
				JSONObject reqObj = new JSONObject();
				JSONObject dataObj = new JSONObject();
				dataObj.put("transactionid", transactionId);
				dataObj.put("status", status);
				dataObj.put("mihpayid", mihpayid);
				dataObj.put("issuingbank", bank);
				reqObj.put("servicetype", resource.getString("paymentGateway_failed_serviceType"));
				reqObj.put("functiontype", resource.getString("failed_payment_functionType"));
				reqObj.put("data", dataObj);
				jsonObj.put("request", reqObj);
				mainObj.put("json", jsonObj);
				URL url=new URL(resource.getString("failureurl")+mainObj);
				System.out.println("Json request "+jsonObj.toString());
				//{\"json\":{\"request\":{\"servicetype\":\"800\",\"functiontype\":\"80002\",\"data\":{\"transactionid\":"+transactionId+",\"status\":"+status+",\"mihpayid\": "+mihpayid+", \"issuingbank\": "+bank+"}}}}");    
				System.out.println("URL : "+url);
				HttpURLConnection huc=(HttpURLConnection)url.openConnection();  
				huc.setReadTimeout(15*1000);
				huc.connect();
			    BufferedReader reader = new BufferedReader(new InputStreamReader(huc.getInputStream()));
			    StringBuilder  stringBuilder = new StringBuilder();

			     String line = null;
			     while ((line = reader.readLine()) != null)
			     {
			        stringBuilder.append(line + "\n");
			     }
			     System.out.println("Response Recvd : " + stringBuilder ) ;
				 reader.close();
				 
			} catch(Exception e){System.out.println(e);}    
	} 
	%>

<%
	Enumeration<String> kayParams = request.getParameterNames();
	String result = "";
	String key = "";
	while (kayParams.hasMoreElements()) {
		key = (String) kayParams.nextElement();
		result += key + " = " + request.getParameter(key) + (kayParams.hasMoreElements() ? ", " : "");
	}
	/* System.out.println(" Key : "+key+" ");
	out.print(" Key : "+key+" "); */
	System.out.println(" result : "+result);
	/* out.println(" result : "+result); */
	System.out.println(result.getClass().getName());
	String[] responseList = result.split(",");
	System.out.println(responseList);
	String transactionId = "";
	String status = "";
	String mihpayid = "testing";
	String bank = "testing";
	
	for(int i=0; i < responseList.length; i++) {
		String iterated = responseList[i];
		//System.out.println(iterated.getClass().getName());
		String[] splittedValue = iterated.split(" = ");
		//System.out.println(iterated);
		//System.out.println("****"+splittedValue[0]+"****");
		//System.out.println(splittedValue[0].getClass().getName());
		//System.out.println("&&&&&&"+splittedValue[0].trim()+"&&&&&&");
		if(splittedValue[0].trim().equalsIgnoreCase("txnid")) {
			//System.out.println("Txnid.....");
			System.out.println("Txnid : "+splittedValue[1]);
			transactionId = splittedValue[1].toString();
		}
		
		if(splittedValue[0].trim().equalsIgnoreCase("status")) {
			System.out.println("Status : "+splittedValue[1]);
			status = splittedValue[1].toString();
		}
		
		if(splittedValue[0].trim().equalsIgnoreCase("mihpayid")) {
			System.out.println("mihpayid : "+splittedValue[1]);
			mihpayid = splittedValue[1].toString();
		}
		
		if(splittedValue[0].trim().equalsIgnoreCase("issuing_bank")) {
			System.out.println("bank : "+splittedValue[1]);
			bank = splittedValue[1].toString();
		}
	}
	    
	if (status.trim().equals("failure")) {
		String state = "failure"; 
		PayUParameters(transactionId, state, mihpayid, bank);
		String redirectURL = resource.getString("failureWebUrl");
	    response.sendRedirect(redirectURL);
	}
	
%>

