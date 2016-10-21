package de.mpg.mpdl.doxi.exception;

import de.mpg.mpdl.doxi.util.PropertyReader;

public class PidAlreadyExistsException extends DoxiException {
  public PidAlreadyExistsException() {
    super(PropertyReader.getMessage("PID_ALREADY_EXISTS_EXCEPTION"));
  }

  public PidAlreadyExistsException(String message) {
    super(message);
  }

  public PidAlreadyExistsException(int statusCode) {
    super(statusCode);
  }

  public PidAlreadyExistsException(int statusCode, String message) {
    super(statusCode, message);
  }
}
