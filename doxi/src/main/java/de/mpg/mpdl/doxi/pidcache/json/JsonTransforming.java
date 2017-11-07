package de.mpg.mpdl.doxi.pidcache.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.mpg.mpdl.doxi.exception.GwdgException;
import de.mpg.mpdl.doxi.exception.PidNotFoundException;

public class JsonTransforming {
  private ObjectMapper mapper = new ObjectMapper();

  public JsonTransforming() {}

  public EpicPid getAsEpicPid(String json) throws GwdgException {
    try {
      return this.mapper.readValue(json, EpicPid.class);
    } catch (IOException e) {
      throw new GwdgException(e);
    }
  }

  public List<FullPid> getAsFullPid(String jsonArray) throws GwdgException {
    try {
      return this.mapper.readValue(jsonArray, new TypeReference<List<FullPid>>() { });
    } catch (IOException e) {
      throw new GwdgException(e);
    }
  }
  
  @SuppressWarnings("unchecked")
  public List<String> getAsList(String json) throws PidNotFoundException  {
    try {
      List<String> list = this.mapper.readValue(json, List.class);
      if (list.size() != 0) {
        return list;  
      }
    } catch (Exception e) { // TODO: Eigentlich sollte die Liste immer gefüllt sein. Manchmal liefert die GWDG aber eine leere Liste zurück
    }
    
    throw new PidNotFoundException();  
  }

  public String getGwdgInputAsJson(String type, String parsedData) throws GwdgException {
    List<GwdgInput> gwdgInputs = new ArrayList<GwdgInput>();
    GwdgInput input = new GwdgInput(type, parsedData);
    gwdgInputs.add(input);
    
    try {
      return this.mapper.writeValueAsString(gwdgInputs);
    } catch (IOException e) {
      throw new GwdgException(e);
    }
  }

}
