package de.mpg.mpdl.doxi.pidcache;

import de.mpg.mpdl.doxi.pidcache.model.AbstractVO;

@SuppressWarnings("serial")
public class PidServiceResponseVO extends AbstractVO {
  private String identifier;
  private String url;

  public PidServiceResponseVO() {}

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  @Override
  public String toString() {
    return "PidServiceResponseVO [identifier=" + identifier + ", url=" + url + "]";
  }
}
