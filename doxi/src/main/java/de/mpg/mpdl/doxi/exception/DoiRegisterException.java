package de.mpg.mpdl.doxi.exception;

import de.mpg.mpdl.doxi.util.PropertyReader;

/**
 * Subclass of DoxiException signaling that registering the DOI at the datacite service failed
 * 
 * @see de.mpg.mpdl.doxi.exception.DoxiException
 * @author walter
 *
 */
public class DoiRegisterException extends DoxiException {

  public DoiRegisterException() {
    super(PropertyReader.getMessage("DOI_REGISTER_EXCEPTION"));
  }

  public DoiRegisterException(String message) {
    super(message);
  }

  public DoiRegisterException(int statusCode) {
    super(statusCode);
  }

  public DoiRegisterException(int statusCode, String message) {
    super(statusCode, message);
  }

  public DoiRegisterException(String message, Throwable cause) {
    super(message, cause);
  }
}
