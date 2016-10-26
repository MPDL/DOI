package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;

public interface GwdgClientInterface {
  public Pid create(URI url) throws GwdgException;

  public Pid retrieve(PidID pidID) throws GwdgException;

  public Pid search(URI url) throws GwdgException;

  public Pid update(Pid pid) throws GwdgException;

  public Pid delete(PidID pidID) throws GwdgException;

  public boolean serviceAvailable();

  public String getGwdgUser();
}
