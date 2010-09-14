package lj.epub.sd;

import org.jdom.Attribute;

import bro1.mine.filter.Filter;

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
