package ivr.modules.inquiryRequest;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import org.apache.log4j.Logger;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

public class SendInquirySms {
	public static Logger logger = Logger.getLogger("inquiryLogger");
	static Properties prop = new Properties();
	static {
		try {

			InputStream in = SendInquirySms.class
					.getResourceAsStream("/ivr.properties");
			prop.load(in);
		} catch (Exception e) {
			logger.error(
					"Exception occured in load property file send sms inquiry.",
					e);
			e.printStackTrace();

		}
	}

	public static void sendinqsms(String smsText, String xmladdress,
			String option) throws Exception {
		System.out.println("sms text " + smsText);
		String address = null;

		if (xmladdress != null && xmladdress.isEmpty() == false) {

			JSONObject dataObj = (JSONObject) JSONSerializer.toJSON(xmladdress);

			JSONObject jsndatalistObj = dataObj.getJSONObject("dataList");

			address = jsndatalistObj.getString(option);
			
			System.out.println(address);
		}

		XMLSerializer xmlSerializer = new XMLSerializer();
		JSON json = xmlSerializer.read(address);
		JSONObject jo = (JSONObject) JSONSerializer.toJSON(json);

		JSONObject joreq = (JSONObject) jo.get("request");
		JSONObject joaddress = (JSONObject) joreq.get("address");

		JSONObject jomessage = (JSONObject) joreq.get("message");

		jomessage.put("shorttext", smsText);

		JSONObject jonNewSmsStr = new JSONObject();
		JSONObject jorequest = new JSONObject();

		jonNewSmsStr.put("message", jomessage);
		jonNewSmsStr.put("address", joaddress);

		jorequest.put("request", jonNewSmsStr);

		XMLSerializer serializer = new XMLSerializer();

		JSON jsonadd = JSONSerializer.toJSON(jorequest);
		serializer.setRootName("xml");
		serializer.setTypeHintsEnabled(false);
		String xmlsmsString = serializer.write(jsonadd);

		System.out.println("new sms xml : " + xmlsmsString);

		String urlString = "http://localhost:8080/GroupzSmsWebProject/grpzsms?";
		String functiontype = prop.getProperty("smsfunctyp");
		StringBuffer sendStr = new StringBuffer();
		sendStr.append("xmlString=");
		sendStr.append(xmlsmsString);
		sendStr.append("&");
		sendStr.append("functiontype=");
		sendStr.append(functiontype);
		String sendString = sendStr.toString();
		sendString = sendString.replace("+", "%2B");
		System.out.println("urlencode : " + sendString);
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
		while ((inputLine = in.readLine()) != null) {
			System.out.println("Response:  " + inputLine);

		}

		in.close();

	}

}
