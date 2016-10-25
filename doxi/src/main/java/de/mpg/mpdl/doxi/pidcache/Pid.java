package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;

public class Pid extends AbstractVO {
  private final PidID pidID;
  private final URI url;

  public Pid(PidID pidID, URI url) {
    this.pidID = pidID;
    this.url = url;
  }
  
  public PidID getPidID() {
    return this.pidID;
  }

  public URI getUrl() {
    return this.url;
  }

  @Override
  public String toString() {
    return "Pid [pidID=" + this.pidID + ", url=" + this.url + "]";
  }
}
