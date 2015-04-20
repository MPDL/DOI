package de.mpg.mdpl.doi.exception;

import de.mpg.mdpl.doi.util.PropertyReader;

/**
 * Subclass of DoxiException signaling that the transfered URL is invalid
 * @see de.mpg.mdpl.doi.exception.DoxiException
 * @author walter
 *
 */
public class UrlInvalidException extends DoxiException {
	
	
	public UrlInvalidException()
	{
		super(PropertyReader.getMessage("URL_INVALID_EXCEPTION"));
	}
	
	public UrlInvalidException(String message)
	{
		super(message);
	}
	
	public UrlInvalidException(int statusCode)
	{
		super(statusCode);
	}
	
	public UrlInvalidException(int statusCode, String message)
	{
		super(statusCode, message);
	}
}
