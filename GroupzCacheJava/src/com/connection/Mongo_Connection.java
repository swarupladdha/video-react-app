package com.connection;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.utils.PropertiesUtil;

public class Mongo_Connection {


	
	static MongoClient mongoClient = null;
	
	public static MongoDatabase getConnection(){
		mongoClient = new MongoClient(PropertiesUtil.getProperty("host_name"),Integer.parseInt(PropertiesUtil.getProperty("port_number")));
		MongoDatabase db = mongoClient.getDatabase(PropertiesUtil.getProperty("db_name"));
//		System.out.println(PropertiesUtil.getProperty("host_name"));
//		System.out.println(Integer.parseInt(PropertiesUtil.getProperty("port_number")));
//		System.out.println(PropertiesUtil.getProperty("db_name"));
		System.out.println("Connection Established!");
		System.out.println("Connected to mongotest DataBase!");
		return db;
	}
	
	public static void closeConnection(){
		if (mongoClient != null){
			mongoClient.close();
			System.out.println("Connection Closed!");
		}
	}
	
	
//	public static Double getNextSequence(MongoDatabase db , String name) throws Exception{
//		System.out.println("ABC");
//	    MongoCollection<Document> collection = db.getCollection("counters");
//	    BasicDBObject find = new BasicDBObject();
//	    find.put("_id", name);
//	    BasicDBObject update = new BasicDBObject();
//	    update.put("$inc", new BasicDBObject("seq", 1));
//	    Document obj =  collection.findOneAndUpdate(find, update);
//	    System.out.println("XYZ");
//	    
//	    return obj.getDouble("seq");
//	}


}