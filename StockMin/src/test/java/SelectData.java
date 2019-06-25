/*
 * import java.sql.Connection; import java.sql.ResultSet; import
 * java.sql.Statement; import java.sql.Time;
 * 
 * import com.stock.utils.ConnectionManager;
 * 
 * import net.sf.json.JSONArray;
 * 
 * public class SelectData {
 * 
 * // String ss ="select date,max(high),min(low),fiveminute,time from oneminute
 * // where date ='"+date+"' group by fiveminute";
 * 
 * public static void main(String args[]) {
 * 
 * ConnectionManager cManager = new ConnectionManager();
 * 
 * Connection connection = cManager.getConnection();
 * 
 * JSONArray arr = getDate(connection); System.out.println(arr); String date;
 * 
 * //getAllMinute(connection);
 * 
 * for (int i = 0; i < arr.size(); i++) {
 * 
 * date = arr.getString(i); MaxMinOpenHigh(connection, date);
 * 
 * // OpenHigh(connection,date);
 * 
 * try { Thread.sleep(10 * 1000); } catch (InterruptedException e) { // TODO
 * Auto-generated catch block e.printStackTrace(); }
 * 
 * // Closelow(connection,date); }
 * 
 * }
 * 
 * 
 * private static void Closelow(Connection connection, String date) {
 * System.out.println("inside Close low"); Statement stmt = null; ResultSet res
 * = null; String qry=""; try { //qry =
 * "select open,rem,date from oneminute where date ='"+date+"' and rem=0";
 * //qry="select rem,date,time,open from oneminute where date='"
 * +date+"' group by fiveminute";
 * qry="select rem,date,time,close from oneminute where date='"
 * +date+"' and rem=4 group by fiveminute"; stmt = connection.createStatement();
 * res = stmt.executeQuery(qry);
 * 
 * while(res.next()) {
 * 
 * String ndate = res.getDate("date").toString(); double close =
 * res.getDouble("close"); String ntime = res.getTime("time").toString();
 * 
 * int id = getFiveMinuteId(connection,ndate,ntime); if(id<=0) {
 * insertIntoFiveMinuteClose(ndate,close,ntime,connection); } else {
 * updateCloseFiveMinute(ndate,close,id,ntime,connection); } }
 * 
 * 
 * }catch (Exception e) { e.printStackTrace(); } finally {
 * ConnectionManager.closeStatement(stmt);
 * ConnectionManager.closeResultSet(res); }
 * 
 * 
 * }
 * 
 * 
 * 
 * private static void updateCloseFiveMinute(String ndate, double close, int
 * id,String time, Connection connection) {
 * System.out.println("inside update close ");
 * 
 * Statement stmt = null; String
 * qry="update fiveminute set close="+close+",time='"+time+"' where id="+id+"";
 * try { stmt = connection.createStatement(); int i = stmt.executeUpdate(qry);
 * System.out.println(i);
 * 
 * }catch (Exception e) { e.printStackTrace(); } finally {
 * ConnectionManager.closeStatement(stmt); }
 * 
 * 
 * }
 * 
 * 
 * 
 * private static void insertIntoFiveMinuteClose(String ndate, double close,
 * String ntime, Connection connection) {
 * System.out.println("inside insert close"); Statement stmt = null; String
 * qry="insert into fiveminute (date,close,time) values ('"+ndate+"',"+close+
 * ",'"+ntime+"')"; try { stmt = connection.createStatement(); int i =
 * stmt.executeUpdate(qry); System.out.println(i);
 * 
 * }catch (Exception e) { e.printStackTrace(); } finally {
 * ConnectionManager.closeStatement(stmt); }
 * 
 * }
 * 
 * 
 * 
 * private static void OpenHigh(Connection connection, String date) {
 * System.out.println("inside openhigh");
 * 
 * Statement stmt = null; ResultSet res = null; String qry=""; try { //qry =
 * "select open,rem,date from oneminute where date ='"+date+"' and rem=0";
 * qry="select rem,date,time,open from oneminute where date='"
 * +date+"' group by fiveminute"; stmt = connection.createStatement(); res =
 * stmt.executeQuery(qry); System.out.println("query "+qry);
 * System.out.println("------------------------------------------------");
 * while(res.next()) {
 * 
 * String ndate = res.getDate("date").toString(); double open =
 * res.getDouble("open"); String ntime = res.getTime("time").toString();
 * 
 * int id = getFiveMinuteId(connection,ndate,ntime); if(id<=0) {
 * System.out.println("inserting");
 * insertIntoFiveMinuteOpen(ndate,open,ntime,connection); } else {
 * System.out.println("updating for id :- "+id);
 * updateOpenFiveMinute(ndate,open,id,connection); }
 * System.out.println("--------------------------------------------");
 * 
 * System.out.println(ndate); System.out.println(high); System.out.println(low);
 * System.out.println(ntime);
 * 
 * }
 * 
 * 
 * }catch (Exception e) { e.printStackTrace(); } finally {
 * ConnectionManager.closeStatement(stmt);
 * ConnectionManager.closeResultSet(res); }
 * 
 * 
 * }
 * 
 * 
 * 
 * private static void updateOpenFiveMinute(String ndate, double open, int id,
 * Connection connection) { System.out.println("inside update open");
 * 
 * Statement stmt = null; String qry = "update fiveminute set open=" + open +
 * " where id=" + id + ""; try { stmt = connection.createStatement(); int i =
 * stmt.executeUpdate(qry); System.out.println(i);
 * 
 * } catch (Exception e) { e.printStackTrace(); } finally {
 * ConnectionManager.closeStatement(stmt); }
 * 
 * 
 * }
 * 
 * 
 * private static void insertIntoFiveMinuteOpen(String ndate, double high,
 * String ntime, Connection connection) {
 * System.out.println("inside insert open");
 * 
 * Statement stmt = null; String qry =
 * "insert into fiveminute (date,open,time) values ('" + ndate + "'," + high +
 * ",'" + ntime + "')"; try { stmt = connection.createStatement(); int i =
 * stmt.executeUpdate(qry); System.out.println(i);
 * 
 * } catch (Exception e) { e.printStackTrace(); } finally {
 * ConnectionManager.closeStatement(stmt); }
 * 
 * }
 * 
 * 
 * private static void getAllMinute(Connection connection) {
 * 
 * System.out.println("inside get all minute"); int count = 1; Statement stmt =
 * null; ResultSet res = null; try { String query =
 * "select time from oneminute"; stmt = connection.createStatement(); res =
 * stmt.executeQuery(query); while (res.next()) { System.out.println(count);
 * Time s = res.getTime("time");
 * 
 * @SuppressWarnings("deprecation") int hours = s.getHours();
 * 
 * @SuppressWarnings("deprecation") int minute = s.getMinutes();
 * 
 * int total = (hours * 60) + minute; double fiveminute = total / 5; int fivemin
 * = (int) fiveminute; System.out.println("fiveminute:" + fivemin); int rem =
 * total % 5; System.out.println("rem :" + rem);
 * 
 * instertTodb(fivemin, rem, s, connection);
 * 
 * count++; }
 * System.out.println("end---------------------------------------------");
 * 
 * } catch (Exception e) { e.printStackTrace(); } finally {
 * ConnectionManager.closeStatement(stmt);
 * ConnectionManager.closeResultSet(res); }
 * 
 * }
 * 
 * private static void instertTodb(int fivemin, int rem, Time s, Connection
 * connection) {
 * 
 * System.out.println("inside insertintodb");
 * 
 * Statement stmt = null; String qry = "update oneminute set fiveminute=" +
 * fivemin + ",rem=" + rem + " where time = '" + s + "'"; try { stmt =
 * connection.createStatement(); int i=stmt.executeUpdate(qry);
 * System.out.println(qry+"-----------------"+i);
 * 
 * } catch (Exception e) { e.printStackTrace(); } finally {
 * ConnectionManager.closeStatement(stmt); }
 * 
 * }
 * 
 * private static void MaxMinOpenHigh(Connection connection, String date) {
 * 
 * System.out.println("inside maxminopenhigh");
 * 
 * Statement stmt = null; Statement stmt1=null; Statement stmt2=null;
 * //ResultSet res = null; ResultSet res1 = null; ResultSet res2 = null;
 * ResultSet top =null; double open = 0; double close = 0; String Closetime =
 * ""; int rem = 0; double high=0; double low=0;
 * 
 * String qrymain = "";
 * 
 * try {
 * 
 * //String qry =
 * "select date,max(high) as high,min(low) as low,fiveminute,time from oneminute where date ='"
 * + date // + "' group by fiveminute";
 * 
 * //res = stmt.executeQuery(qry); //while (res.next()) {
 * 
 * //String ndate = res.getDate("date").toString(); //high =
 * res.getDouble("high"); //low = res.getDouble("low"); // String ntime =
 * res.getTime("time").toString(); //int fiveminute = res.getInt("fiveminute");
 * //} int count=0;
 * 
 * qrymain
 * ="select date,max(high) as high,min(low) as low,fiveminute,time from oneminute where date ='"
 * + date + "' group by fiveminute"; stmt = connection.createStatement();
 * top=stmt.executeQuery(qrymain);
 * 
 * do { top.beforeFirst(); }
 * 
 * while(top.next()){
 * 
 * int fiveminute = top.getInt("fiveminute"); //String ndate =
 * top.getDate("date").toString(); high = top.getDouble("high"); low =
 * top.getDouble("low"); //String ntime=top.getTime("time").toString();
 * 
 * 
 * 
 * 
 * String qry1 = "select open,rem,date,time from oneminute where fiveminute=" +
 * fiveminute + " and date='" + date + "' limit 1"; stmt1 =
 * connection.createStatement(); res1 = stmt1.executeQuery(qry1); if
 * (res1.next()) { open = res1.getDouble("open"); //ndate =
 * res1.getDate("date").toString(); rem = res1.getInt("rem");
 * System.out.println("rem ..........................." + rem);
 * 
 * }
 * 
 * String qry2 =
 * "select close,rem,date,time,id from oneminute where fiveminute=" + fiveminute
 * + " and date='" + date + "' order by id desc limit 1"; stmt2 =
 * connection.createStatement(); res2 = stmt2.executeQuery(qry2); if
 * (res2.next()) { close = res2.getDouble("close"); //ndate =
 * res2.getDate("date").toString(); rem = res2.getInt("rem"); Closetime =
 * res2.getTime("time").toString();
 * System.out.println("rem ..........................." + rem); }
 * 
 * int id = getFiveMinuteId(connection, date, Closetime);
 * 
 * if (id <= 0) { insertIntoFiveMinute(date, high, low, open, close, Closetime,
 * connection); } else { updateFiveMinute(date, high, low, open, close,
 * Closetime,id, connection); }
 * 
 * System.out.println(ndate); System.out.println(high); System.out.println(low);
 * System.out.println(ntime);
 * 
 * count++; System.out.println("count------------------"+count); }
 * 
 * } catch (Exception e) { e.printStackTrace(); } finally {
 * ConnectionManager.closeStatement(stmt);
 * ConnectionManager.closeResultSet(res2);
 * ConnectionManager.closeResultSet(res1);
 * ConnectionManager.closeStatement(stmt2);
 * ConnectionManager.closeStatement(stmt1);
 * ConnectionManager.closeResultSet(top);
 * 
 * 
 * }
 * 
 * }
 * 
 * private static void updateFiveMinute(String ndate, double high, double low,
 * double open, double close, String ntime,int id, Connection connection) {
 * 
 * System.out.println("inside update fiveminute");
 * 
 * Statement stmt = null; String qry = "update fiveminute set high=" + high +
 * ", low=" + low + " , open=" + open + ",close=" + close + " where id="+ id
 * +""; try { stmt = connection.createStatement(); int i =
 * stmt.executeUpdate(qry); System.out.println(i);
 * 
 * } catch (Exception e) { e.printStackTrace(); } finally {
 * ConnectionManager.closeStatement(stmt); }
 * 
 * }
 * 
 * private static void insertIntoFiveMinute(String ndate, double high, double
 * low, double open, double close, String ntime, Connection connection) {
 * 
 * System.out.println("inside insertfiveminute");
 * 
 * Statement stmt = null; String qry =
 * "insert into fiveminute (date,high,low,open,close,time) values ('" + ndate +
 * "'," + high + "," + low + "," + open + "," + close + ",'" + ntime + "')"; try
 * { stmt = connection.createStatement(); int i = stmt.executeUpdate(qry);
 * System.out.println(i);
 * 
 * } catch (Exception e) { e.printStackTrace(); } finally {
 * ConnectionManager.closeStatement(stmt); }
 * 
 * }
 * 
 * public static JSONArray getDate(Connection connection) {
 * System.out.println("inside get date");
 * 
 * Statement stmt = null; ResultSet res = null; try { String query =
 * "select distinct date from oneminute"; stmt = connection.createStatement();
 * res = stmt.executeQuery(query); JSONArray arr = new JSONArray(); while
 * (res.next()) { // System.out.println(res.getDate("date"));
 * arr.add(res.getDate("date").toString()); } return arr; //
 * System.out.println(l); // System.out.println(date); } catch (Exception e) {
 * e.printStackTrace(); } finally { ConnectionManager.closeStatement(stmt);
 * ConnectionManager.closeResultSet(res); } return null; }
 * 
 * public static int getFiveMinuteId(Connection connection, String date, String
 * time) { System.out.println("inside fivemin id"); Statement stmt = null;
 * ResultSet res = null; try { String query =
 * "select id from fiveminute where date='" + date + "' and time='" + time +
 * "'"; stmt = connection.createStatement(); res = stmt.executeQuery(query);
 * System.out.println("query " + query); if (res.next()) { return
 * res.getInt("id"); } else { return -1; } } catch (Exception e) {
 * e.printStackTrace(); return -1; } finally {
 * ConnectionManager.closeStatement(stmt);
 * ConnectionManager.closeResultSet(res); } } }
 */