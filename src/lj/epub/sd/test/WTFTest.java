package lj.epub.sd.test;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;
import lj.epub.sd.SlashDotSite;
import lj.epub.wtf.WTFSite;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import bro1.utils.string.StringUtil;
import bro1.utils.xml.XMLUtils;
import bro1.utils.xml.XPathUtils;

class WTFSiteMock extends WTFSite {

  @Override
  public void removeButtons(Element e) throws JDOMException, IOException {
    super.removeButtons(e);
  }

  @Override
  public void removeTable(Element e) throws Exception {
    super.removeTable(e);
  }

}

public class WTFTest extends TestCase {

  public void testDropCommentsControl() throws Exception {
    WTFSiteMock s = new WTFSiteMock();

    XPathUtils xpath = new XPathUtils();

    Document storyDoc = new XMLUtils().buildDocument(StringUtil
        .loadFile("main1.html"));

    List<Element> cl = xpath.listElements(storyDoc,
        "//x:div[@class='CommentContainer']");
    for (Element e : cl) {

      s.removeButtons(e);
      s.removeTable(e);

      SlashDotSite.printDom(e);

    }

  }


}
