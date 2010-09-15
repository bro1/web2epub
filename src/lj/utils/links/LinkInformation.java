package lj.utils.links;

import java.util.regex.Matcher;

public class LinkInformation {

  private String urlAsRetrieved;
  
  String id;
  String urlFull;
  String urlToDownload;
  Matcher m;
  
 
  
  public LinkInformation(String url) {
    this.urlFull = url;
    this.urlAsRetrieved = url;
  }

  public LinkInformation() {
  }

  @Override
  public boolean equals(Object other) {
    
    if (other instanceof LinkInformation) {
      LinkInformation otherLinkInformation = (LinkInformation) other;
      return this.urlFull.equals(otherLinkInformation.urlFull);
    } else {
      return false;
    }    
  }

  public void setUrlAsRetrieved(String urlAsRetrieved) {
    this.urlAsRetrieved = urlAsRetrieved;
  }

  public void setUrlFull(String fullUrl) {
    this.urlFull = fullUrl;    
  }
  
  public String getUrlToDownload() {
    return this.urlToDownload == null ? urlFull : urlToDownload;
  }

  public String getUrlAsRetrieved() {
    return urlAsRetrieved;
  }

  public String getUrlFull() {
    return this.urlFull;
  }

  public void setUrlToDownload(String urlToDownload) {
    this.urlToDownload = urlToDownload;    
  }
  
}
