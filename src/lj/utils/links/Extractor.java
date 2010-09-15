package lj.utils.links;

import java.util.LinkedList;
import java.util.List;

import lj.utils.filter.Filter;
import lj.utils.filter.LinkInformationProducer;
import lj.utils.log.Log;
import lj.utils.xml.XPathUtils;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Document;


public class Extractor {

  static Logger logger = Log.getLogger(Extractor.class);  
  
  List<LinkInformation> list = new LinkedList<LinkInformation>();

  public void extract(Document doc, Filter f, LinkInformationProducer producer) throws Exception {

    XPathUtils xPath = new XPathUtils();
    List<Attribute> l = xPath.listAttributes(doc, "//x:a/@href");    
    for (Attribute attribute : l) {
      
      logger.debug("Inspecting link " + attribute.getValue());
      
      if (f.filter(attribute)) {                
        LinkInformation li = producer.getLinkInformation(f);
        
        if (!list.contains(li)) {
          logger.debug("Adding link " + li.getUrlToDownload());
          list.add(li);
        }
        
      }
    }
    
    logger.info("Number of links found: " + list.size());
  }
  
  public List<LinkInformation> getList() {
    return list;
  }
  
}
