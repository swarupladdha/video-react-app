import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;


public class Mongo_Connection {


	
	static MongoClient mongoClient = null;
	
	public static MongoDatabase getConnection(){
		mongoClient = new MongoClient(PropertiesUtil.getProperty("host_name"),Integer.parseInt(PropertiesUtil.getProperty("port_number")));
		MongoDatabase db = mongoClient.getDatabase(PropertiesUtil.getProperty("db_name"));
		System.out.println("Connection Established!");
		System.out.println("Connected to mongotest DataBase!!");
		return db;
	}
	
	public static void closeConnection(){
		if (mongoClient != null){
			mongoClient.close();
			System.out.println("Connection Closed!");
		}
	}
}