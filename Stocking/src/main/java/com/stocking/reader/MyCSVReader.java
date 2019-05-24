package com.stocking.reader;

import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.text.SimpleDateFormat;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.log4j.Logger;

import com.stocking.dao.RecordsDao;
import com.stocking.utils.ConnectionManager;

import net.sf.json.JSONObject;

public class MyCSVReader {
	public static final Logger logger = Logger.getLogger(MyCSVReader.class);
	SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
	RecordsDao rDao = new RecordsDao();
	Reader reader;
    
    public void readCsvAndDisplayOutput(String ticker,String path) {
    	
    	int rowCount=0;
    	ConnectionManager cManager = new ConnectionManager();
    	Connection connection = null;
		try {
			Path filePath = Paths.get(path);
			if(!filePath.isAbsolute()) {
				logger.info("Please re-run the program with path : e.g. /home/ubuntu/sample.csv");
			}
			else {
				connection = cManager.getConnection();
				reader = Files.newBufferedReader(filePath);
				CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
				for (CSVRecord csvRecord : csvParser) {
					  if(rowCount>0) {
						  String date = sdf1.format(sdf.parse(csvRecord.get(0)));
						  String open =csvRecord.get(1);
						  String close =csvRecord.get(4);
						  String high =csvRecord.get(2);
						  String low =csvRecord.get(3);
						  String adjustClose =csvRecord.get(5);
						  String volume=csvRecord.get(6); 
//							System.out.println(ticker);
//							System.out.println(date);
//							System.out.println(open);
//							System.out.println(close);
//							System.out.println(high);
//							System.out.println(low);
//							System.out.println(adjustClose);
//							System.out.println(volume);
//							System.out.println("-------------\n");
							int id = rDao.getRevordId(connection, date, ticker);
							JSONObject obj = new JSONObject();
							obj.put("ticker", ticker);
							obj.put("date", date);
							obj.put("open", open);
							obj.put("close", close);
							obj.put("high", high);
							obj.put("low", low);
							obj.put("adjustclose", adjustClose);
							obj.put("volume", volume);
							if(id <=0) {
								rDao.insertIntoRecords(connection, obj);
							}
							else {
								rDao.updateRecord(connection,obj);
							}
					  }
					  
					  rowCount++;
	            }
				csvParser.close();
			}
		}catch (NoSuchFileException e) {
			logger.info("No such file "+path);
			logger.info("Please re-run the program with path : e.g. /home/ubuntu/sample.csv");
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			cManager.closeConnection(connection);
		}
    }
}
