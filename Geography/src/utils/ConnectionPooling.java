package utils;

import java.sql.Connection;
import java.sql.SQLException;
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
			Connection con = null;
			System.out.println("Getting the Connection");
			con = cpds.getConnection();
			getConnectionCount++;
			System.out.println("Get Connection count in Geography : "+getConnectionCount);
			return con;
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
				System.out.println("Close Connection count in Geography : "+closeConnectionCount);
				con = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
