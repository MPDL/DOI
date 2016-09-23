package de.mpg.mpdl.doi.exception;

import de.mpg.mpdl.doi.util.PropertyReader;

/**
 * Subclass of DoxiException signaling that a DOI has already been created before and cannot be created
 * @see de.mpg.mpdl.doi.exception.DoxiException
 * @author walter
 *
 */
public class DoiAlreadyExistsException extends DoxiException {
	
	
	public DoiAlreadyExistsException()
	{
		super(PropertyReader.getMessage("DOI_ALREADY_EXISTS_EXCEPTION"));
	}
	
	public DoiAlreadyExistsException(String message) {
		super(message);
	}
	
	public DoiAlreadyExistsException(int statusCode) {
		super(statusCode);
	}
	
	public DoiAlreadyExistsException(int statusCode, String message) {
		super(statusCode, message);
	}
}
