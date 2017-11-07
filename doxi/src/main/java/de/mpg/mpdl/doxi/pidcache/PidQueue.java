package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import de.mpg.mpdl.doxi.pidcache.model.PidID;

@Entity(name = "pid_queue")
public class PidQueue {
  @Id
  @Column(name = "identifier", nullable = false)
  private String identifier;

  @Column(name = "url", nullable = false)
  private String url;

  @Column(name = "created", nullable = false)
  private Date created;

  public PidQueue() {}

  public PidQueue(PidID pidID, URI url, Date created) {
    this.identifier = pidID.getIdAsString();
    this.url = url.toString();
    this.created = created;
  }

  public Date getCreated() {
    return created;
  }

  public PidID getID() {
    return PidID.create(this.identifier);
  }

  public String getIdentifier() {
    return identifier;
  }

  public URI getUrl() {
    return URI.create(this.url);
  }

  public void setCreated(Date created) {
    this.created = created;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public void setUrl(URI url) {
    this.url = url.toString();
  }

  @Override
  public String toString() {
    return "PidQueue [identifier=" + this.identifier + ", url=" + this.url + ", created="
        + this.created + "]";
  }
}
