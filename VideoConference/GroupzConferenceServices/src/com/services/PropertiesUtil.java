package com.services;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

public class PropertiesUtil {

	public static Properties props;
	public static InetAddress inetAddress = null;
	public static String serverName = null;
	private static final String qaUrl = "172.31.20.83";
	static {
		props = new Properties();
		try {
			inetAddress = InetAddress.getLocalHost();
			PropertiesUtil util = new PropertiesUtil();
//			logger.info("PROPERTIES UTILS CALLING");
			serverName = inetAddress.getHostAddress();
			if (serverName != null) {
				if (serverName.equalsIgnoreCase(qaUrl)) {
					props =util.getPropertiesFromClasspath("bgproperties.properties");
				}
				else {
					props = util.getPropertiesFromClasspath("bgproperties1.properties");
				}
			}
			else {
				util.getPropertiesFromClasspath("bgproperties1.properties");	
			}
/*			PropertiesUtil util = new PropertiesUtil();
//			System.out.println("PROPERTIES UTILS CALLING");
			props = util.getPropertiesFromClasspath("background.properties");
//			System.out.println("CALLED");
*/		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// private constructor
	private PropertiesUtil() {
	}

	public static String getProperty(String key) {
		return props.getProperty(key);
	}

	public Properties getPropertiesFromClasspath(String propFileName)
			throws IOException {
		Properties props = new Properties();

		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream(propFileName);

		if (inputStream == null) {
			throw new FileNotFoundException("property file '" + propFileName
					+ "' not found in the classpath");
		}

		props.load(inputStream);
		return props;
	}

}
