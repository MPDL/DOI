package de.mpg.mdpl.doi.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertyReader {
	
	private static Logger logger = LogManager.getLogger();
	
	private static Properties properties = null;
	
	public static String getProperty(String key)
	{
		if(properties == null)
		{
			loadProperties();
		}
		return properties.getProperty(key);
	}
	
	private static void loadProperties()
	{
		try {
			InputStream is = PropertyReader.class.getResourceAsStream("/doxi.properties");
			properties = new Properties();
			properties.load(is);
			logger.info("Properties loaded from doxi.properties");
		} catch (IOException e) {
			logger.error("Error while loading properties from doxi.properties", e);
		}
		
	}

}
