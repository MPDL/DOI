package de.mpg.mpdl.doxi.pidcache;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import de.mpg.mpdl.doxi.pidcache.model.PidID;

@Entity(name = "pid_cache")
public class PidCache {
  @Id
  @Column(name = "identifier", nullable = false)
  private String identifier;

  @Column(name = "created", nullable = false)
  private Date created;

  public PidCache() {}

  public PidCache(PidID pidID, Date created) {
    this.identifier = pidID.getIdAsString();
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

  public void setCreated(Date created) {
    this.created = created;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }
  
  @Override
  public String toString() {
    return "PidCache [identifier=" + this.identifier + ", created=" + this.created + "]";
  }
}
