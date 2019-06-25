

import java.io.IOException;
import java.sql.Connection;
import java.util.Scanner;

import org.apache.log4j.Logger;

import com.stock.csvreader.MyCSVReader;
import com.stock.dao.RecordsDao;
import com.stock.utils.ConnectionManager;

import net.sf.json.JSONArray;


public class MainClass {
	
	public static final Logger logger = Logger.getLogger(MainClass.class);
	
	public static void main(String[] args) throws IOException {
    	ConnectionManager cManager = new ConnectionManager();
    	Connection connection =null;

		// /home/backend/Desktop/csv.csv
		// /home/backend/Downloads/C2ImportUsersSample.csv
		Scanner scanner = new Scanner(System.in);
		try {
			connection = cManager.getConnection();

			logger.info("Choose 1 for entering csv in db.");
			logger.info("select 2 for storing data minutewise");
			int choose= scanner.nextInt();
			scanner.nextLine();
			switch (choose) {
			case 1:
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
				
				break;
			case 2:
				logger.info("Please Enter how many minutes of data u want to store . e.g,5/10/13/15/21/30/34/55");
				int number= scanner.nextInt();
				if(number==5||number==10||number==13||number==15||number==21||number==30||number==34||number==55) {
				RecordsDao rDao = new RecordsDao();
				JSONArray arr = rDao.getDate(connection);
				System.out.println(arr);
				String date;
				rDao.getAllMinute(connection,number);
				for (int i = 0; i < arr.size(); i++) {

					date = arr.getString(i);
					rDao.MaxMinOpenHigh(connection, date,number);
				}
				logger.info("Operation Successful");
				}else {
					logger.info("Invalid Number");
				}
				break;

			default:
				logger.info("please choose an option to proceed");
				break;
			}
			
			scanner.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			cManager.closeConnection(connection);
		}
	}
}