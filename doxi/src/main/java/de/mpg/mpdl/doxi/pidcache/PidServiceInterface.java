package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;

import de.mpg.mpdl.doxi.exception.DoxiException;

public interface PidServiceInterface {
  public long getCacheSize() throws DoxiException;;

  public long getQueueSize() throws DoxiException;;

  public String update(Pid pid) throws DoxiException;

  public String search(URI url) throws DoxiException;

  public String retrieve(PidID pidID) throws DoxiException;

  public String create(URI url) throws DoxiException;
}
