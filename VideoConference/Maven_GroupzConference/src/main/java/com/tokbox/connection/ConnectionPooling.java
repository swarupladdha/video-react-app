package com.tokbox.connection;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.tokbox.utils.DbUtils;
import com.tokbox.utils.TokBoxInterfaceKeys;

public class ConnectionPooling {
	static final Logger logger = Logger.getLogger(ConnectionPooling.class);
	private static ConnectionPooling instance;

	int getConnectionCount = 0;
	int closeConnectionCount = 0;
	static {
		instance = new ConnectionPooling();
	}

	public static ConnectionPooling getInstance() {
		return instance;
	}

	public static ComboPooledDataSource cpds;
	
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
			cpds.setDriverClass(DbUtils.getProperty(TokBoxInterfaceKeys.jdbcDriver));
		} 
		catch (PropertyVetoException e) {
			e.printStackTrace();
		}
    }

    public synchronized Connection getConnection() {
		try {
			Connection con = null;
			con = cpds.getConnection();
			getConnectionCount++;
			logger.info("connection accquired 1" + cpds);
			logger.info("Get Connection Count : " + getConnectionCount+ " And ThreadId : " + Thread.currentThread().getId());
			return con;

		} catch (IllegalStateException e) {
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public synchronized void close(Connection con) {
		try {
			if (con != null) {
				con.close();
				closeConnectionCount++;
				logger.info("connection released" + con.toString());
				logger.info("Close Connection Count : " + closeConnectionCount
						+ " And ThreadId : " + Thread.currentThread().getId());
				// con = null;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
