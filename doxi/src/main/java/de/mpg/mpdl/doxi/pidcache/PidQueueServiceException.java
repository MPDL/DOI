package de.mpg.mpdl.doxi.pidcache;

public class PidQueueServiceException extends Exception {
  private int statusCode;

  public PidQueueServiceException() {
    super();
  }

  public PidQueueServiceException(String message) {
    super(message);
  }

  public PidQueueServiceException(Throwable cause) {
    super(cause);
  }

  public PidQueueServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public PidQueueServiceException(int statusCode) {
    super();
    this.statusCode = statusCode;
  }

  public PidQueueServiceException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public PidQueueServiceException(int statusCode, Throwable cause) {
    super(cause);
    this.statusCode = statusCode;
  }

  public PidQueueServiceException(int statusCode, String message, Throwable cause) {
    super(message, cause);
    this.statusCode = statusCode;
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

}
