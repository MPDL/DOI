package de.mpg.mpdl.doxi.exception;

@SuppressWarnings("serial")
public class GwdgException extends Exception {
  private int statusCode;

  public GwdgException() {
    super();
  }

  public GwdgException(String message) {
    super(message);
  }

  public GwdgException(Throwable cause) {
    super(cause);
  }

  public GwdgException(String message, Throwable cause) {
    super(message, cause);
  }

  public GwdgException(int statusCode) {
    super();
    this.statusCode = statusCode;
  }

  public GwdgException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public GwdgException(int statusCode, Throwable cause) {
    super(cause);
    this.statusCode = statusCode;
  }

  public GwdgException(int statusCode, String message, Throwable cause) {
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
