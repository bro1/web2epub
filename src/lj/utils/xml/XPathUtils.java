package lj.utils.xml;

import java.io.IOException;
import java.util.List;

import lj.utils.log.Log;

import org.apache.log4j.Logger;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.xpath.XPath;


public class XPathUtils {
    
  static Logger logger = Log.getLogger(XPathUtils.class);

  public Attribute getAttribute(Object context, String xPathExpression)
      throws JDOMException, IOException {
    Object node = getSingleNode(context, xPathExpression);
    return (Attribute) node;
  }

  public Element getElement(Object context, String xPathExpression)
      throws JDOMException, IOException {
    Object node = getSingleNode(context, xPathExpression);
    return (Element) node;
  }

  private Object getSingleNode(Object context, String xPathExpression)
      throws JDOMException {
    XPath xPathObj = org.jdom.xpath.XPath.newInstance(xPathExpression);
    xPathObj.addNamespace("x", "http://www.w3.org/1999/xhtml");
    Object node = xPathObj.selectSingleNode(context);
    return node;
  }

  public List<Attribute> listAttributes(Object context, String xPathExpression)
      throws JDOMException, IOException {
    XPath xPathObj = org.jdom.xpath.XPath.newInstance(xPathExpression);   
    xPathObj.addNamespace("x", "http://www.w3.org/1999/xhtml");
    @SuppressWarnings("unchecked")
    List<Attribute> returnList = xPathObj.selectNodes(context);
    return returnList;
  }

  public List<Content> listElementsOrText(Object context, String xPathExpression)
      throws JDOMException, IOException {
    
    XPath xPathObj = org.jdom.xpath.XPath.newInstance(xPathExpression);
    xPathObj.addNamespace("x", "http://www.w3.org/1999/xhtml");
    @SuppressWarnings("unchecked")
    List<Content> returnList = xPathObj.selectNodes(context);
    return returnList;
  }

  public List<Element> listElements(Object context, String xPathExpression)
      throws JDOMException, IOException {
    XPath xPathObj = org.jdom.xpath.XPath.newInstance(xPathExpression);
    xPathObj.addNamespace("x", "http://www.w3.org/1999/xhtml");
    @SuppressWarnings("unchecked")
    List<Element> returnList = xPathObj.selectNodes(context);
    
    logger.info("Number of elements matching the expression '" + xPathExpression + "' is " + returnList.size());
    if (logger.isDebugEnabled()) {
      for (Element e : returnList) {
        logger.debug(e);
      }
    }
    
    return returnList;
  }

  public List<Text> listTextNodes(Object context, String xPathExpression)
      throws JDOMException, IOException {

    XPath xPathObj = org.jdom.xpath.XPath.newInstance(xPathExpression);
    xPathObj.addNamespace("x", "http://www.w3.org/1999/xhtml");
    @SuppressWarnings("unchecked")
    List<Text> returnList = xPathObj.selectNodes(context);
    return returnList;
  }

}
