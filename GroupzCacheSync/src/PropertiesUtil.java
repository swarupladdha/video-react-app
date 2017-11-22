

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {

	public static Properties props;

	static {
		props = new Properties();
		try {

			PropertiesUtil util = new PropertiesUtil();
//			System.out.println("PROPERTIES UTILS CALLING");
			props = util.getPropertiesFromClasspath("Sync.properties");
//			System.out.println("CALLED");
		} catch (FileNotFoundException e) {
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
