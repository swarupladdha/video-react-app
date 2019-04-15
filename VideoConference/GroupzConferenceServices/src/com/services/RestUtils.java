package com.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.CharacterIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RestUtils {

	
	public boolean isEmpty(String test) {
		if (test == null || test.trim().isEmpty() == true|| test.equalsIgnoreCase("[]") || test == "") {
			return false;
		}
		return true;
	}

	public String encode(String s) {
		if (s == null)
			return null;
		final StringBuilder result = new StringBuilder();
		final StringCharacterIterator iterator = new StringCharacterIterator(s);
		char character = iterator.current();
		while (character != CharacterIterator.DONE) {
			if (character == '<') {
				result.append("&lt;");
			} else if (character == '>') {
				result.append("&gt;");
			} else if (character == '\"') {
				result.append("&quot;");
			} else if (character == '\'') {
				result.append("&#039;");
			} else if (character == '&') {
				result.append("&amp;");
			} else {
				// the char is not a special one
				// add it to the result as is
				result.append(character);
			}
			character = iterator.next();
		}
		return result.toString();
	}
	
	

	public Date getLastSynchTime() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
		f.setTimeZone(TimeZone.getTimeZone("UTC"));
		String utcTime = f.format(new Date());
		Date lastSynch = StringDateToDate(utcTime);
		return lastSynch;
	}

	public static Date StringDateToDate(String StrDate) {
		String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
		Date dateToReturn = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
		try {
			dateToReturn = (Date) dateFormat.parse(StrDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateToReturn;
	}
	
	public String DateToStringDate(Date date) {
		String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
		String dateStr= null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);
		try { 
			dateStr = dateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}
	
	public double calulateCommission(int creditsPerAssessment) {
		double value = -1;
		try {
			int percent_commision=Integer.parseInt(PropertiesUtil.getProperty("percent_commission"));
			value = Math.ceil((creditsPerAssessment*100)/(100-percent_commision));
			return value;
		}catch (Exception e) {
			e.printStackTrace();
			return value;
		}
	}
	
	public String getConcatenatedJSONids(JSONArray array) {
		String concatenatedString = "";
		if (array.size()==1){	
			concatenatedString =   "'"+array.get(0)+"'";
			return concatenatedString;
			}
			for(Object rol:array){
				concatenatedString +=   "'"+rol+"',";
			}
			return concatenatedString.substring(0,concatenatedString.length()-1);
	
	}
	
	@SuppressWarnings("rawtypes")
	public String getConcatenatedInt(List array) {
		String concatenatedString = "";
		if (array.size()>0) {
			if (array.size()==1){
				concatenatedString =   ""+array.get(0);
				return concatenatedString;
			}
			
				for(Object rol:array){
					concatenatedString +=   rol+",";
				}
				
				return concatenatedString.substring(0,concatenatedString.length()-1);
			
			
		}
		else {
			return null;
		}
	}
	
	   public  String createMatchingJobBody (JSONArray jobs) {
		   String jobCount = " job";
		   if (jobs.size()>1) {
			   jobCount = " jobs";
		   }
		   String html ="";
//		   String openBodyTag = "<html><body>";
//		   String closeBodyTag = "</body></html>";
		   String breakTag = "<br/>";
		   String openDivTag="<div style='color:#fff;margin-left:10%'>";
		   String closeDivTag="</div>";
		   String openPTag="</p>";
		   String closePTag="</p>";
//		   html += openBodyTag;
		   html += "<div style='background:purple;margin-left:30%;width:40%;'>";
		   html += "<h3 style='color:yellow;'>"+"We found "+jobs.size()+jobCount+" matching for your profile"+"</h3>"; 
		   html += "<hr style='color:#fff;width:100%;float:left;'/>"+breakTag;
		   for (int i=0; i<jobs.size();i++) {
			   JSONObject obj = jobs.getJSONObject(i);
			   System.out.println(obj.toString());
			   if (i !=0) {
				   html += "<hr style='color:indigo;width:60%;float:left;'/>"+breakTag;
			   }
			   html += openDivTag;
			   html += openPTag;
			   html += obj.getString("jobTitle");
			   html += closePTag;
			   html += breakTag;
			   html += openPTag;
			   html += obj.getString("companyName");
			   html += closePTag;
			   html += breakTag;
			   html += openPTag;
			   html += obj.getString("location");
			   html += closePTag;
			   html += breakTag;
			   html += openPTag;
			   html += obj.getString("experience");
			   html += closePTag;
			   html += breakTag;
			   html += openPTag;
			   html += obj.getString("skill");
			   html += breakTag;
			   html += closePTag;
			   html += closeDivTag;
		   }
//		   html += closeDivTag;
//		   html += closeBodyTag;
		   System.out.println(html);
		   return html;
	   }
	   
		public String readEmailFromHtml(String topFile,String middleFile,String bottomFile, JSONArray inputArray){
			String msg=null;
			try
		    {
			 String jobCount = " job";
			   if (inputArray.size()>1) {
				   jobCount = " jobs";
			   }
			StringBuffer appenedMessage = new StringBuffer();
			for (int i=0; i< inputArray.size(); i++) {
				JSONObject input = inputArray.getJSONObject(i);
				String message = readContentFromFile(middleFile);
				if (message != null) {
					message=message.replace("jobTitle", input.getString("jobTitle"));
					message=message.replace("companyName", input.getString("companyName"));
					message=message.replace("experience", input.getString("experience"));
					message=message.replace("location", input.getString("location"));
					message=message.replace("skill", input.getString("skill"));
					message=message.replace("createdTime", input.getString("createdTime"));
					message=message.replace("jobDescription", input.getString("jobDescription"));
					appenedMessage.append(message);
					//appenedMessage.append(System.getProperty("line.separator"));
				}
			}
			String finalMessage = appenedMessage.toString();
			String bottomMessage = readContentFromFile(bottomFile);

			msg = readContentFromFile(topFile);
		    
		    	msg = msg.replace("count", "We found "+inputArray.size()+jobCount+" matching for your profile");
			    msg = msg.replace("<middle>", finalMessage);
			    msg = msg.replace("<bottom>", bottomMessage);
			    return msg;
		    }
		    catch(Exception exception)
		    {
		        exception.printStackTrace();
		        return msg;
		    }
		    
		}
		
		private String readContentFromFile(String fileName)
		{
		    StringBuffer contents = new StringBuffer();
		    try {
		      //use buffering, reading one line at a time
		      BufferedReader reader =  new BufferedReader(new FileReader(fileName));
		      try {
		        String line = null; 
		        while (( line = reader.readLine()) != null){
		        	contents.append(line);
			       // contents.append(System.getProperty("line.separator"));
		        	}
		      }
		      finally {
		          reader.close();
		      }
		    }
		    catch (IOException ex){
		      ex.printStackTrace();
		    }
		    return contents.toString();
		}
	
}
