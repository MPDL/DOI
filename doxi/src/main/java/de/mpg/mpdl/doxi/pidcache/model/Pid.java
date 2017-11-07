package de.mpg.mpdl.doxi.pidcache.model;

import java.net.URI;

@SuppressWarnings("serial")
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((pidID == null) ? 0 : pidID.hashCode());
    result = prime * result + ((url == null) ? 0 : url.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Pid other = (Pid) obj;
    if (pidID == null) {
      if (other.pidID != null)
        return false;
    } else if (!pidID.equals(other.pidID))
      return false;
    if (url == null) {
      if (other.url != null)
        return false;
    } else if (!url.equals(other.url))
      return false;
    return true;
  }
}
