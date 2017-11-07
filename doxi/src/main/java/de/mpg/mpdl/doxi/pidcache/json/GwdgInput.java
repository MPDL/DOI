package de.mpg.mpdl.doxi.pidcache.json;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GwdgInput {
  private String type;
  private String parsedData;

  public GwdgInput(String type, String parsedData) {
    this.type = type;
    this.parsedData = parsedData;
  }

  public String getType() {
    return this.type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @JsonProperty("parsed_data")
  public String getParsedData() {
    return this.parsedData;
  }

  @JsonProperty("parsed_data")
  public void setParsedData(String parsedData) {
    this.parsedData = parsedData;
  }

}
