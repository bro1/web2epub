package bro1.mine.filter;

import bro1.mine.utils.LinkInformation;

public class SimpleLinkInformationProducer implements LinkInformationProducer {

  private String prefix;

  public String getPrefix() {
    return prefix;
  }

  public SimpleLinkInformationProducer(String prefix) {
      this.prefix = prefix;
  }
  
  private String get(String lastUrl) {
    if (!lastUrl.startsWith("http://")) {
      return prefix + lastUrl;
    } else {
      return lastUrl;
    }

  }
  
  public LinkInformation getLinkInformation(Filter filter) throws Exception {
    
    LinkInformation li = new LinkInformation();
    
    li.setUrlAsRetrieved(filter.getUrl());
    li.setUrlFull(get(filter.getUrl()));
    
    return li;
  }

}
