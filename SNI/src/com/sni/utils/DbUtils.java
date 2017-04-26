package com.sni.utils;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.Properties;

import java.util.Set;

public class DbUtils {
	public static Properties props;

	static {
		props = new Properties();
		try {
			
			DbUtils util = new DbUtils();
			props = util.getPropertiesFromClasspath("./com/sniconfig/sni.properties");
				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// public constructor
	public DbUtils() {
	}

	public static String getProperty(String key) {
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
