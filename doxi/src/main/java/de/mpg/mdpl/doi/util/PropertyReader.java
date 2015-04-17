package de.mpg.mdpl.doi.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PropertyReader {
	
	private static Logger logger = LogManager.getLogger();
	
	private static Properties properties = null;
	private static Properties messages = null;
	
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
	
	public static String getMessage(String key)
	{
		if(messages == null)
		{
			loadMessages();
		}
		return messages.getProperty(key);
	}
	
	private static void loadMessages()
	{
		try {
			InputStream is = PropertyReader.class.getResourceAsStream("/message.properties");
			messages = new Properties();
			messages.load(is);
			logger.info("Messages loaded from message.properties");
		} catch (IOException e) {
			logger.error("Error while loading messages from message.properties", e);
		}
		
	}

}
