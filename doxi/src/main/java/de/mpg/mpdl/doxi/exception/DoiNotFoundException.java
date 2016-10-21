package de.mpg.mpdl.doxi.exception;

import de.mpg.mpdl.doxi.util.PropertyReader;

/**
 * Subclass of DoxiException signaling that a DOI does not exist
 * 
 * @see de.mpg.mpdl.doxi.exception.DoxiException
 * @author walter
 *
 */
public class DoiNotFoundException extends DoxiException {
  public DoiNotFoundException() {
    super(PropertyReader.getMessage("DOI_NOT_FOUND_EXCEPTION"));
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
