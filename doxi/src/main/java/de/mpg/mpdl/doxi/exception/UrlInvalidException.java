package de.mpg.mpdl.doxi.exception;

import de.mpg.mpdl.doxi.util.PropertyReader;

/**
 * Subclass of DoxiException signaling that the transfered URL is invalid
 * 
 * @see de.mpg.mpdl.doxi.exception.DoxiException
 * @author walter
 *
 */
@SuppressWarnings("serial")
public class UrlInvalidException extends DoxiException {
  public UrlInvalidException() {
    super(PropertyReader.getMessage("URL_INVALID_EXCEPTION"));
  }

  public UrlInvalidException(String message) {
    super(message);
  }

  public UrlInvalidException(int statusCode) {
    super(statusCode);
  }

  public UrlInvalidException(int statusCode, String message) {
    super(statusCode, message);
  }
}
