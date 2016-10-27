package de.mpg.mpdl.doxi.exception;

import de.mpg.mpdl.doxi.util.PropertyReader;

public class DoiNotFoundException extends DoxiException {
  public DoiNotFoundException() {
    super(PropertyReader.getMessage("PID_NOT_FOUND_EXCEPTION"));
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
