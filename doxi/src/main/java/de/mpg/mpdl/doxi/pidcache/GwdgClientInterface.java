package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;

import de.mpg.mpdl.doxi.exception.PidNotFoundException;

public interface GwdgClientInterface {
  public Pid create(URI url) throws GwdgException, Exception;

  public Pid retrieve(PidID pidID)  throws PidNotFoundException, GwdgException, Exception;

  public Pid search(URI url) throws PidNotFoundException, GwdgException, Exception;

  public Pid update(Pid pid) throws PidNotFoundException, GwdgException, Exception;

  public boolean serviceAvailable();

  public String getGwdgUser();
}
