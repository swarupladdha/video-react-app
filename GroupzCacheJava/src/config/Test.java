package config;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class Test {
	
	public static void main(String[] args) {
		MongoClient mongoClient = new MongoClient("localhost",27017);
		MongoDatabase db = mongoClient.getDatabase("session");
		MongoCollection<Document> collection = db.getCollection("groupzdetails");
		BasicDBObject query = new BasicDBObject ("groupzid",221);
		
		FindIterable<Document> r = collection.find(query);
		MongoCursor<Document> re = r.iterator();
		if(re.hasNext()){
			System.out.println("Present");
		}
		else {
			System.out.println("not present");
		}
	}
}
