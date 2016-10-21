package de.mpg.mpdl.doxi.model;

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
}
