package de.mpg.mpdl.doxi.controller;

import java.net.URI;

import de.mpg.mpdl.doxi.exception.DoxiException;
import de.mpg.mpdl.doxi.exception.PidNotFoundException;
import de.mpg.mpdl.doxi.model.Pid;
import de.mpg.mpdl.doxi.model.PidID;

public interface GwdgControllerInterface {
  /**
   * Calls GWDG PID manager interface:
   * 
   * - http://handle.gwdg.de:8080/pidservice/write/create
   * 
   * @param url:
   * @return
   */
  public Pid createPid(URI url) throws DoxiException;

  /**
   * Calls GWDG PID manager interface:
   * 
   * - http://handle.gwdg.de:8080/pidservice/read/view
   * 
   * @return
   */
  public Pid retrievePid(PidID pidID) throws PidNotFoundException;

  /**
   * Calls GWDG PID manager interface via:
   * 
   * - http://handle.gwdg.de:8080/pidservice/read/search
   * 
   * @param url
   * @return
   */
  public Pid searchPid(URI url) throws PidNotFoundException;

  /**
   * Calls GWDG PID manager interface:
   * 
   * - http://handle.gwdg.de:8080/pidservice/write/edit
   * 
   * @return
   * @throws PidNotFoundException 
   * @throws Exception
   */
  public Pid updatePid(Pid pid) throws PidNotFoundException;

  /**
   * Calls GWDG PID manager interface:
   * 
   * - http://handle.gwdg.de:8080/pidservice/
   * 
   * @return
   */
  public void deletePid(PidID pidID) throws DoxiException;

  /**
   * True if GWDG PID service is available. False if not.
   * 
   * @return
   */
  public boolean serviceAvailable();
}
