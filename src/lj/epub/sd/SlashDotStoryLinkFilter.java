package lj.epub.sd;

import lj.utils.filter.Filter;

import org.jdom.Attribute;


public class SlashDotStoryLinkFilter implements Filter {

  private String url;

  @Override
  public boolean filter(Attribute url) {
    
    this.url = url.getValue();
    Attribute classAttribute = url.getParent().getAttribute("class");    
    return classAttribute != null && classAttribute.getValue().equals("datitle");    
  }

  @Override
  public String getUrl() {
    return url;
  }

}
