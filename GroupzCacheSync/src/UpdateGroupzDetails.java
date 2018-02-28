import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.omg.CORBA.Object;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class UpdateGroupzDetails extends RIJDBBaseThread {

	int threadid;

	public UpdateGroupzDetails(int tid, MongoDatabase session) {
		super(tid, session);
		threadid = tid;
	}

	@Override
	void process(MongoDatabase session) {
		try {
			MongoCollection<Document> updateCollection = session
					.getCollection("updategroupz");
			BasicDBObject whereQuery = new BasicDBObject();
			List<BasicDBObject> list = new ArrayList<BasicDBObject>();
			list.add(new BasicDBObject("proccessedtime", new BasicDBObject(
					"$lte", "lastupdatedtime")));
			list.add(new BasicDBObject("proccessedtime", null));
			whereQuery.put("$or", list);
			System.out.println(whereQuery);
			FindIterable<Document> resultant = updateCollection
					.find(whereQuery);
			MongoCursor<Document> result = resultant.iterator();
			if (result.hasNext()) {
				Document res = result.next();
				String groupzcode = res.getString("groupzcode");
				String groupzbasekey = res.getString("groupzbasekey");
				int memberid = res.getInteger("memberid");
				System.out.println("-------------------------------");
				System.out.println(groupzcode);
				System.out.println(groupzbasekey);
				System.out.println(memberid);
				System.out.println(res.get("lastupdatedtime"));
				System.out.println(res.get("proccessedtime"));
				System.out.println("-------------------------------");
				String url = PropertiesUtil
						.getProperty("update_groupz_details_url");
				JSONObject jObj = new JSONObject();
				JSONObject json = new JSONObject();
				JSONObject request = new JSONObject();
				JSONObject data = new JSONObject();
				data.put("groupzcode", groupzcode);
				request.put("servicetype", PropertiesUtil
						.getProperty("update_groupz_details_servicetye"));
				request.put("functiontype", PropertiesUtil
						.getProperty("update_groupz_details_functiontype"));
				request.put("groupzbasekey", groupzbasekey);
				request.put("memberid", memberid);
				request.put("data", data);
				json.put("request", request);
				jObj.put("json", json);
				System.out.println(jObj);
				String response = ConnectionUtils.ConnectandRecieve(url,
						jObj.toString());
				System.out.println(response);
				JSONObject serverRes = JSONObject.fromObject(response);
				if (serverRes
						.getJSONObject("json")
						.getJSONObject("response")
						.getString("statuscode")
						.equalsIgnoreCase(
								PropertiesUtil
										.getProperty("statuscodesuccessvalue"))) {
					MongoCollection<Document> groupzdetails = session
							.getCollection("groupzdetails");
					System.out.println("-------------------------------");
					JSONObject groupzData = serverRes.getJSONObject("json")
							.getJSONObject("response").getJSONObject("data");
					System.out.println("-------------------------------");
					BasicDBObject where = new BasicDBObject();
					where.put("groupzcode", groupzcode);
					System.out.println("deleting old value");
					System.out.println(where);
					groupzdetails.deleteMany(where);
					System.out.println("deleted");
					System.out.println("-------------------------------");
					if (groupzData.getJSONObject("srsettings").containsKey(
							"issueflowrulelist")) {
						JSONArray issueflowrulelist = groupzData.getJSONObject(
								"srsettings").getJSONArray("issueflowrulelist");
						JSONArray issueflowrulelist1 = new JSONArray();
						groupzData.remove("issueflowrulelist");
						for (int k = 0; k < issueflowrulelist.size(); k++) {
							JSONObject obj = issueflowrulelist.getJSONObject(k);
							obj.remove("$$hashKey");
							issueflowrulelist1.add(obj);
						}
						groupzData.getJSONObject("srsettings").put(
								"issueflowrulelist", issueflowrulelist1);
					}

					groupzData.put("lastUpdatedTime", RestUtil
							.getLastSynchTime().toString());
					System.out
							.println("--------------------------------------------");
					System.out.println(groupzData);
					System.out
							.println("--------------------------------------------");
					Document groupz = new Document(groupzData);
					groupzdetails.insertOne(groupz);
					System.out.println("--" + groupz);
					ObjectId id = (ObjectId) groupz.get("_id");
					if (id != null) {
						// BasicDBObject setQuery = new BasicDBObject();
						// setQuery.put("$set", new
						// BasicDBObject("proccessedTime",RestUtil.getLastSynchTime().toString()));
						// updateCollection.updateOne(whereQuery, setQuery);
						System.out
								.println("--------------------------------------------");
						System.out.println(whereQuery);
						updateCollection.deleteOne(whereQuery);
						System.out.println("delete value from updategroupz");
						System.out
								.println("--------------------------------------------");
						System.out.println("successfully updated!");
					} else {
						BasicDBObject setQuery = new BasicDBObject();
						setQuery.put("$set", new BasicDBObject(
								"proccessedTime", RestUtil.getLastSynchTime()
										.toString()));
						updateCollection.updateOne(whereQuery, setQuery);
						System.out
								.println("successfully updated! last updated time");
					}
				} else {
					System.out.println("Updating Time For Failure");
					BasicDBObject setQuery = new BasicDBObject();
					setQuery.put("$set", new BasicDBObject("proccessedtime",
							RestUtil.getLastSynchTime().toString()));
					System.out.println("whereQuery : " + whereQuery
							+ " setQuery : " + setQuery);
					updateCollection.updateOne(whereQuery, setQuery);
				}
			}
			Thread.sleep(5000);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Exception Occured In Updating GroupzDetails!");
		} finally {

		}
	}
}