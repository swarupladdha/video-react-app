import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mongodb.client.MongoDatabase;

public class GroupzCacheSync {
/*	static String host = "localhost";
	static String port = "27017";
	static String db = "session";*/
	static int noofthreads = 2;

	public static void init() {
		// initialize the static variables here
	}

	public static void main(String[] args) {

		MongoDatabase session = Mongo_Connection.getConnection();
		
		ExecutorService executor = Executors.newFixedThreadPool(noofthreads);
		for (int i = 0; i < noofthreads; i++) {
			Runnable worker1 = new UpdateGroupzDetails(i, session);
			Runnable worker2 = new UpdateMemberDetails(i, session);
			executor.execute(worker1);
//			executor.execute(worker2);
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
			}
		}
		System.out.println("Finished all threads");
	}
}
