package ivr.modules.recordRequest;

import ivr.tables.RecordTransactions;
import ivr.utils.StaticUtils;
import java.io.*;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;
import org.apache.log4j.Logger;

public class DownloadURLAndPublishRecordXML implements Runnable {
	public static Logger logger = Logger.getLogger("recordLogger");
	static Properties prop = new Properties();
	private int id = 0;
	static {

		try {
			System.setProperty(
					"Hibernate-Url",
					DownloadURLAndPublishRecordXML.class.getResource(
							"/Hibernate.cfg.xml").toString());
			InputStream in = DownloadURLAndPublishRecordXML.class
					.getResourceAsStream("/ivr.properties");
			prop.load(in);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public DownloadURLAndPublishRecordXML(int id) {
		this.id = id;

	}

	public void run() {
		
		int threadId = this.id;
		
		String modVal = prop.getProperty("thread_pool_size");
		String urltoinvoke = prop.getProperty("publishXMLURL");
		String urltoupload = prop.getProperty("uploadFileURL");
		

		while (true) {
			try {
				// Get list of records which are not downloaded.(recorded messages  not been downloaded from kookoo and copied to groupz server).
				List<RecordTransactions> listResult = RecordTransactions
						.listdownloadWav(threadId, modVal);

				if (listResult != null) {

					if (listResult.size() != 0) {

						for (RecordTransactions recObj : listResult) {

							String jsonurlsStr = recObj.getkookoourls();
							if (StaticUtils.isEmptyOrNull(jsonurlsStr) == false) {

								JSONObject urlobj = new JSONObject();

								JSONObject dataListObj = new JSONObject();

								JSONObject dataObj = (JSONObject) JSONSerializer
										.toJSON(jsonurlsStr);

								JSONObject jsndatalistObj = dataObj
										.getJSONObject("urlList");

								Iterator<?> keys = jsndatalistObj.keys();

								

								MultiFileUpload multipart = new MultiFileUpload(
										urltoupload);
								
								
								
								while (keys.hasNext()) {

									String key = (String) keys.next();
									String url = jsndatalistObj.getString(key);

									InputStream inputstream = new URL(url)
											.openStream();

									String recfilename = prop
											.getProperty("recfilename");

									String uniquefileName = recfilename
											+ +new Date().getTime() + ".wav";

									multipart.addFilePart("fileUpload",
											inputstream, uniquefileName);

									urlobj.put(key, uniquefileName);
								

								}

								
								String resultString = multipart.finish();

								System.out.println("SERVER REPLIED: "
										+ resultString);
								
								if(resultString!=null && StaticUtils.isEmptyOrNull(resultString)==false){
								
								
								JSONObject responseListobj = new JSONObject();

								JSONObject jsnrespObj = (JSONObject) JSONSerializer
										.toJSON(resultString);

								Iterator<?> filekeys = jsnrespObj.keys();

								while (filekeys.hasNext()) {

									String filekey = (String) filekeys.next();
									String idval = jsnrespObj
											.getString(filekey);
									String newUrl =  idval;
									responseListobj.put(filekey, newUrl);
								}
								JSONObject finalUrlsListobj = new JSONObject();

								Iterator<?> responsekeys = urlobj.keys();

								while (responsekeys.hasNext()) {
									String keyname = (String) responsekeys.next();
									String value = urlobj
											.getString(keyname);
									String newurl = responseListobj
											.getString(value);
									finalUrlsListobj.put(keyname, newurl);
								}

								dataListObj.put("urlsList", finalUrlsListobj);

								String newJUrlString = dataListObj.toString();

								recObj.setgroupzurls(newJUrlString);
								recObj.setdownloadFlag(true);
								recObj.save();

								
								//create publish xml for the server
								// publish part

								String endDate  = recObj.getendDateString();
								String xmldata = recObj.getselectiondata();
								String urlsList = recObj.getgroupzurls();
								String groupzCode = recObj.getgroupzCode();

								JSONObject xmldataObj = (JSONObject) JSONSerializer
										.toJSON(xmldata);

								JSONObject reportObj = xmldataObj
										.getJSONObject("request");

								JSONObject urlsObj = (JSONObject) JSONSerializer
										.toJSON(urlsList);

								JSONObject uploadurlsObj = urlsObj
										.getJSONObject("urlsList");

								JSONObject urlListobj = new JSONObject();

								urlListobj.put("urlslist", uploadurlsObj);
								urlListobj.put("enddate", endDate);
								reportObj.put("data", urlListobj);
							

								JSONObject finalreportObj = new JSONObject();
								finalreportObj.put("request", reportObj);
								
								JSONObject finalJsonObj = new JSONObject();
								finalJsonObj.put("json", finalreportObj);
								
								System.out.println("jsnstring:@@@@@"+finalJsonObj.toString());

								String publishJsonString = finalJsonObj.toString();
								
								recObj.setpublishJSON(publishJsonString);
								recObj.save();
								
								

								boolean publishStatusFlag = false;												
								
								if (StaticUtils.isEmptyOrNull(publishJsonString) == false) {
									
									String responseJSONString = StaticUtils.ConnectAndGetResponse(urltoinvoke,
											publishJsonString);
									
									System.out.println("response :" +responseJSONString);
									//Get status code for publishing.
									publishStatusFlag=	StaticUtils.getJSONStatusCode(responseJSONString,groupzCode);


									if (publishStatusFlag) {
										recObj.setpublishedFlag(true);
										recObj.save();
									} else {
										recObj.setpublishedFlag(false);
										int retryCounter = recObj
												.getRetryCounter();
										retryCounter = retryCounter + 1;
										recObj.setRetryCounter(retryCounter);
										recObj.save();
									}

								}
								
							}
								
							} else {
								logger.info("kookoourl list null groupzCode: "
										+ recObj.getgroupzCode());
								recObj.setdownloadFlag(false);
								recObj.setpublishedFlag(false);
								recObj.save();
							}
						}

					} else {
						Thread.sleep(6000);
						Thread.yield();
					}
				} else {
					Thread.sleep(6000);
					Thread.yield();
				}

			} catch (Exception e) {
				e.printStackTrace();
				logger.info("Error occured while downloading url list and the thread is interrupted.:");

			}
		}
	}

	public static void main(String[] args) {
		DownloadURLAndPublishRecordXML smsobj = new DownloadURLAndPublishRecordXML(
				8);
		Thread newthrd = new Thread(smsobj);
		newthrd.run();
	}

}