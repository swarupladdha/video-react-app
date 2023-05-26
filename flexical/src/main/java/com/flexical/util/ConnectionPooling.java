package com.flexical.util;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import com.mchange.v2.c3p0.ComboPooledDataSource;

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

	private ConnectionPooling() {
		cpds = new ComboPooledDataSource();
		cpds.setInitialPoolSize(Integer.parseInt(DbUtils
				.getProperty("MIN_POOL_SIZE")));
		cpds.setMinPoolSize(Integer.parseInt(DbUtils
				.getProperty("MAX_POOL_SIZE")));
		cpds.setIdleConnectionTestPeriod(Integer.parseInt(DbUtils
				.getProperty("IDLE_TIME")));
		cpds.setJdbcUrl(DbUtils.getProperty("JDBC_URL"));
		cpds.setTestConnectionOnCheckout(true);
		cpds.setUnreturnedConnectionTimeout(Integer.parseInt(DbUtils
				.getProperty("TIMEOUT")));
		cpds.setDebugUnreturnedConnectionStackTraces(true);
		cpds.setUser(DbUtils.getProperty("USER"));
		cpds.setPassword(DbUtils.getProperty("PASSWORD"));
		logger.info(cpds);
		try {
			cpds.setDriverClass(DbUtils.getProperty("JDBC_DRIVER"));
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}
	}

	public synchronized Connection getConnection() {
		System.out.println("inside get connection");
		try {
			Connection con = null;
			con = cpds.getConnection();
			getConnectionCount++;
			logger.info("Get Connection Count : " + getConnectionCount
					+ " And Thread Id : " + Thread.currentThread().getId());
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
						+ " And Thread Id : " + Thread.currentThread().getId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
