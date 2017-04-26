package com.sni.connection;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.sni.utils.DbUtils;
import com.sni.utils.InterfaceKeys;

public class ConnectionPooling {
	static final Logger logger = Logger.getLogger(ConnectionPooling.class);
	private static ConnectionPooling instance;

	static {
		System.out.println("===============");
        instance = new ConnectionPooling();
    }
	public static ConnectionPooling getInstance() {
	   return instance;
	}
	public static ComboPooledDataSource cpds;
	public  Connection con = null;
	
    public ConnectionPooling() {
    	cpds = new ComboPooledDataSource();
    	cpds.setInitialPoolSize(Integer.parseInt(DbUtils.getProperty("minpoolsize")));
    	cpds.setMinPoolSize(Integer.parseInt(DbUtils.getProperty("maxpoolsize")));
    	cpds.setIdleConnectionTestPeriod(Integer.parseInt(DbUtils.getProperty("maxpoolsize")));
		cpds.setJdbcUrl(DbUtils.getProperty("url"));
		cpds.setTestConnectionOnCheckout( true );
		cpds.setPreferredTestQuery( "SELECT 1" );
		cpds.setUnreturnedConnectionTimeout(Integer.parseInt(DbUtils.getProperty("timeout")));
		cpds.setDebugUnreturnedConnectionStackTraces(true);
		cpds.setUser(DbUtils.getProperty("root"));
		cpds.setPassword(DbUtils.getProperty("password"));
		logger.info(cpds);
		try {
			cpds.setDriverClass(DbUtils.getProperty(InterfaceKeys.jdbcDriver));
		} 
		catch (PropertyVetoException e) {
			e.printStackTrace();
		}
    }

	public Connection getConnection() {
		try {
			if(con==null){
				con = cpds.getConnection();
				logger.info("connection accquired"+cpds);
				return con;
			}
			else{
				con = cpds.getConnection();
				logger.info("connection accquired"+cpds);
				return con;
			}
		} 
		catch(IllegalStateException e){
			System.out.println("");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}

	public void close(Connection con) {
		try{
			if(con!=null){
			con.close();
			logger.info("connection released"+con.toString());
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				if(con!=null){
				con.close();
				logger.info("connection released"+con.toString());
				}
			}
			catch(IllegalStateException e){
				System.out.println("");
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}