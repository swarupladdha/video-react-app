package ivr.modules.inquiryRequest;

import ivr.servlets.Inquiry;
import ivr.tables.CallHistory;
import ivr.utils.StaticUtils;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.apache.log4j.Logger;

public class MigrationTasks {
	static Properties prop = new Properties();
	private static Logger logger = Logger.getLogger("inquiryLogger");
	static {

		try {

			System.setProperty("Hibernate-Url",
					Inquiry.class.getResource("/Hibernate.cfg.xml").toString());

			Properties logProperties = null;
			logProperties = new Properties(System.getProperties());

			InputStream inlog = Inquiry.class
					.getResourceAsStream("/log4j.properties");
			logProperties.load(inlog);

			System.out.println("inside log properties" + logProperties);
			logger.info("Log  initialized in Inquiry class ");

			InputStream in = InquiryRequest.class
					.getResourceAsStream("/ivr.properties");

			prop.load(in);

		} catch (Exception e) {
			logger.info(
					"Exception occured in load property files in Inquiry Details list request class.",
					e);
			e.printStackTrace();
		}
	}

	public static String listOfMigration(String functype, int start) {
		String result = null;
		
		boolean migratestatus = false;
		
		JSONObject responseListJson = new JSONObject();
		
		try {
			
			if (functype.equals("migrateTrue")) {
				migratestatus = true;

			}
			String sucesscode = prop.getProperty("sucesscode");
			String rcordsnum = prop.getProperty("noOfrecords");
			int noOfRecs = Integer.parseInt(rcordsnum);

			List<CallHistory> list = (List<CallHistory>) CallHistory
					.listofMigrationRecords(migratestatus, start, noOfRecs);

			JSONObject listOfRecsJson = new JSONObject();
			JSONObject listOfInquiryJson = new JSONObject();
			
			ArrayList<JSONObject> listOfjsonObjects = new ArrayList<JSONObject>();
		

			if (list != null && list.size() > 0) {
				for (CallHistory callhstry : list) {
					
					JSONObject inquiryJson = new JSONObject();
					
					
					int id = callhstry.getId();
					String idstr = Integer.toString(id);
					String contactNumber = callhstry.getcontactNumb();
					String ivrnumber = callhstry.getivrnumber();
					boolean migrationStatus = callhstry.getmigrationStatus();
					String migrateStaturStr = String.valueOf(migrationStatus);
					boolean contactStatus = callhstry.getcontactFlag();
					String contactStatusStr = String.valueOf(contactStatus);
					String grpzcode = callhstry.getgroupzCode();
					String selection = callhstry.getselection();
					Date calltime = callhstry.getdatetime();
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
					String dateStr = df.format(calltime);

					inquiryJson.put("id", idstr);
					inquiryJson.put("contactNumber", contactNumber);
					inquiryJson.put("ivrnumber", ivrnumber);
					inquiryJson.put("migrationStatus", migrateStaturStr);
					inquiryJson.put("contactStatus", contactStatusStr);
					inquiryJson.put("grpzcode", grpzcode);
					inquiryJson.put("selection", selection);
					inquiryJson.put("dateTime", dateStr);
					
					listOfjsonObjects.add(inquiryJson);
			

				}
				listOfInquiryJson.put("inquiryList", listOfjsonObjects);
				listOfRecsJson.put("details", listOfInquiryJson);
				listOfRecsJson.put("statuscode", sucesscode);
				responseListJson.put("response", listOfRecsJson);
			

			} else {
				listOfInquiryJson.put("inquiryList", listOfjsonObjects);
				listOfInquiryJson.put("endOfrecords", "1");
				listOfRecsJson.put("details", listOfInquiryJson);
				listOfRecsJson.put("statuscode", sucesscode);
				responseListJson.put("response", listOfRecsJson);
				
			}
			
			

		} catch (Exception e) {

			e.printStackTrace();
			logger.info("Exception occured in MigrationTasks.", e);

			String resultstr = "Technical Error Occured.";

			String stserror = prop.getProperty("errorcode");

			result = StaticUtils.Createrespobject(resultstr, stserror);
			
			XMLSerializer serializer = new XMLSerializer();
			JSON jsonobj = JSONSerializer.toJSON(responseListJson);
			serializer.setRootName("xml");
			serializer.setTypeHintsEnabled(false);			
			result = serializer.write(jsonobj);
			
			
			return result;

		}
		
		XMLSerializer serializer = new XMLSerializer();
		JSON jsonobj = JSONSerializer.toJSON(responseListJson);
		serializer.setRootName("xml");
		serializer.setTypeHintsEnabled(false);
		serializer.setElementName("inquiry");
		result = serializer.write(jsonobj);
		
		
		return result;
	}
	
	


	public static String UpdateMigratedList(String data) {
		
		String result = null;
		try{
			XMLSerializer xmlSerializer = new XMLSerializer();
			JSON json = xmlSerializer.read(data);
			JSONObject jo = (JSONObject) JSONSerializer.toJSON(json);

			JSONObject joreq = (JSONObject) jo.get("request");
		
			String idlistStr = "";
			Object objprov = joreq.get("idlist");

			if (objprov instanceof JSONArray) {
				JSONArray jsnidarray = joreq
						.getJSONArray("idlist");
				for (int i = 0; i < jsnidarray.size(); i++) {
					
					String id =  jsnidarray.getString(i);
					
				//	String id = idJson.getString("id");
					idlistStr = idlistStr + id + ",";
					
				}
				idlistStr = idlistStr.substring(0, idlistStr.length()-1);
				List <CallHistory> listrecs = (List <CallHistory>)CallHistory.lisofrecordsforUpdate(idlistStr);
				for(CallHistory callhistryrec : listrecs){
					callhistryrec.setmigrationStatus(true);
					callhistryrec.save();
				}
				
			}else{
				JSONObject idJson = joreq.getJSONObject("idlist");
				String idstr = idJson.getString("id");
				int  id = Integer.parseInt(idstr);
				CallHistory callhistryRec = CallHistory.getSingleCallHistory(id);
				callhistryRec.setmigrationStatus(true);
				callhistryRec.save();
			}
			
			
			String resultstr = "Sucessfully updated";

			String stsucess = prop.getProperty("sucesscode");

			result = StaticUtils.Createrespobject(resultstr, stsucess);
			
			
		}
		
		catch (Exception e) {

			e.printStackTrace();
			logger.info("Exception occured in MigrationTasks.", e);

			String resultstr = "Technical Error Occured.";

			String stserror = prop.getProperty("errorcode");

			result = StaticUtils.Createrespobject(resultstr, stserror);
			return result;

		}
		return result;
	}

}
