package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;

import de.mpg.mpdl.doxi.exception.PidNotFoundException;
import de.mpg.mpdl.doxi.model.Pid;
import de.mpg.mpdl.doxi.model.PidID;

public interface PidCacheServiceInterface {
  public int getCacheSize();

  public String getLocation();

  public int getQueueSize();

  public int getStatus();

  public String delete(String id);

  public String update(Pid pid);

  public String search(URI url) throws PidNotFoundException;

  public String retrieve(PidID pidID) throws PidNotFoundException;

  public String create(URI url);
}
