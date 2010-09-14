package bro1.mine.filter;

import bro1.mine.utils.LinkInformation;

public interface LinkInformationProducer {

  LinkInformation getLinkInformation(Filter filter) throws Exception;
  
}
