package ivr.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

public class TestURLTimeout {

	public static boolean sendSMS(String formattedMobilenumber, String codemsg) {
		String smsurl = "http://code.groupz.in:8081/GroupzSmsWebProject/grpzsms?";
		boolean flag = true;
		try {

			String xmlsmsString = "<xml><request><datetime></datetime><address><tolist><to><contactpersonname>gnbhat2</contactpersonname><name>payaswini2</name><number>%2B"
					+ "91.9986067657"
					+ "</number><prefix>Mrs</prefix></to></tolist></address><smscost>0.3</smscost><message><shorttext>"
					+ "test"
					+ "</shorttext><sid></sid><provider></provider></message></request></xml>&functiontype=sms";

			StringBuffer sendStr = new StringBuffer();
			sendStr.append("xmlString=");
			sendStr.append(xmlsmsString);
			String sendString = sendStr.toString();
			sendString = sendString.replace("+", "%2B");
			System.out.println("urlencode : " + sendString);

			URL url = new URL(smsurl);

			URLConnection connection = url.openConnection();

			connection.setDoOutput(true);
			connection.setConnectTimeout(50000);

			System.out.println("time out @@" + connection.getConnectTimeout());
			connection.setReadTimeout(80000);

			System.out.println(connection.getConnectTimeout());

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

		} catch (Exception e) {

			flag = false;

		}
		return flag;
	}

