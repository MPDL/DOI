package de.mpg.mpdl.doxi.pidcache;

import java.net.URI;

import org.jibx.runtime.JiBXException;

public interface GwdgClientInterface {
  public Pid create(URI url) throws GwdgException, JiBXException;

  public Pid retrieve(PidID pidID) throws GwdgException, JiBXException;

  public Pid search(URI url) throws GwdgException, JiBXException;

  public Pid update(Pid pid) throws GwdgException, JiBXException;

  public Pid delete(PidID pidID) throws GwdgException, JiBXException;

  public boolean serviceAvailable();

  public String getGwdgUser();
}
