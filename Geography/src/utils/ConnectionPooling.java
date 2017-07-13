package utils;

import java.sql.Connection;
import java.sql.SQLException;
import com.mchange.v2.c3p0.ComboPooledDataSource;

public class ConnectionPooling {
	public static ComboPooledDataSource cpds;
	public Connection con = null;
	private static ConnectionPooling instance;

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
			cpds.setDriverClass("com.mysql.jdbc.Driver");
			cpds.setJdbcUrl("jdbc:mysql://localhost:3306/geography");
			cpds.setUser("root");
			cpds.setPassword("password");
			cpds.setMinPoolSize(5);
			cpds.setAcquireIncrement(5);
			cpds.setMaxPoolSize(20);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Connection getConnection() {
		try {
			if (con == null) {
				System.out.println("Getting the Connection");
				con = cpds.getConnection();
				System.out.println("returnunfg");
				return con;
			} else {
				con = cpds.getConnection();
				System.out.println("returnunfg");
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
