package lj.utils.filter;

import lj.utils.links.LinkInformation;

public interface LinkInformationProducer {

  LinkInformation getLinkInformation(Filter filter) throws Exception;
  
}
