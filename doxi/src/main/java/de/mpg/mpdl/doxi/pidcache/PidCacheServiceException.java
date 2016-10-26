package de.mpg.mpdl.doxi.pidcache;

public class PidCacheServiceException extends Exception {
  private int statusCode;

  public PidCacheServiceException() {
    super();
  }

  public PidCacheServiceException(String message) {
    super(message);
  }

  public PidCacheServiceException(Throwable cause) {
    super(cause);
  }

  public PidCacheServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public PidCacheServiceException(int statusCode) {
    super();
    this.statusCode = statusCode;
  }

  public PidCacheServiceException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public PidCacheServiceException(int statusCode, Throwable cause) {
    super(cause);
    this.statusCode = statusCode;
  }

  public PidCacheServiceException(int statusCode, String message, Throwable cause) {
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
