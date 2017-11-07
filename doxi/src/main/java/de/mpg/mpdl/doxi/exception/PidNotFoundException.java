package de.mpg.mpdl.doxi.exception;

@SuppressWarnings("serial")
public class PidNotFoundException extends DoxiException {
  public PidNotFoundException() {
    super("PID not found.");
  }

  public PidNotFoundException(int statusCode, String message) {
    super(statusCode, message);
  }
}
