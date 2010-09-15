package lj.utils.filter;

import org.jdom.Attribute;

public interface Filter {
  
  boolean filter(Attribute url);
  String getUrl();
  
}
