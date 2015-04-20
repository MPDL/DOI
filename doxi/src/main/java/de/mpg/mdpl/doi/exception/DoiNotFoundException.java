package de.mpg.mdpl.doi.exception;

import de.mpg.mdpl.doi.util.PropertyReader;

/**
 * Subclass of DoxiException signaling that a DOI does not exist
 * @see de.mpg.mdpl.doi.exception.DoxiException
 * @author walter
 *
 */
public class DoiNotFoundException extends DoxiException {
	
	
	public DoiNotFoundException()
	{
		super(PropertyReader.getMessage("DOI_ALREADY_EXISTS_EXCEPTION"));
	}
	
	public DoiNotFoundException(String message) {
		super(message);
	}
	
	public DoiNotFoundException(int statusCode) {
		super(statusCode);
	}
	
	public DoiNotFoundException(int statusCode, String message) {
		super(statusCode, message);
	}
}
