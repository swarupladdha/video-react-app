package ivr.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import org.apache.log4j.Logger;

public class GetURLResponse {
	
	public static Logger logger =  Logger.getLogger("recordLogger");
	
	public static String ConnectAndGetResponse(String urlString, String xmlsmsString){
	String resultString=null;
	
	try{
		

		StringBuffer sendStr = new StringBuffer();
		sendStr.append("request=");
		sendStr.append(xmlsmsString);
		String sendString = sendStr.toString();
		System.out.println("request : " + sendString);

		URL url = new URL(urlString);

		URLConnection connection = url.openConnection();

		connection.setDoOutput(true);

		OutputStreamWriter outputWriter = new OutputStreamWriter(
				connection.getOutputStream());
		outputWriter.write(sendString);
		outputWriter.close();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));

		String inputLine;
		StringBuffer responseStr = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			responseStr.append(inputLine);

		}

		in.close();

		resultString = responseStr.toString();

		
	}catch(Exception e){
		e.printStackTrace();
		logger.info("Exception occured in url connection.", e);
		return resultString;
	}
	return resultString;
	}
}
