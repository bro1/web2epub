package lj.epub.sd.test;

import java.util.List;

import junit.framework.TestCase;
import lj.epub.sd.SlashDotSite;
import lj.utils.string.StringUtil;

import org.jdom.Element;


class SD extends SlashDotSite {

  @Override
  protected String getPlainFileContent(String plainUrl) throws Exception {
    return StringUtil.loadFile("/home/bro1/temp/sd-plain.html");
  }

  
}

public class SlashDotTest extends TestCase{

  public void testDropCommentsControl() throws Exception {
    SD s = new SD();
    List<Element> commentsList = s.getComments("test");
    
    SD.printDom(commentsList.get(0));
    
  }
  
}
