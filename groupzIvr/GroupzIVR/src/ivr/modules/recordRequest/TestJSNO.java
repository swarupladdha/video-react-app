package ivr.modules.recordRequest;

import ivr.tables.ContextMapping;
import ivr.tables.RecordTransactions;
import ivr.utils.StaticUtils;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;

import org.apache.log4j.Logger;

public class TestJSNO {


	public static void Createrespobject() {
	String mainMStr  = "Divisions";
	String stackSTr = "1to5th;2~6to8;7~1to5th;2~10~";
	
		try {
			
			
			JSONObject tempJson1 = new JSONObject();
			JSONObject finalReportJson = new JSONObject();
			JSONObject tempJson2 = new JSONObject();
			JSONObject jsnoObj = new JSONObject();
			
			
			String [] tildList = stackSTr.split("~");
			
			List<String> myList = new ArrayList<String>();
			 

			Collections.addAll(myList, tildList); 
			
			HashSet<String> hashSet = new HashSet<String>(myList);
			myList.clear();
			myList.addAll(hashSet);
			
			System.out.println(myList);
			
			for(int i=0;i<myList.size();i++){
				
				String str = myList.get(i);
				if(str.contains(";")){
					
					
					
					String [] semiList = str.split(";");
					
					
						tempJson1.put(semiList[0], semiList[1]);	
					
					
					
					
				}else{
					tempJson2.put("option", str);
				}
				
			
				
			}
			
			jsnoObj.put("selectionList", tempJson2);
			
			jsnoObj.put("groupsList", tempJson1);
			finalReportJson.put(mainMStr, jsnoObj);
			
			System.out.println(finalReportJson);
			
		} catch (Exception e) {
			
			e.printStackTrace();

			

		}
	

	}
	
	
	public static void InsertDataToPublishXML() {
		
		String mainMStr  = "Divisions";
		String stackSTr = "1st,2nd,3rd,4th,3rd,";
		
		
		String kookoourl = "http://test.wav,http://test1.wav";
		String grpzcode = "5676";

		try {

		
			JSONObject finalReportJson = new JSONObject();
			JSONObject subMenuJSONList = new JSONObject();
			JSONObject jsnoObj = new JSONObject();
			if (StaticUtils.isEmptyOrNull(stackSTr) == false) {
			
				String[] subMenuList = stackSTr.split(",");
				ArrayList<String> options= new ArrayList<String>(); 
				List<String> subCollectList = new ArrayList<String>();

				Collections.addAll(subCollectList, subMenuList);
				
				HashSet<String> hashSet = new HashSet<String>(subCollectList);
				
				subCollectList.clear();
				subCollectList.addAll(hashSet);
				
				int j=0;
				for (int i = 0; i < subCollectList.size(); i++) {
					
					String str = subCollectList.get(i);				
					
					subMenuJSONList.put("option"+j, str);
				
					j++;
					

				}
				
				
				
				jsnoObj.put("submenulist", subMenuJSONList);
				
				jsnoObj.put("submenulist", subMenuJSONList);

				jsnoObj.put("mainmenu", mainMStr);
				
				jsnoObj.put("groupzcode", grpzcode);
				
				
				
				finalReportJson.put("report", jsnoObj);

					XMLSerializer serializer = new XMLSerializer();

			JSON jsonadd = JSONSerializer.toJSON(finalReportJson);
				serializer.setRootName("response");
				serializer.setTypeHintsEnabled(false);
				String xmlsmsString = serializer.write(jsonadd);

				// Inserting data to table to download the recorded messages.
				RecordTransactions recTranobj = new RecordTransactions();
				recTranobj.setdatetime(new Date());
				recTranobj.setgroupzCode(grpzcode);
				recTranobj.setkookoourls(kookoourl);
				recTranobj.setselectiondata(finalReportJson.toString());
				recTranobj.setdownloadFlag(true);
				recTranobj.setpublishedFlag(true);
				recTranobj.save();

				System.out.println(xmlsmsString);
				
			}
		

		} catch (Exception e) {

			e.printStackTrace();

		}

	}
	
