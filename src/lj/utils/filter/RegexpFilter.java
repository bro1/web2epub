package lj.utils.filter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lj.utils.log.Log;
import lj.utils.regex.PatternCompiler;

import org.apache.log4j.Logger;
import org.jdom.Attribute;


public class RegexpFilter implements Filter {

  Logger logger = Log.getLogger(RegexpFilter.class);
  
  private Pattern regexp;

  private String lastUrl;

  private Matcher lastMatch;

  public RegexpFilter(String regExp) {

    this.regexp = PatternCompiler.compile(regExp);
    logger.debug("Regular expression filter has been created with regular expression '" + regExp + "'");
  }

  /**
   * Filter URLs using the regular expression supplied.
   */
  @Override
  public boolean filter(Attribute urlA) {

    String url = urlA.getValue();
    
    Log.debug("Do we need to filter out URL: " + url);
    
    if (url.startsWith("mailto:")) {
      return false;
    }
        
    Matcher matcher = regexp.matcher(url);
    
    lastUrl = url;
    lastMatch = matcher;
    
    return matcher.find();
  }

  /**
   * Get full URL.
   * 
   * Combines the site name and relative URL.
   */
  @Override
  public String getUrl() {
    return lastUrl;
  }
  
  public Matcher getLastMatcher() {
    return lastMatch;
  }


}
