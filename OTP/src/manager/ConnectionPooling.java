package manager;

import java.sql.Connection;
import java.sql.SQLException;
import utils.PropertiesUtil;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ConnectionPooling {
	public static ComboPooledDataSource cpds;
	public Connection con = null;
	private static ConnectionPooling instance;

	int getConnectionCount = 0;
	int closeConnectionCount = 0;

	static {
		try {
			instance = new ConnectionPooling();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static ConnectionPooling getInstance() {
		return instance;
	}

	public ConnectionPooling() throws SQLException {
		cpds = new ComboPooledDataSource();
		try {
			cpds.setDriverClass(PropertiesUtil.getProperty("driver_class"));
			cpds.setJdbcUrl(PropertiesUtil.getProperty("database_url"));
			cpds.setUser(PropertiesUtil.getProperty("user"));
			cpds.setPassword(PropertiesUtil.getProperty("password"));
			cpds.setMinPoolSize(Integer.parseInt(PropertiesUtil
					.getProperty("minimum_pool_size")));
			cpds.setAcquireIncrement(Integer.parseInt(PropertiesUtil
					.getProperty("Aquire_increment")));
			cpds.setMaxPoolSize(Integer.parseInt(PropertiesUtil
					.getProperty("maximum_pool_size")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		try {
			if (con == null) {
				System.out.println("Getting the Connection");
				con = cpds.getConnection();
				getConnectionCount++;
				System.out.println("Get connection count : "
						+ getConnectionCount + " and Thread Id : "
						+ Thread.currentThread().getId());
				return con;
			} else {
				con = cpds.getConnection();
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
				System.out.println("Closing the Connection");
				con.close();
				closeConnectionCount++;
				System.out.println("Close connection count : "
						+ closeConnectionCount + " and Thread Id : "
						+ Thread.currentThread().getId());
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
