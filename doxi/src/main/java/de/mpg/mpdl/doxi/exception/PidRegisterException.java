package de.mpg.mpdl.doxi.exception;

import de.mpg.mpdl.doxi.util.PropertyReader;

public class PidRegisterException extends DoxiException {
  public PidRegisterException() {
    super(PropertyReader.getMessage("PID_REGISTER_EXCEPTION"));
  }

  public PidRegisterException(String message) {
    super(message);
  }

  public PidRegisterException(int statusCode) {
    super(statusCode);
  }

  public PidRegisterException(int statusCode, String message) {
    super(statusCode, message);
  }

  public PidRegisterException(String message, Throwable cause) {
    super(message, cause);
  }
}
