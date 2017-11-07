package de.mpg.mpdl.doxi.pidcache.model;

@SuppressWarnings("serial")
public class PidID extends AbstractID {
  private final String id;

  private PidID(String id) {
    this.id = id;
  }
  
  public static PidID create(String id) {
    return new PidID(id);
  }
  
  public String getIdAsString() {
    return this.id;
  }

  @Override
  public String toString() {
    return "PidID [id=" + this.id + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    PidID other = (PidID) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }
}
