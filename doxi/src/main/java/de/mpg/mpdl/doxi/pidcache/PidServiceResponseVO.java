package de.mpg.mpdl.doxi.pidcache;

@SuppressWarnings("serial")
public class PidServiceResponseVO extends AbstractVO {
  private String action;
  private String identifier;
  private String url;
  private String creator;
  private String userUid;
  private String matches;
  private String message;
  private String institute;
  private String contact;

  public PidServiceResponseVO() {}

  public String getAction() {
    return this.action;
  }

  public void setAction(String action) {
    this.action = action;
  }

  public String getUrl() {
    return this.url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getIdentifier() {
    return this.identifier;
  }

  public void setIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public String getCreator() {
    return this.creator;
  }

  public void setCreator(String creator) {
    this.creator = creator;
  }

  public String getUserUid() {
    return this.userUid;
  }

  public void setUserUid(String userUid) {
    this.userUid = userUid;
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getInstitute() {
    return this.institute;
  }

  public void setInstitute(String institute) {
    this.institute = institute;
  }

  public String getContact() {
    return this.contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getMatches() {
    return this.matches;
  }

  public void setMatches(String matches) {
    this.matches = matches;
  }

  @Override
  public String toString() {
    return "PidServiceResponseVO [action=" + action + ", identifier=" + identifier + ", url=" + url
        + ", creator=" + creator + ", userUid=" + userUid + ", matches=" + matches + ", message="
        + message + ", institute=" + institute + ", contact=" + contact + "]";
  }
}
