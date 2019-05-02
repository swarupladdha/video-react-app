package com.config;

public class BackUpPlan {

	/*
	 * static int apiKey = Integer.parseInt(PropertiesUtil.getProperty("apiKey"));
	 * // YOUR API KEY static String apiSecret =
	 * PropertiesUtil.getProperty("apiSecret"); public static void main(String[]
	 * args) { Scanner s = new Scanner(System.in);
	 * System.out.println("Please enter session id :"); String sid = s.next();
	 * BackUpPlan p = new BackUpPlan(); String res = p.getArchiveUrl(sid);
	 * 
	 * if (res != null ) { System.out.println("The url is :");
	 * System.out.println(res); Test t = new Test(); // res=
	 * "https://s3.amazonaws.com/tokbox.com.archive2/46128652/b86e9db4-330f-4ab9-8b1f-2906e2c6f055/archive.mp4?AWSAccessKeyId=AKIAI6LQCPIXYVWCQV6Q&Expires=1537971910&Signature=iQHyut0Y6pR3USG5wplMSfyBggo%3D";
	 * System.out.println(res); t.connectAndGet(res); } else {
	 * System.out.println("achive url recieved is null"); } s.close(); }
	 * 
	 * public String getArchiveUrl(String sessionId) { String eduResponse=""; try{
	 * //String sessionId =RestUtils.getJSONStringValue(data, "sessionId");
	 * System.out.println(
	 * "--------------------------------------------------------------");
	 * System.out.println(sessionId); System.out.println(
	 * "--------------------------------------------------------------");
	 * System.out.println(RestUtils.isEmpty(sessionId) == false); if (
	 * RestUtils.isEmpty(sessionId) == false ) { eduResponse =
	 * RestUtils.processError(PropertiesUtil.getProperty("sessionid_isempty_code"),
	 * PropertiesUtil.getProperty("sessionid_isempty_message")); return eduResponse;
	 * } System.out.println("apikey= "+apiKey+" and apisecret = "+apiSecret);
	 * OpenTok opentok = new OpenTok(apiKey, apiSecret); List<Archive> archives =
	 * opentok.listArchives(); String archiveUrl = ""; String archiveId=""; String
	 * archiveName=""; int archiveDuration=0; int archiveSize=0; long
	 * archiveCreatedAt=0; Status archiveStatus = null;
	 * System.out.println("--------------------------------------"); for (int i = 0;
	 * i < archives.size(); i++) {
	 * System.out.println("--------------------------------");
	 * System.out.println(archives.get(i).getSessionId());
	 * System.out.println(sessionId);
	 * System.out.println(" match is "+archives.get(i).getSessionId().equals(
	 * sessionId)); System.out.println("--------------------------------");
	 * if(archives.get(i).getSessionId().equals(sessionId)){ archiveId =
	 * archives.get(i).getId(); archiveUrl= archives.get(i).getUrl();
	 * archiveDuration = archives.get(i).getDuration(); archiveSize=
	 * archives.get(i).getSize(); archiveName= archives.get(i).getName();
	 * archiveCreatedAt= archives.get(i).getCreatedAt(); archiveStatus=
	 * archives.get(i).getStatus();
	 * System.out.println("archieve exists id : "+archiveId);
	 * System.out.println("archieve exists archiveUrl : "+archiveUrl);
	 * System.out.println("archieve exists archiveName : "+archiveName);
	 * System.out.println("archieve exists getDuration : "+archives.get(i).
	 * getDuration());
	 * System.out.println("archieve exists getSize : "+archives.get(i).getSize());
	 * System.out.println("archieve exists getCreatedAt : "+archives.get(i).
	 * getCreatedAt());
	 * System.out.println("status is "+archives.get(i).getStatus());
	 * 
	 * System.out.println("======================");
	 * System.out.println(archives.get(i)); break; } }
	 * System.out.println("--------------------------------------");
	 * System.out.println(archives);
	 * System.out.println("archive url is :"+archiveUrl);
	 * 
	 * JSONObject responsedata = new JSONObject(); responsedata.put("archiveurl",
	 * archiveUrl); responsedata.put("archiveid", archiveId);
	 * responsedata.put("archiveDuration", archiveDuration);
	 * responsedata.put("archiveSize", archiveSize);
	 * responsedata.put("archiveCreatedAt", archiveCreatedAt);
	 * responsedata.put("archiveStatus", archiveStatus); // eduResponse =
	 * RestUtils.processSucess(serviceType,functionType,responsedata,""); // return
	 * eduResponse; if(RestUtils.isEmpty(archiveUrl)) { return archiveUrl; } else {
	 * return null; } } catch(Exception e) { e.printStackTrace(); } eduResponse =
	 * RestUtils.processError(PropertiesUtil.getProperty("XMLRequest_code"),
	 * PropertiesUtil.getProperty("XMLRequest_message")); return eduResponse;
	 * 
	 * }
	 */
	


}
