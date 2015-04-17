package de.mpg.mdpl.doi.exception;

/**
 * DoxiException and its subclasses define a subclass adapted for the DOxI service
 * @author walter
 *
 */
public class DoxiException extends Exception{
	
	private int statusCode;
	
	public DoxiException() {
		super();
	}
	
	public DoxiException(String message) {
		super(message);
	}
	
	public DoxiException(Throwable cause) {
		super(cause);
	}
	
	public DoxiException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public DoxiException(int statusCode) {
		super();
		this.statusCode = statusCode;
	}
	
	public DoxiException(int statusCode, String message) {
		super(message);
		this.statusCode = statusCode;
	}
	
	public DoxiException(int statusCode, Throwable cause) {
		super(cause);
		this.statusCode = statusCode;
	}
	
	public DoxiException(int statusCode, String message, Throwable cause) {
		super(message, cause);
		this.statusCode = statusCode;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
}
