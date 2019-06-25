package com.stock.csvreader;

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

import com.stock.dao.RecordsDao;
import com.stock.utils.ConnectionManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class MyCSVReader {
	public static final Logger logger = Logger.getLogger(MyCSVReader.class);
	//SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
	/*
	 * SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy"); SimpleDateFormat
	 * sdf1 = new SimpleDateFormat("yyyy/MM/dd");
	 */
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
	
	SimpleDateFormat tme = new SimpleDateFormat("hh:mm");
	SimpleDateFormat tme1 = new SimpleDateFormat("hh:mm:ss");

	
	RecordsDao rDao = new RecordsDao();
	Reader reader;
    
    public void readCsvAndDisplayOutput(String ticker,String path) {
    	
    	int rowCount=0;
    	ConnectionManager cManager = new ConnectionManager();
    	Connection connection = null;
    	double dopen=0;
    	double dhigh=0;
    	double dlow=0;
    	double dclose=0;
		/*
		 * long lvolume=0; double drsi=0; double dnorRsi=0; double dmacd=0; double
		 * dnormacd=0; double dexp=0; double dmacdHis=0; double dwillium=0; double
		 * dnorWillium=0; double dbUpperband=0; double dnorbUpperband=0; double
		 * dmiddleBand=0; double dnorMiddleBand=0; double dlowerBand=0; double
		 * dnorLowerBand=0; double dnorBoillingerBand=0; double dslowK=0; double
		 * dnorSlowK=0; double dslowD=0; double dnorSlowD=0; double dnorStocastic=0;
		 * double dADX=0; double dnorADX=0; double dtotalContribution=0;
		 */
    	String date = null;
    	String time =null;
    	//JSONObject obj = new JSONObject();
    	
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
						  JSONArray arr = new JSONArray();
						  JSONObject obj1 = new JSONObject();
						  obj1.put("name","ticker");
						  obj1.put("value",ticker);
						  arr.add(obj1);
						  try {
							  date = sdf1.format(sdf.parse(csvRecord.get(0)));
						  if(date==null || date.equalsIgnoreCase("") || date.equalsIgnoreCase("null")) {
							  System.out.println("Empty or null data");
						  }else {
							  JSONObject obj = new JSONObject();
							  obj.put("name","date");
							  obj.put("value",date);
							  arr.add(obj);
						  }
						  
						  time = tme1.format(tme.parse(csvRecord.get(1)));
						  if(time==null || time.equalsIgnoreCase("") || time.equalsIgnoreCase("null")) {
							  System.out.println("Empty or null data");
						  }else {
							  JSONObject obj = new JSONObject();
							  obj.put("name","time");
							  obj.put("value",time);
							  arr.add(obj);
						  }
						  String open =csvRecord.get(2);
						  if(open==null || open.equalsIgnoreCase("") || open.equalsIgnoreCase("null")) {
							  System.out.println("Empty or null data");
						  }else {
							  JSONObject obj = new JSONObject();
							  dopen = Double.parseDouble(open);
							  obj.put("name","open");
							  obj.put("value",dopen);
							  arr.add(obj);
						  }
						  
						  String high =csvRecord.get(3);
						  if(high==null || high.equalsIgnoreCase("") || high.equalsIgnoreCase("null")) {
							  System.out.println("Empty or null data");
						  }else {
							  dhigh = Double.parseDouble(high);
							  JSONObject obj = new JSONObject();
							  obj.put("name","high");
							  obj.put("value",dhigh);
							  arr.add(obj);
							  }
						  
						  String low =csvRecord.get(4);
						  if(low==null || low.equalsIgnoreCase("") || low.equalsIgnoreCase("null")) {
							  System.out.println("Empty or null data");
						  }else {
							  dlow = Double.parseDouble(low);
							  JSONObject obj = new JSONObject();
							  obj.put("name","low");
							  obj.put("value",dlow);
							  arr.add(obj);
							  }
						  
						  String close =csvRecord.get(5);
						  if(close==null || close.equalsIgnoreCase("") || close.equalsIgnoreCase("null")) {
							  System.out.println("Empty or null data");
						  }else {
							  dclose = Double.parseDouble(close);
							  JSONObject obj = new JSONObject();
							  obj.put("name","close");
							  obj.put("value",dclose);
							  arr.add(obj);
							  }
						  
							/*
							 * String volume =csvRecord.get(5); if(volume==null ||
							 * volume.equalsIgnoreCase("") || volume.equalsIgnoreCase("null")) {
							 * System.out.println("Empty or null data"); }else { lvolume =
							 * Long.parseLong(volume); JSONObject obj = new JSONObject();
							 * obj.put("name","volume"); obj.put("value",lvolume); arr.add(obj); }
							 * 
							 * String rsi =csvRecord.get(6); if(rsi==null || rsi.equalsIgnoreCase("") ||
							 * rsi.equalsIgnoreCase("null") ) { System.out.println("Empty or null data");
							 * }else { drsi = Double.parseDouble(rsi); JSONObject obj = new JSONObject();
							 * obj.put("name","rsi"); obj.put("value",drsi); arr.add(obj); }
							 * 
							 * String norRsi =csvRecord.get(7); if(norRsi==null ||
							 * norRsi.equalsIgnoreCase("") || norRsi.equalsIgnoreCase("null")) {
							 * System.out.println("Empty or null data"); }else { dnorRsi =
							 * Double.parseDouble(norRsi); JSONObject obj = new JSONObject();
							 * obj.put("name","Normalised_RSI"); obj.put("value",dnorRsi); arr.add(obj); }
							 * 
							 * String macd =csvRecord.get(8); if(macd==null || macd.equalsIgnoreCase("") ||
							 * macd.equalsIgnoreCase("null")) { System.out.println("Empty or null data");
							 * }else { dmacd = Double.parseDouble(macd); JSONObject obj = new JSONObject();
							 * obj.put("name","macd"); obj.put("value",dmacd); arr.add(obj); }
							 * 
							 * String normacd =csvRecord.get(9); if(normacd==null ||
							 * normacd.equalsIgnoreCase("") || normacd.equalsIgnoreCase("null")) {
							 * System.out.println("Empty or null data"); }else { dnormacd =
							 * Double.parseDouble(normacd); JSONObject obj = new JSONObject();
							 * obj.put("name","Normalised_MACD"); obj.put("value",dnormacd); arr.add(obj); }
							 * 
							 * String exp =csvRecord.get(10); if(exp==null || exp.equalsIgnoreCase("") ||
							 * exp.equalsIgnoreCase("null")) { System.out.println("Empty or null data");
							 * }else { dexp = Double.parseDouble(exp); JSONObject obj = new JSONObject();
							 * obj.put("name","exp"); obj.put("value",dexp); arr.add(obj); }
							 * 
							 * String macdHis =csvRecord.get(11); if(macdHis==null ||
							 * macdHis.equalsIgnoreCase("") || macdHis.equalsIgnoreCase("null")) {
							 * System.out.println("Empty or null data"); }else { dmacdHis =
							 * Double.parseDouble(macdHis); JSONObject obj = new JSONObject();
							 * obj.put("name","MACD_Histogram"); obj.put("value",dmacdHis); arr.add(obj); }
							 * 
							 * String willium =csvRecord.get(12); if(willium==null ||
							 * willium.equalsIgnoreCase("") || willium.equalsIgnoreCase("null")) {
							 * System.out.println("Empty or null data"); }else { dwillium =
							 * Double.parseDouble(willium); JSONObject obj = new JSONObject();
							 * obj.put("name","willium"); obj.put("value",dwillium); arr.add(obj); }
							 * 
							 * String norWillium =csvRecord.get(13); if(norWillium==null ||
							 * norWillium.equalsIgnoreCase("") || norWillium.equalsIgnoreCase("null")) {
							 * System.out.println("Empty or null data"); }else { dnorWillium =
							 * Double.parseDouble(norWillium); JSONObject obj = new JSONObject();
							 * obj.put("name","Normalised_willium"); obj.put("value",dnorWillium);
							 * arr.add(obj); }
							 * 
							 * String bUpperband =csvRecord.get(14); if(bUpperband==null ||
							 * bUpperband.equalsIgnoreCase("") || bUpperband.equalsIgnoreCase("null")) {
							 * System.out.println("Empty or null data"); }else { dbUpperband =
							 * Double.parseDouble(bUpperband); JSONObject obj = new JSONObject();
							 * obj.put("name","Boillinger_UpperBand"); obj.put("value",dbUpperband);
							 * arr.add(obj); }
							 * 
							 * String norbUpperband =csvRecord.get(15); if(norbUpperband==null ||
							 * norbUpperband.equalsIgnoreCase("") || norbUpperband.equalsIgnoreCase("null"))
							 * { System.out.println("Empty or null data"); }else { dnorbUpperband =
							 * Double.parseDouble(norbUpperband); JSONObject obj = new JSONObject();
							 * obj.put("name","Normalised_Boillinger_UpperBand");
							 * obj.put("value",dnorbUpperband); arr.add(obj); }
							 * 
							 * String middleBand =csvRecord.get(16); if(middleBand==null ||
							 * middleBand.equalsIgnoreCase("") || middleBand.equalsIgnoreCase("null")) {
							 * System.out.println("Empty or null data"); }else { dmiddleBand =
							 * Double.parseDouble(middleBand); JSONObject obj = new JSONObject();
							 * obj.put("name","Middle_Band"); obj.put("value",dmiddleBand); arr.add(obj); }
							 * 
							 * String norMiddleBand =csvRecord.get(17); if(norMiddleBand==null ||
							 * norMiddleBand.equalsIgnoreCase("") || norMiddleBand.equalsIgnoreCase("null"))
							 * { System.out.println("Empty or null data"); }else { dnorMiddleBand =
							 * Double.parseDouble(norMiddleBand); JSONObject obj = new JSONObject();
							 * obj.put("name","Normalised_Boillinger_MiddleBand");
							 * obj.put("value",dnorMiddleBand); arr.add(obj); }
							 * 
							 * String lowerBand =csvRecord.get(18); if(lowerBand==null ||
							 * lowerBand.equalsIgnoreCase("") || lowerBand.equalsIgnoreCase("null")) {
							 * System.out.println("Empty or null data"); }else { dlowerBand =
							 * Double.parseDouble(lowerBand); JSONObject obj = new JSONObject();
							 * obj.put("name","Lower_Band"); obj.put("value",dlowerBand); arr.add(obj); }
							 * 
							 * String norLowerBand =csvRecord.get(19); if(norLowerBand==null ||
							 * norLowerBand.equalsIgnoreCase("") || norLowerBand.equalsIgnoreCase("null")) {
							 * System.out.println("Empty or null data"); }else { dnorLowerBand =
							 * Double.parseDouble(norLowerBand); JSONObject obj = new JSONObject();
							 * obj.put("name","Normalised_Boillinger_LowerBand");
							 * obj.put("value",dnorLowerBand); arr.add(obj); }
							 * 
							 * String norBoillingerBand =csvRecord.get(20); if(norBoillingerBand==null ||
							 * norBoillingerBand.equalsIgnoreCase("") ||
							 * norBoillingerBand.equalsIgnoreCase("null")) {
							 * System.out.println("Empty or null data"); }else { dnorBoillingerBand =
							 * Double.parseDouble(norBoillingerBand); JSONObject obj = new JSONObject();
							 * obj.put("name","Normalised_Boillinger_Band");
							 * obj.put("value",dnorBoillingerBand); arr.add(obj); }
							 * 
							 * String slowK =csvRecord.get(21); if(slowK==null || slowK.equalsIgnoreCase("")
							 * || slowK.equalsIgnoreCase("null")) {
							 * System.out.println("Empty or null data"); }else { dslowK =
							 * Double.parseDouble(slowK); JSONObject obj = new JSONObject();
							 * obj.put("name","Stocastic_slow_k"); obj.put("value",dslowK); arr.add(obj); }
							 * 
							 * String norSlowK =csvRecord.get(22); if(norSlowK==null ||
							 * norSlowK.equalsIgnoreCase("") || norSlowK.equalsIgnoreCase("null")) {
							 * System.out.println("Empty or null data"); }else { dnorSlowK =
							 * Double.parseDouble(norSlowK); JSONObject obj = new JSONObject();
							 * obj.put("name","Normalised_Stocastic_slow_k"); obj.put("value",dnorSlowK);
							 * arr.add(obj); }
							 * 
							 * String slowD =csvRecord.get(23); if(slowD==null || slowD.equalsIgnoreCase("")
							 * || slowD.equalsIgnoreCase("null")) {
							 * System.out.println("Empty or null data"); }else { dslowD =
							 * Double.parseDouble(slowD); JSONObject obj = new JSONObject();
							 * obj.put("name","Slow_D"); obj.put("value",dslowD); arr.add(obj); }
							 * 
							 * String norSlowD =csvRecord.get(24); if(norSlowD==null ||
							 * norSlowD.equalsIgnoreCase("") || norSlowD.equalsIgnoreCase("null")) {
							 * System.out.println("Empty or null data"); }else { dnorSlowD =
							 * Double.parseDouble(norSlowD); JSONObject obj = new JSONObject();
							 * obj.put("name","Normalised_Stocastic_slow_D"); obj.put("value",dnorSlowD);
							 * arr.add(obj); }
							 * 
							 * String norStocastic =csvRecord.get(25); if(norStocastic==null ||
							 * norStocastic.equalsIgnoreCase("") || norStocastic.equalsIgnoreCase("null")) {
							 * System.out.println("Empty or null data"); }else { dnorStocastic =
							 * Double.parseDouble(norStocastic); JSONObject obj = new JSONObject();
							 * obj.put("name","Normalised_Stocastic"); obj.put("value",dnorStocastic);
							 * arr.add(obj); }
							 * 
							 * String ADX =csvRecord.get(26); if(ADX==null || ADX.equalsIgnoreCase("") ||
							 * ADX.equalsIgnoreCase("null")) { System.out.println("Empty or null data");
							 * }else { dADX = Double.parseDouble(ADX); JSONObject obj = new JSONObject();
							 * obj.put("name","ADX"); obj.put("value",dADX); arr.add(obj); }
							 * 
							 * String norADX =csvRecord.get(27); if(norADX==null ||
							 * norADX.equalsIgnoreCase("") || norADX.equalsIgnoreCase("null")) {
							 * System.out.println("Empty or null data"); }else { dnorADX =
							 * Double.parseDouble(norADX); JSONObject obj = new JSONObject();
							 * obj.put("name","Normalised_ADX"); obj.put("value",dnorADX); arr.add(obj); }
							 * 
							 * String totalContribution =csvRecord.get(28); if(totalContribution==null ||
							 * totalContribution.equalsIgnoreCase("") ||
							 * totalContribution.equalsIgnoreCase("null")) {
							 * System.out.println("Empty or null data"); }else { dtotalContribution =
							 * Double.parseDouble(totalContribution); JSONObject obj = new JSONObject();
							 * obj.put("name","Total_Contribution"); obj.put("value",dtotalContribution);
							 * arr.add(obj); }
							 */
						  }catch(ArrayIndexOutOfBoundsException e) {
							  logger.info("No more columns");
						  }
//							System.out.println(ticker);
//							System.out.println(date);
//							System.out.println(open);
//							System.out.println(close);
//							System.out.println(high);
//							System.out.println(low);
//							System.out.println(adjustClose);
//							System.out.println(volume);
//							System.out.println("-------------\n");
							int id = rDao.getRevordId(connection, date,time, ticker);
							System.out.println("id is :"+id);
						/*
						 * obj.put("ticker", ticker); obj.put("date", date); obj.put("open", dopen);
						 * obj.put("high", dhigh); obj.put("low", dlow); obj.put("close", dclose);
						 * obj.put("volume", lvolume); obj.put("rsi", drsi); obj.put("norRsi", dnorRsi);
						 * obj.put("macd", dmacd); obj.put("normacd", dnormacd); obj.put("exp", dexp);
						 * obj.put("macdHis", dmacdHis); obj.put("willium", dwillium);
						 * obj.put("norWillium", dnorWillium); obj.put("bUpperband", dbUpperband);
						 * obj.put("norbUpperband", dnorbUpperband); obj.put("middleBand", dmiddleBand);
						 * obj.put("norMiddleBand", dnorMiddleBand); obj.put("lowerBand", dlowerBand);
						 * obj.put("norLowerBand", dnorLowerBand); obj.put("norBoillingerBand",
						 * dnorBoillingerBand); obj.put("slowK", dslowK); obj.put("norSlowK",
						 * dnorSlowK); obj.put("slowD", dslowD); obj.put("norSlowD", dnorSlowD);
						 * obj.put("norStocastic", dnorStocastic); obj.put("ADX", dADX);
						 * obj.put("norADX", dnorADX); obj.put("totalContribution", dtotalContribution);
						 */
							if(id <=0) {
								rDao.insertIntoRecords(connection, arr);
							}
							else {
								rDao.updateRecord(connection,arr);
							}
						  
					  }
					  
					  rowCount++;
	            }
					  
				csvParser.close();
			}
		
			
		}catch (NoSuchFileException e) {
			logger.info("No such file "+path);
			logger.info("Please re-run the program with path : e.g. /home/ubuntu/sample.csv");
		}
		
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			cManager.closeConnection(connection);
		}
    }
}
