package com.stock.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;

import com.stock.utils.ConnectionManager;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RecordsDao {
	
	public int getRevordId(Connection connection,String date,String time,String ticker) {
		Statement stmt = null;
		ResultSet res = null;
		try {
			String query = "select id from oneminute where date='"+date+"' and time='"+time+"' and ticker='"+ticker+"'";
			stmt = connection.createStatement();
			res = stmt.executeQuery(query);
			if(res.next()) {
				return res.getInt("id");
			}
			else {
				return -1;
			}
		}catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		finally {
			ConnectionManager.closeStatement(stmt);
			ConnectionManager.closeResultSet(res);
		}
	}
	
	public void insertIntoRecords(Connection connection,JSONArray arr) {
		System.out.println("inside InsertRecords");
		Statement stmt = null;
		JSONObject obj = new JSONObject();
		System.out.println("---------------------"+arr.size());
		try {
			String query1 = "insert into oneminute (";
			String query2 = ") values (";
			for(int i=0;i<arr.size();i++)
			{
				obj = arr.getJSONObject(i);
				System.out.println(obj.toString());
				query1 = query1 + obj.getString("name")+",";
				Object obj1 = obj.get("value");
				if (obj1 instanceof String) {
					String value = (String) obj1;
					query2= query2+"'"+value+"',";
				}
				else {
					double value = obj.getDouble("value");
					query2  += value+","; 
				}
			}
			stmt = connection.createStatement();
			query1 = query1.substring(0, query1.length()-1);
			query2 = query2.substring(0, query2.length()-1);
			String query3=query1+query2+")";
			System.out.println("----------------------");
			System.out.println("Query is:"+query3);
			stmt.executeUpdate(query3);
			/*if(obj !=null && !obj.isEmpty()) {
				  String ticker = obj.getString("ticker");
				  String date = obj.getString("date");
				  double open = obj.getDouble("open");
				  double high = obj.getDouble("high");
				  double low  = obj.getDouble("low");
				  double close = obj.getDouble("close");
				  long volume = obj.getLong("volume");
				  double rsi = obj.getDouble("rsi");
				  double norRsi = obj.getDouble("norRsi");
				  double macd = obj.getDouble("macd");
				  double normacd = obj.getDouble("normacd");
				  double exp = obj.getDouble("exp");
				  double macdHis = obj.getDouble("macdHis");
				  double willium = obj.getDouble("willium");
				  double norWillium = obj.getDouble("norWillium");
				  double bUpperband = obj.getDouble("bUpperband");
				  double norbUpperband = obj.getDouble("norbUpperband");
				  double middleBand = obj.getDouble("middleBand");
				  double norMiddleBand = obj.getDouble("norMiddleBand");
				  double lowerBand = obj.getDouble("lowerBand");
				  double norLowerBand = obj.getDouble("norLowerBand");
				  double norBoillingerBand = obj.getDouble("norBoillingerBand");
				  double slowK = obj.getDouble("slowK");
				  double norSlowK = obj.getDouble("norSlowK");
				  double slowD = obj.getDouble("slowD");
				  double norSlowD = obj.getDouble("norSlowD");
				  double norStocastic = obj.getDouble("norStocastic");
				  double ADX = obj.getDouble("ADX");
				  double norADX = obj.getDouble("norADX");
				  double totalContribution = obj.getDouble("totalContribution");*/
				
				//String query = "insert into records1 (ticker,date,open,high,low,close,volume,RSI,Normalised_RSI,MACD,Normalised_MACD,Exp,MACD_Histogram,willium,Normalised_willium,Boillinger_UpperBand,Normalised_Boillinger_UpperBand, Middle_Band,Normalised_Boillinger_MiddleBand,Lower_Band,Normalised_Boillinger_LowerBand,Normalised_Boillinger_Band,Stocastic_slow_k,Normalised_Stocastic_slow_k,Slow_D,Normalised_Stocastic_slow_D,Normalised_Stocastic,ADX,Normalised_ADX,Total_Contribution ) values ('"
						//+ticker+"','"+date+"','"+open+"','"+high+"','"+low+"','"+close+"','"+volume+"','"+rsi+"','"+norRsi+"','"+macd+"','"+normacd+"','"+exp+"','"+macdHis+"','"+willium+"','"+norWillium+"','"+bUpperband+"','"+norbUpperband+"','"+middleBand+"','"+norMiddleBand+"','"+lowerBand+"','"+norLowerBand+"','"+norBoillingerBand+"','"+slowK+"','"+norSlowK+"','"+slowD+"','"+norSlowD+"','"+norStocastic+"','"+ADX+"','"+norADX+"','"+totalContribution+"')";
				  /*String query = "insert into records1 (ticker,date,open,high,low,close,volume,RSI,Normalised_RSI,MACD,Normalised_MACD,Exp,MACD_Histogram,willium,Normalised_willium,Boillinger_UpperBand,Normalised_Boillinger_UpperBand, Middle_Band,Normalised_Boillinger_MiddleBand,Lower_Band,Normalised_Boillinger_LowerBand,Normalised_Boillinger_Band,Stocastic_slow_k,Normalised_Stocastic_slow_k,Slow_D,Normalised_Stocastic_slow_D,Normalised_Stocastic,ADX,Normalised_ADX,Total_Contribution ) values ('"
							+ticker+"','"+date+"',"+open+","+high+","+low+","+close+","+volume+","+rsi+","+norRsi+","+macd+","+normacd+","+exp+","+macdHis+","+willium+","+norWillium+","+bUpperband+","+norbUpperband+","+middleBand+","+norMiddleBand+","+lowerBand+","+norLowerBand+","+norBoillingerBand+","+slowK+","+norSlowK+","+slowD+","+norSlowD+","+norStocastic+","+ADX+","+norADX+","+totalContribution+")";
				System.out.println("Insert query :"+query);
				stmt = connection.createStatement();
				stmt.executeUpdate(query);*/
			}
			
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			ConnectionManager.closeStatement(stmt);
		}
	}

	public void updateRecord(Connection connection, JSONArray arr) {
		System.out.println("inside UpdateRecords");
		Statement stmt = null;
		JSONObject obj = new JSONObject();
		System.out.println("---------------------"+arr.size());
		try {
			
			String query1 = "update oneminute set ";
			String query2 = " where ";
			for(int i=0;i<arr.size();i++)
			{
				obj = arr.getJSONObject(i);
				Object obj1 = obj.get("value");
				if (obj1 instanceof String) {
					String value = (String) obj1;
					query2= query2+obj.getString("name")+"='"+value+"' and ";
				}
				else {
					
					double value = obj.getDouble("value");
					query1 = query1 + obj.getString("name")+"="+value+",";
				}
			}
			stmt = connection.createStatement();
			query1 = query1.substring(0, query1.length()-1);
			query2 = query2.substring(0, query2.length()-5);
			String query3=query1+query2;
			System.out.println("----------------------");
			System.out.println("Query is:"+query3);
			stmt.executeUpdate(query3);

			/*if(obj !=null && !obj.isEmpty()) {
				  String ticker = obj.getString("ticker");
				  String date = obj.getString("date");
				  double open = obj.getDouble("open");
				  double high = obj.getDouble("high");
				  double low  = obj.getDouble("low");
				  double close = obj.getDouble("close");
				  long volume = obj.getLong("volume");
				  double rsi = obj.getDouble("rsi");
				  double norRsi = obj.getDouble("norRsi");
				  double macd = obj.getDouble("macd");
				  double normacd = obj.getDouble("normacd");
				  double exp = obj.getDouble("exp");
				  double macdHis = obj.getDouble("macdHis");
				  double willium = obj.getDouble("willium");
				  double norWillium = obj.getDouble("norWillium");
				  double bUpperband = obj.getDouble("bUpperband");
				  double norbUpperband = obj.getDouble("norbUpperband");
				  double middleBand = obj.getDouble("middleBand");
				  double norMiddleBand = obj.getDouble("norMiddleBand");
				  double lowerBand = obj.getDouble("lowerBand");
				  double norLowerBand = obj.getDouble("norLowerBand");
				  double norBoillingerBand = obj.getDouble("norBoillingerBand");
				  double slowK = obj.getDouble("slowK");
				  double norSlowK = obj.getDouble("norSlowK");
				  double slowD = obj.getDouble("slowD");
				  double norSlowD = obj.getDouble("norSlowD");
				  double norStocastic = obj.getDouble("norStocastic");
				  double ADX = obj.getDouble("ADX");
				  double norADX = obj.getDouble("norADX");
				  double totalContribution = obj.getDouble("totalContribution");*/
				
				/*String query = "update records1 set open="+open+",high="+high+",low="+low+",close="+close+",volume="+volume+",RSI="+rsi+",Normalised_RSI="+norRsi+",MACD="+macd+",Normalised_MACD="+normacd+",Exp="+exp+",MACD_Histogram="+macdHis+",willium="+willium+",Normalised_willium="+norWillium+",Boillinger_UpperBand="+bUpperband+",Normalised_Boillinger_UpperBand="+norbUpperband+", Middle_Band="+middleBand+",Normalised_Boillinger_MiddleBand="+norMiddleBand+",Lower_Band="+lowerBand+",Normalised_Boillinger_LowerBand="+norLowerBand+",Normalised_Boillinger_Band="+norBoillingerBand+",Stocastic_slow_k="+slowK+",Normalised_Stocastic_slow_k="+norSlowK+",Slow_D="+slowD+",Normalised_Stocastic_slow_D="+norSlowD+",Normalised_Stocastic="+norStocastic+",ADX="+ADX+",Normalised_ADX="+norADX+",Total_Contribution="+totalContribution+" where ticker='"+ticker+"' and date='"+date+"'";
				System.out.println("update query :"+query);*/
				//String query = "update records1 set open='"+open+"',high='"+high+"',low='"+low+"',close='"+close+"',volume='"+volume+"',RSI='"+rsi+"',Normalised_RSI='"+norRsi+"',MACD='"+macd+"',Normalised_MACD='"+normacd+"',Exp='"+exp+"',MACD_Histogram='"+macdHis+"',willium='"+willium+"',Normalised_willium='"+norWillium+"',Boillinger_UpperBand='"+bUpperband+"',Normalised_Boillinger_UpperBand='"+norbUpperband+"', Middle_Band='"+middleBand+"',Normalised_Boillinger_MiddleBand='"+norMiddleBand+"',Lower_Band='"+lowerBand+"',Normalised_Boillinger_LowerBand='"+norLowerBand+"',Normalised_Boillinger_Band='"+norBoillingerBand+"',Stocastic_slow_k='"+slowK+"',Normalised_Stocastic_slow_k='"+norSlowK+"',Slow_D='"+slowD+"',Normalised_Stocastic_slow_D='"+norSlowD+"',Normalised_Stocastic='"+norStocastic+"',ADX='"+ADX+"',Normalised_ADX='"+norADX+"',Total_Contribution='"+totalContribution+"' where ticker='"+ticker+"' and date='"+date+"'";
				//System.out.println("update query :"+query);
			/*
			 * stmt = connection.createStatement(); stmt.executeUpdate(query); }
			 */
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			ConnectionManager.closeStatement(stmt);
		}
	}
	
	public JSONArray getDate(Connection connection) {
		System.out.println("inside get date");

		Statement stmt = null;
		ResultSet res = null;
		try {
			String query = "select distinct date from oneminute";
			stmt = connection.createStatement();
			res = stmt.executeQuery(query);
			JSONArray arr = new JSONArray();
			while (res.next()) {
				// System.out.println(res.getDate("date"));
				arr.add(res.getDate("date").toString());
			}
			return arr;
			// System.out.println(l);
			// System.out.println(date);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.closeStatement(stmt);
			ConnectionManager.closeResultSet(res);
		}
		return null;
	}
	
	public void getAllMinute(Connection connection,int number) {

		System.out.println("inside get all minute");
		int count = 1;
		Statement stmt = null;
		ResultSet res = null;
		try {
			String query = "select time from oneminute";
			stmt = connection.createStatement();
			res = stmt.executeQuery(query);
			while (res.next()) {
				System.out.println(count);
				Time s = res.getTime("time");
				@SuppressWarnings("deprecation")
				int hours = s.getHours();
				@SuppressWarnings("deprecation")
				int minute = s.getMinutes();

				int total = (hours * 60) + minute;
				double fiveminute = total / number;
				int fivemin = (int) fiveminute;
				System.out.println("fiveminute:" + fivemin);
				int rem = total % number;
				System.out.println("rem :" + rem);
				
				instertTodb(fivemin, rem, s, connection);
				
				count++;
			}
			System.out.println("end---------------------------------------------");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.closeStatement(stmt);
			ConnectionManager.closeResultSet(res);
		}

	}
	
	private static void instertTodb(int fivemin, int rem, Time s, Connection connection) {

		System.out.println("inside insertintodb");

		Statement stmt = null;
		String qry = "update oneminute set fiveminute=" + fivemin + ",rem=" + rem + " where time = '" + s + "'";
		try {
			stmt = connection.createStatement();
			int i=stmt.executeUpdate(qry);
			System.out.println(qry+"-----------------"+i);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.closeStatement(stmt);
		}

	}
	
	public void MaxMinOpenHigh(Connection connection, String date,int number) {

		System.out.println("inside maxminopenhigh");

		Statement stmt = null;
		Statement stmt1=null;
		Statement stmt2=null;
		//ResultSet res = null;
		ResultSet res1 = null;
		ResultSet res2 = null;
		ResultSet top =null;
		double open = 0;
		double close = 0;
		String Closetime = "";
		int rem = 0;
		double high=0;
		double low=0;

		String qrymain = "";

		try {
			
			//String qry = "select date,max(high) as high,min(low) as low,fiveminute,time from oneminute where date ='" + date
				//	+ "' group by fiveminute";
			
				//res = stmt.executeQuery(qry);
			//while (res.next()) {

				//String ndate = res.getDate("date").toString();
				 //high = res.getDouble("high");
				 //low = res.getDouble("low");
				// String ntime = res.getTime("time").toString();
				//int fiveminute = res.getInt("fiveminute");
			//}
			int count=0;
		
			qrymain ="select date,max(high) as high,min(low) as low,fiveminute,time from oneminute where date ='" + date
					+ "' group by fiveminute";
			stmt = connection.createStatement();
			top=stmt.executeQuery(qrymain);
			/*
			 * do { top.beforeFirst(); }
			 */
			while(top.next()){
				
				int fiveminute = top.getInt("fiveminute");
				//String ndate = top.getDate("date").toString();
				high = top.getDouble("high");
				low = top.getDouble("low");
				//String ntime=top.getTime("time").toString();


			
			
				String qry1 = "select open,rem,date,time from oneminute where fiveminute=" + fiveminute + " and date='"
						+ date + "' limit 1";
				stmt1 = connection.createStatement();
				res1 = stmt1.executeQuery(qry1);
				if (res1.next()) {
					open = res1.getDouble("open");
					//ndate = res1.getDate("date").toString();
					rem = res1.getInt("rem");
					System.out.println("rem ..........................." + rem);

				}

				String qry2 = "select close,rem,date,time,id from oneminute where fiveminute=" + fiveminute
						+ " and date='" + date + "' order by id desc limit 1";
				stmt2 = connection.createStatement();
				res2 = stmt2.executeQuery(qry2);
				if (res2.next()) {
					close = res2.getDouble("close");
					//ndate = res2.getDate("date").toString();
					rem = res2.getInt("rem");
					Closetime = res2.getTime("time").toString();
					System.out.println("rem ..........................." + rem);
				}

				int id = getFiveMinuteId(connection, date,number, Closetime);
				
				if (id <= 0) {
					insertIntoFiveMinute(date, high, low, open, close, Closetime,number, connection);
				} else {
					updateFiveMinute(date, high, low, open, close, Closetime,id,number,connection);
				}
				/*
				 * System.out.println(ndate); System.out.println(high); System.out.println(low);
				 * System.out.println(ntime);
				 */
				count++;
				System.out.println("count------------------"+count);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.closeStatement(stmt);
			ConnectionManager.closeResultSet(res2);
			ConnectionManager.closeResultSet(res1);
			ConnectionManager.closeStatement(stmt2);
			ConnectionManager.closeStatement(stmt1);
			ConnectionManager.closeResultSet(top);


		}

	}
	
	public int getFiveMinuteId(Connection connection, String date,int number, String time) {
		System.out.println("inside get id");
		Statement stmt = null;
		ResultSet res = null;
		String query="";
		try {
			if(number==5) {
			 query = "select id from fiveminute where date='" + date + "' and time='" + time + "'";
			}
			if(number==10) {
			 query = "select id from tenminute where date='" + date + "' and time='" + time + "'";
			}
			if(number==13) {
			 query = "select id from thirteen where date='" + date + "' and time='" + time + "'";
			}
			if(number==15) {
			 query = "select id from fifteen where date='" + date + "' and time='" + time + "'";
			}
			if(number==21) {
			 query = "select id from twentyone where date='" + date + "' and time='" + time + "'";
			}
			if(number==30) {
			 query = "select id from thirty where date='" + date + "' and time='" + time + "'";
			}
			if(number==34) {
			 query = "select id from thirtyfour where date='" + date + "' and time='" + time + "'";
			}
			if(number==55) {
			 query = "select id from fiftyfive where date='" + date + "' and time='" + time + "'";
			}
			stmt = connection.createStatement();
			res = stmt.executeQuery(query);
			System.out.println("query " + query);
			if (res.next()) {
				return res.getInt("id");
			} else {
				return -1;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		} finally {
			ConnectionManager.closeStatement(stmt);
			ConnectionManager.closeResultSet(res);
		}
	}
	
	private void insertIntoFiveMinute(String ndate, double high, double low, double open, double close,
			String ntime,int number, Connection connection) {

		System.out.println("inside insert");
		String query="";
		Statement stmt = null;
		if(number==5) {
			 query = "insert into fiveminute (date,high,low,open,close,time) values ('" + ndate + "'," + high + ","
						+ low + "," + open + "," + close + ",'" + ntime + "')";
		}
			if(number==10) {
			 query = "insert into tenminute (date,high,low,open,close,time) values ('" + ndate + "'," + high + ","
						+ low + "," + open + "," + close + ",'" + ntime + "')";
			}
			if(number==13) {
			 query = "insert into thirteen (date,high,low,open,close,time) values ('" + ndate + "'," + high + ","
						+ low + "," + open + "," + close + ",'" + ntime + "')";			}
			if(number==15) {
			 query = "insert into fifteen (date,high,low,open,close,time) values ('" + ndate + "'," + high + ","
						+ low + "," + open + "," + close + ",'" + ntime + "')";
			}
			if(number==21) {
			 query = "insert into twentyone (date,high,low,open,close,time) values ('" + ndate + "'," + high + ","
						+ low + "," + open + "," + close + ",'" + ntime + "')";
			}
			if(number==30) {
			 query = "insert into thirty (date,high,low,open,close,time) values ('" + ndate + "'," + high + ","
						+ low + "," + open + "," + close + ",'" + ntime + "')";
			}
			if(number==34) {
			 query = "insert into thirtyfour (date,high,low,open,close,time) values ('" + ndate + "'," + high + ","
						+ low + "," + open + "," + close + ",'" + ntime + "')";
			}
			if(number==55) {
			 query = "insert into fiftyfive (date,high,low,open,close,time) values ('" + ndate + "'," + high + ","
						+ low + "," + open + "," + close + ",'" + ntime + "')";
			}
			System.out.println("query " + query);
		/*
		 * String qry =
		 * "insert into fiveminute (date,high,low,open,close,time) values ('" + ndate +
		 * "'," + high + "," + low + "," + open + "," + close + ",'" + ntime + "')";
		 */
		try {
			stmt = connection.createStatement();
			int i = stmt.executeUpdate(query);
			System.out.println(i);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.closeStatement(stmt);
		}

	}
	
	private void updateFiveMinute(String ndate, double high, double low, double open, double close, String ntime,int id,int number,
			Connection connection) {

		System.out.println("inside update");
		String query="";
		Statement stmt = null;
		if(number==5) {
			 query = "update fiveminute set high=" + high + ", low=" + low + " , open=" + open + ",close=" + close
						+ " where id="+ id +"";
		}
			if(number==10) {
			 query = "update tenminute set high=" + high + ", low=" + low + " , open=" + open + ",close=" + close
						+ " where id="+ id +"";
			}
			if(number==13) {
			 query = "update thirteen set high=" + high + ", low=" + low + " , open=" + open + ",close=" + close
						+ " where id="+ id +"";
			}
			if(number==15) {
			 query = "update fifteen set high=" + high + ", low=" + low + " , open=" + open + ",close=" + close
						+ " where id="+ id +"";
			}
			if(number==21) {
			 query = "update twentyone set high=" + high + ", low=" + low + " , open=" + open + ",close=" + close
						+ " where id="+ id +"";
			}
			if(number==30) {
			 query = "update thirty set high=" + high + ", low=" + low + " , open=" + open + ",close=" + close
						+ " where id="+ id +"";			
			 }
			if(number==34) {
			 query = "update thirtyfour set high=" + high + ", low=" + low + " , open=" + open + ",close=" + close
						+ " where id="+ id +"";
			}
			if(number==55) {
			 query = "update fiftyfive set high=" + high + ", low=" + low + " , open=" + open + ",close=" + close
						+ " where id="+ id +"";
			}
			System.out.println("query " + query);
		
		/*
		 * String qry = "update fiveminute set high=" + high + ", low=" + low +
		 * " , open=" + open + ",close=" + close + " where id="+ id +"";
		 */
		try {
			stmt = connection.createStatement();
			int i = stmt.executeUpdate(query);
			System.out.println(i);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ConnectionManager.closeStatement(stmt);
		}

	}


	
	
}
