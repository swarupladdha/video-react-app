import java.io.IOException;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.stocking.reader.MyCSVReader;
import com.stocking.utils.PropertiesUtil;

public class MainClass {
	

	public static final Logger logger = Logger.getLogger(MainClass.class);
	
	public static void main(String[] args) throws IOException {
		// /home/backend/Desktop/csv.csv
		// /home/backend/Downloads/C2ImportUsersSample.csv
		Scanner scanner = new Scanner(System.in);
		try {
			logger.info("Please enter ticker name :");
			String ticker = scanner.nextLine();
			if(ticker == null || ticker.equalsIgnoreCase("")) {
				logger.info("Please re-run the programm with ticker name :");
			}
			else {
				logger.info("Please enter csv file name with path : e.g. /home/ubuntu/sample.csv");
				String fileName = scanner.nextLine();
				if(fileName == null || fileName.equalsIgnoreCase("")) {
					logger.info("Please re-run the programm with filename name :");
				}
				else {
					logger.info("Please wait while we are updating records");
					MyCSVReader csvReader = new MyCSVReader();
					csvReader.readCsvAndDisplayOutput(ticker.trim(),fileName.trim());
					logger.info("Records are updated successfully!");
				}
			}
			scanner.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
