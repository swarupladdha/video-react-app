package com.stock.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.apache.log4j.Logger;

public class PropertiesUtil {
	static final Logger logger = Logger.getLogger(PropertiesUtil.class);
	public static Properties props;
	static {
		logger.info("Inside PropertiesUtil");
		props = new Properties();
		try {
			PropertiesUtil util = new PropertiesUtil();
			props = util.getPropertiesFromClasspath("api.properties");
//			logger.info("CALLD");
		} catch (FileNotFoundException e) {
			logger.error("Exception",e);
		} catch (IOException e) {
			logger.error("Exception",e);
		}
	}

	// private constructor
	private PropertiesUtil() {
	}

	public static String getProperty(String key) {
//		logger.info(serverName);
//		 logger.info("IP Address:- " + inetAddress.getHostAddress());
//		 logger.info("Host Name:- " + inetAddress.getHostName());
		return props.getProperty(key);
	}

	public static Set<Object> getkeys() {
		return props.keySet();
	}

	public  Properties getPropertiesFromClasspath(String propFileName)
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