	public static void CreateArrayJSN(){
		
		
		DateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");

		Calendar c = Calendar.getInstance();    
		c.setTime(new Date());
		c.add(Calendar.DATE, 7);
		System.out.println(dateFormat.format(c.getTime()));
		
		String xmlStr="<xml><request><allusers>false</allusers><data><urlsList><kannada>groupzivr_audio1404972793445.wav</kannada></urlsList></data><divisions><e>1st</e><e>11th</e></divisions><functiontype>2015</functiontype><groupzlist><groupzcode>t100</groupzcode></groupzlist><mobile><countrycode>91</countrycode><mobilenumber>9986067657</mobilenumber></mobile><servicetype>21</servicetype></request></xml>";
		
		XMLSerializer xmlSerializer = new XMLSerializer();
		JSON json = xmlSerializer.read(xmlStr);		
		System.out.println(json);
		
		List<String> subCollectList = new ArrayList<String>();
		
		ArrayList<String> submenuList = new ArrayList<String>();
		subCollectList.add("1st");
		subCollectList.add("11th");
		
		
		JSONObject subMenuArray =new JSONObject();
		JSONObject subMenuArrayList =new JSONObject();
		
		for (int i = 0; i < subCollectList.size(); i++) {

			String str = subCollectList.get(i);
			
			submenuList.add(str);

		}
		subMenuArrayList.put("divisions", submenuList);
	
		
	System.out.println(subMenuArrayList);


		JSONObject roles = new JSONObject();
		JSONObject text = new JSONObject();
		JSONObject dataList = new JSONObject();
		JSONObject audio = new JSONObject();
		JSONObject rolesJSN = new JSONObject();
		JSONObject subMenuJSN = new JSONObject();
		ArrayList<String> arrayList = new ArrayList<String>();
		
		//arrayList.add("welcome to roles");
		//arrayList.add("please select");
		
		//jsnoObj.put("welcomeNotesList", arrayList);
		
		JSONArray arralijsn = new JSONArray();
		
		audio.put("url", "http://test.wav");
		
		dataList.put("1", "student");
		dataList.put("2", "teacher");
		dataList.put("3", "batchgroup1");
		dataList.put("4", "batchgroup2");
		dataList.put("5", "All");
		
		text.put("dataList", dataList);
		
		arralijsn.add("Thanks for calling us");
		arralijsn.add("Please select from the following list");
		
		text.put("welcomenotesList", arralijsn);
		
        JSONArray arralijsn1 = new JSONArray();
		
		arralijsn1.add("Press hash to hangup the call");
		arralijsn1.add("Press 9 for repeating the menu");
		
		text.put("endnotesList", arralijsn1);
		
		
		roles.put("audio", audio);
		roles.put("text", text);
		
		
		JSONObject subRoles = new JSONObject();
		JSONObject subtext = new JSONObject();
		JSONObject subdataList = new JSONObject();
		JSONObject subaudio = new JSONObject();
		JSONObject subrolesJSN = new JSONObject();
		

		
		subaudio.put("url", "http://test.wav");
		
		subdataList.put("1", "admin");
		subdataList.put("2", "All");
		
		
		subtext.put("dataList", subdataList);
		

		
        JSONArray subarralijsn1 = new JSONArray();
		
		subarralijsn1.add("Press hash to hangup the call");
		subarralijsn1.add("Press 9 for repeating the menu");
		
		subtext.put("endnotesList", subarralijsn1);
		subrolesJSN.put("audio", subaudio);
		subrolesJSN.put("text", subtext);
		
		roles.put("batchgroup1", subrolesJSN);
		
	
		rolesJSN.put("roles", roles);
		subMenuJSN.put("subMenuDisplayList", rolesJSN);
		
		System.out.println(subMenuJSN.toString());
		
		
		
		
	}
	
	public static void main(String[] args) {

		TestJSNO.CreateArrayJSN();

	
	}

}