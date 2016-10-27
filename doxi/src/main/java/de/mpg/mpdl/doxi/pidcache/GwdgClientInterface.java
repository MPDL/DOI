package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;

import org.jibx.runtime.JiBXException;

import de.mpg.mpdl.doxi.exception.PidNotFoundException;

public interface GwdgClientInterface {
  public Pid create(URI url) throws GwdgException, JiBXException;

  public Pid retrieve(PidID pidID) throws PidNotFoundException, GwdgException, JiBXException;

  public Pid search(URI url) throws PidNotFoundException, GwdgException, JiBXException;

  public Pid update(Pid pid) throws PidNotFoundException, GwdgException, JiBXException;

  public boolean serviceAvailable();

  public String getGwdgUser();
}
