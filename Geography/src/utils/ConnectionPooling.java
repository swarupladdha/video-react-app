package utils;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ConnectionPooling {
	private static ConnectionPooling instance;
	static {
		instance = new ConnectionPooling();
	}

	public static ConnectionPooling getInstance() {
		return instance;
	}

	public static ComboPooledDataSource cpds;
	public Connection con = null;

	public ConnectionPooling() {

		int maxPoolSize = Integer.parseInt(PropertiesUtil
				.getProperty("maxpoolsize"));
		System.out.println("Max pool size " + maxPoolSize);
		int minPoolSize = Integer.parseInt(PropertiesUtil
				.getProperty("minpoolsize"));
		System.out.println("Min pool size :" + minPoolSize);

		String jdbc_url = PropertiesUtil.getProperty("jdbc_url");
		System.out.println("jdbc url :" + jdbc_url);
		String jdbc_driver = PropertiesUtil.getProperty("jdbc_driver");
		System.out.println("jdbc driver :" + jdbc_driver);
		String userName = PropertiesUtil.getProperty("username");
		String password = PropertiesUtil.getProperty("password");
		System.out.println(userName + password);
		int timeout = Integer.parseInt(PropertiesUtil.getProperty("timeout"));
		System.out.println(timeout);

		cpds = new ComboPooledDataSource();
		cpds.setInitialPoolSize(Integer.parseInt(PropertiesUtil
				.getProperty("minpoolsize")));
		cpds.setMinPoolSize(Integer.parseInt(PropertiesUtil
				.getProperty("maxpoolsize")));
		cpds.setIdleConnectionTestPeriod(Integer.parseInt(PropertiesUtil
				.getProperty("maxpoolsize")));
		cpds.setJdbcUrl(PropertiesUtil.getProperty("jdbc_url"));
		cpds.setTestConnectionOnCheckout(true);
		cpds.setPreferredTestQuery("SELECT 1");
		cpds.setUnreturnedConnectionTimeout(Integer.parseInt(PropertiesUtil
				.getProperty("timeout")));
		cpds.setDebugUnreturnedConnectionStackTraces(true);
		cpds.setUser(PropertiesUtil.getProperty("username"));
		cpds.setPassword(PropertiesUtil.getProperty("password"));
		try {
			cpds.setDriverClass(PropertiesUtil.getProperty("jdbc_driver"));
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}

	}

	public Connection getConnection() {
		System.out.println("inside get connecttion");
		try {
			if (con == null) {
				con = cpds.getConnection();
				System.out.println("returnunfg");
				return con;
			} else {
				return con;
			}
		} catch (IllegalStateException e) {
			System.out.println("");
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return con;
	}

	public void close(Connection con) {
		try {
			if (con != null) {
				con.close();
				con = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (con != null) {
					con.close();
					con = null;
				}
			} catch (IllegalStateException e) {
				System.out.println("");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