	public static void GetuserListandInsertintoUserTable(
			String tragetedSelectionxx, String groupzcode) {

		try {
			
			JSONObject tragetedSelection = new JSONObject(tragetedSelectionxx);
			
			if (tragetedSelectionxx != null) {
				
				System.out.println("TAREGET@@ "+tragetedSelection);

				JSONArray jsnrolelistarry = tragetedSelection
						.getJSONArray("roles");

				if (jsnrolelistarry != null & jsnrolelistarry.length() != 0) {

					for (int i = 0; i < jsnrolelistarry.length(); i++) {

						JSONObject rolenameJson = jsnrolelistarry
								.getJSONObject(i);

						String rolename = rolenameJson.getString("role"); // insert

						JSONArray divisionJsonArraylist = rolenameJson
								.getJSONArray("divisions");

						for (int j = 0; j < divisionJsonArraylist.length(); j++) {

							JSONObject divnameJson = divisionJsonArraylist
									.getJSONObject(j);

							String divisionname = divnameJson
									.getString("division"); // insert

							JSONArray subdivisionJsonlist = divnameJson
									.getJSONArray("subdivisions");
							for (int h = 0; h < subdivisionJsonlist.length(); h++) {

							
							
								JSONObject subdivisnobj = subdivisionJsonlist // insert
										.getJSONObject(h);
								String subdivisionname = subdivisnobj
										.getString("subdivision");

								if (subdivisnobj.optString("users") != null
										&& subdivisnobj.optString("users")
												.isEmpty() == false) {
									ArrayList<String> membernamesList = new ArrayList<String>();
									ArrayList<String> membercodesList = new ArrayList<String>();
									ArrayList<String> profileUrllist = new ArrayList<String>();
									ArrayList<Integer> usermemberidsList = new ArrayList<Integer>();
									JSONArray usersJsonArray = subdivisnobj
											.getJSONArray("users");
									for (int k = 0; k < usersJsonArray.length(); k++) {
									

										JSONObject userjsonobj = usersJsonArray // insert
												.getJSONObject(k);

										String membername = userjsonobj
												.getString("membername");

										int usermemberid = userjsonobj
												.getInt("memberid");

										String membercode = userjsonobj
												.getString("membercode");
										String profileurl=null;
										if (userjsonobj.optString("profileurl") != null
												&& userjsonobj.optString("profileurl")
														.isEmpty() == false) {
										 profileurl = userjsonobj
												.getString("profileurl");
										}
										usermemberidsList.add(usermemberid);
										membercodesList.add(membercode);
										membernamesList.add(membername);
										profileUrllist.add(profileurl);

									}
									System.out.println(rolename + " "
											+ divisionname + " "
											+ subdivisionname + " "
											+ usermemberidsList + " "
											+ membernamesList + " "
											+ profileUrllist);
								
								}

							}
						}

					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	public static void main(String args[]) {
		try {

			String josnstr = "{\"groupzlist\":[{\"shortname\":\"PGZ-Test\",\"groupzcode\":\"PGPZ\"}],\"grouplabel\":\"Select Group\",\"rolelabel\":\"Select Role\",\"groups\":[\"GroupA\",\"GroupB\",\"GroupC\"],\"roles\":[{\"divisionlabel\":\"Select ADIV\",\"role\":\"Admin\",\"divisions\":[{\"subdivisions\":[{\"subdivision\":\"Gaming\"}],\"division\":\"Block\"},{\"subdivisions\":[{\"subdivision\":\"Physics\",\"users\":[{\"enabled\":true,\"membername\":\"Payswini\",\"memberid\":23412,\"profileurl\":\"\\/GroupzMobileApp\\/ProfilePhoto\\/43719\",\"membercode\":\"55\"}]},{\"subdivision\":\"Chemistry\"}],\"division\":\"Department\"},{\"subdivisions\":[{\"subdivision\":\"Accounts\"},{\"subdivision\":\"Utility\"}],\"division\":\"Office\"}],\"subdivisionlabel\":\"Select ASUBDIV\"},{\"divisionlabel\":\"Select Class\",\"role\":\"Studentz\",\"divisions\":[{\"subdivisions\":[{\"subdivision\":\"A\",\"users\":[{\"enabled\":true,\"membername\":\"Aditi\",\"memberid\":23582,\"profileurl\":\"\\/GroupzMobileApp\\/ProfilePhoto\\/43997\",\"membercode\":\"123\"}]},{\"subdivision\":\"B\"}],\"division\":\"I std\"},{\"subdivisions\":[{\"subdivision\":\"D\"},{\"subdivision\":\"E\"}],\"division\":\"II std\"}],\"subdivisionlabel\":\"Select Section\"}],\"groupzlabel\":\"Select Account\"}";

			TestURLTimeout.GetuserListandInsertintoUserTable(josnstr,"SuNG");

			/*
			 * JSONArray groupsArray = new JSONArray();
			 * 
			 * groupsArray.put("TestGroup"); groupsArray.put("SecondGRP");
			 * JSONObject grpUserJson = new JSONObject();
			 * 
			 * 
			 * grpUserJson.put("staged", false); grpUserJson.put("hierarchy",
			 * false); grpUserJson.put("groups", groupsArray);
			 * 
			 * 
			 * 
			 * 
			 * 
			 * 
			 * JSONObject testobj = new JSONObject();
			 * 
			 * testobj.put("key1", 1); testobj.put("updateddate", "");
			 * 
			 * String test = testobj.toString(); JSONObject grpzjson = new
			 * JSONObject(test);
			 * 
			 * System.out.println("word" + grpzjson.optString("updateddate"));
			 * 
			 * if (grpzjson.optString("updateddate") != null &&
			 * grpzjson.optString("updateddate").isEmpty() == false) {
			 * 
			 * String datetime = grpzjson.getString("updateddate");
			 * System.out.println("@@insertupdate " + datetime); }
			 * 
			 * SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
			 * "yyyy-MM-dd HH:mm:ss");
			 * simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC")); Date
			 * myDate = simpleDateFormat.parse("2015-08-31 15:59:57");
			 * System.out.println("Response 1:  " + myDate); SimpleDateFormat
			 * format = new SimpleDateFormat( "E yyyy/MM/dd ',' hh:mm:ss a");
			 * 
			 * String datetime = format.format(myDate);
			 * System.out.println("Response:  " + datetime);
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}
		// boolean flag = TestURLTimeout.sendSMS("91.9986067657", "test");
		// System.out.println("Response:  " + flag);

	}
}
