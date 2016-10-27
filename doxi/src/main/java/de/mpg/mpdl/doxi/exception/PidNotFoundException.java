package de.mpg.mpdl.doxi.exception;

import de.mpg.mpdl.doxi.util.PropertyReader;

public class PidNotFoundException extends DoxiException {
  public PidNotFoundException() {
    super(PropertyReader.getMessage("DOI_NOT_FOUND_EXCEPTION"));
  }

  public PidNotFoundException(String message) {
    super(message);
  }

  public PidNotFoundException(int statusCode) {
    super(statusCode);
  }

  public PidNotFoundException(int statusCode, String message) {
    super(statusCode, message);
  }
}
