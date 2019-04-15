

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.services.MergeVideos;
import com.services.PropertiesUtil;

public class GroupzConferenceServices {
	
	static String dbusername = PropertiesUtil.getProperty("dbusername");
	static String dbpassword = PropertiesUtil.getProperty("dbpassword");
	static String dburl = PropertiesUtil.getProperty("dburl");
	static String jdbcDriver = PropertiesUtil.getProperty("jdbcDriver");
	static int noofthreads = Integer.parseInt( PropertiesUtil.getProperty("max_threads"));

	public static void main(String[] args) {

		try {
			Class.forName(jdbcDriver);
		} catch (Exception e) {
		}
		int noOfModules = 1;
		ExecutorService executor = Executors.newFixedThreadPool(noofthreads*noOfModules);
		for (int i = 0; i < noofthreads; i++) {
			
			Runnable worker1 = new MergeVideos(i, dburl, dbusername, dbpassword);
			
			executor.execute(worker1);
			
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


