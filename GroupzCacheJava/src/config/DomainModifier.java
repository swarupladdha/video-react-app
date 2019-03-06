package config;



import org.bson.Document;

import com.connection.Mongo_Connection;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class DomainModifier {
	
	
	public void changeDomain() {
		Mongo_Connection conn = new Mongo_Connection();
		MongoDatabase db = conn.getConnection();
		MongoCollection<Document> col = db.getCollection("authtables");
		
		FindIterable<Document> res = col.find();
		MongoCursor<Document> cursor = res.iterator();
		while (cursor.hasNext()){
			Document doc = cursor.next();
			String uri = doc.getString("uri");
			BasicDBObject query = new BasicDBObject();
			query.put("groupzmodulename", doc.getString("groupzmodulename"));
			System.out.println(uri);
			String[] parts = uri.split("/");
			for(int i=0; i<parts.length; i++){
				System.out.println(parts[i]);
			}
			parts[2]="localhost:8080";
			String s="";
			for(int i=0; i<parts.length; i++){
				System.out.println(parts[i]);
			}
			for(int i=0; i<parts.length; i++){
				s+=parts[i]+"/";
			}
			s = s.substring(0, s.length()-1);
			System.out.println(s);
			BasicDBObject setVal = new BasicDBObject("uri",s);
			BasicDBObject setQuery = new BasicDBObject();
			setQuery.put("$set", setVal);
			col.updateOne(query, setQuery);
			FindIterable<Document> res1 = col.find(query);
			MongoCursor<Document> cursor1 = res1.iterator();
			if(cursor1.hasNext()){
				System.out.println(cursor1.next());
			}
		}
		
		
	}

}
