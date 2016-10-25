package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;

import de.mpg.mpdl.doxi.exception.PidNotFoundException;

public interface PidServiceInterface {
  public long getCacheSize();

  public long getQueueSize();

  public int getStatus();

  public String delete(String id);

  public String update(Pid pid);

  public String search(URI url) throws PidNotFoundException;

  public String retrieve(PidID pidID) throws PidNotFoundException;

  public String create(URI url);
}
