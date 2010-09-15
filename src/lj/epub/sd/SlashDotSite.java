package lj.epub.sd;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lj.utils.epub.EpubUtils;
import lj.utils.filter.SimpleLinkInformationProducer;
import lj.utils.links.Extractor;
import lj.utils.links.LinkInformation;
import lj.utils.log.Log;
import lj.utils.net.http.HttpUtil;
import lj.utils.regex.PatternCompiler;
import lj.utils.xml.XMLUtils;
import lj.utils.xml.XPathUtils;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.filter.Filter;
import org.jdom.output.XMLOutputter;


import com.adobe.dp.epub.ncx.TOCEntry;
import com.adobe.dp.epub.opf.NCXResource;
import com.adobe.dp.epub.opf.OPSResource;
import com.adobe.dp.epub.opf.Publication;
import com.adobe.dp.epub.ops.OPSDocument;

@SuppressWarnings("serial")
public class SlashDotSite {
  
  Logger logger = Log.getLogger(SlashDotSite.class);
  
  public List<LinkInformation> getLinksToStories() throws Exception {
    
    HttpUtil http = new HttpUtil();
    XMLUtils xml = new XMLUtils();
    
    String slashDotIndex = http.getUrl("http://www.slashdot.org/");
    
    SimpleLinkInformationProducer sip = new SimpleLinkInformationProducer("http:");
    
    Extractor extractor = new Extractor();
    lj.utils.filter.Filter urlFilter = new SlashDotStoryLinkFilter();
    Document indexDoc = xml.buildDocument(slashDotIndex);
    extractor.extract(indexDoc, urlFilter, sip);
        
    return extractor.getList();
    
  }

  /**
   * @param args
   */
  public static void main(String[] args) throws Exception {

    
    SlashDotSite site = new SlashDotSite();
    List<LinkInformation> linksToStories = site.getLinksToStories();
    
    HttpUtil http = new HttpUtil();
    XMLUtils x = new XMLUtils();

    Publication epub = EpubUtils.createPublication("Slashdot", "http://bro1.centras.info/epub/slashdot" 
    		+ new SimpleDateFormat("yyyyMMddHHmmss")
    .format(new GregorianCalendar().getTime()) + ".epub");
    
    int counter = 1;
    
    for (LinkInformation storyLink : linksToStories) {
    
      String storyWithJSEnabled = http.getUrl(storyLink.getUrlToDownload());           
      Document doc = x.buildDocument(storyWithJSEnabled);
      
      String fullTitle = site.getFullTitle(doc);
      String plainUrl = site.getPlainUrl(doc);      
      Element description = site.findDescription(doc.getRootElement());
      
      List<Element> commentsList = site.getComments(plainUrl);
      
      writeEpub(epub, fullTitle, description, commentsList, counter, new URL(plainUrl));
      
      counter++;
    }
    
    String fileName = EpubUtils.saveEpub(epub, "slashdot");
    FTPPublisher ftp = new FTPPublisher();
    ftp.publish("slashdot", new File(fileName));

  }

 
  public List<Element> getComments(String plainUrl) throws Exception {
    
    XMLUtils x = new XMLUtils();
    
    String fileContent = getPlainFileContent(plainUrl);    
    Document doc = x.buildDocument(fileContent);
 
    List<Element> commentsList = new LinkedList<Element>();
    
    findComments(doc.getRootElement(), commentsList);
    return commentsList;
    
  }


  protected String getPlainFileContent(String plainUrl) throws Exception {
    HttpUtil http = new HttpUtil();
    String fileContent = http.getUrl(plainUrl);
    return fileContent;
  }


  private String getPlainUrl(Document d) throws JDOMException, IOException {
    
    XPathUtils x1 = new XPathUtils();
    
    Element plainCommentsLinkElement = x1.getElement(d, "//x:div[@class='commentBox']/x:noscript/x:small/x:a");
    String plainUrl = plainCommentsLinkElement.getAttribute("href").getValue();
    plainUrl = "http:" + plainUrl.replace("mode=thread", "mode=flat");
    
    System.out.println(plainUrl);
    
    return plainUrl;
  }


  private String getFullTitle(Document d) throws JDOMException, IOException {
    
    XPathUtils x1 = new XPathUtils();
    
    Element e = x1.getElement(d, "//x:div[@id='firehoselist']");
    
    Element titleElement = x1.getElement(e, "//x:a[@class='datitle']");
    
    Element skin = x1.getElement(e, "//x:a[@class='skin']");
    
    String skinText = "";
    
    if (skin != null) {
      
      skinText = skin.getText();
      
      // Drop the last character if it is a colon
      if (skinText.endsWith(":")) {
        skinText = skinText.substring(0, skinText.length() - 1);
      }
      
      // Add brackets around it
      skinText = " (" + skinText + ")"; 
    }
    
    System.out.println(skinText);
    System.out.println(titleElement.getText());
    
    return titleElement.getText() + skinText;
  }


  @SuppressWarnings("unchecked")
  private Element findDescription(Element element) {
        
    Pattern cp = PatternCompiler.compile("text-\\d+");
       
    if (element.getName().equals("div")
        && cp.matcher(getAtt(element, "id")).matches()) {        
        return element;
    }
    
    for (Element e : (List<Element>) element.getContent(filterElementsOnly)) {
      Element childResult = findDescription(e);
      if (childResult != null) {
        return childResult;
      }
    }
  
    return null;    
  }
  
  
  public static void writeEpub(Publication epub, String title, Element description, List<Element> commentList, int counter, URL url) throws Exception {
    
      // prepare table of contents
      NCXResource toc = epub.getTOC();
      TOCEntry rootTOCEntry = toc.getRootTOCEntry();

      // create new chapter resource
      OPSResource main = epub.createOPSResource("OPS/main" + counter +  ".html");
      epub.addToSpine(main);

      // get chapter document
      OPSDocument mainDoc = main.getDocument();

      // add chapter to the table of contents
      TOCEntry mainTOCEntry = toc.createTOCEntry(title, mainDoc
          .getRootXRef());
      rootTOCEntry.add(mainTOCEntry);

      // chapter XHTML body element
      com.adobe.dp.epub.ops.Element body = mainDoc.getBody();

      // add a header
      com.adobe.dp.epub.ops.Element h1 = mainDoc.createElement("h1");
      h1.add(title);
      body.add(h1);
            
      body.add(EpubUtils.convertDomToEpub(epub, mainDoc, description, url));
      
      for (Element commentElement : commentList) {
        com.adobe.dp.epub.ops.Element commentEpub = EpubUtils.convertDomToEpub(epub, mainDoc, commentElement, url);
        body.add(commentEpub);
      }

  }
  

  public static void printDom(Element element) throws IOException {
    StringWriter s = new StringWriter();
    XMLOutputter x = new XMLOutputter();
    x.output(element, s);
    System.out.println(s.getBuffer());   
  }
    
  static Filter filterElementsOnly = new Filter() {

    @Override
    public boolean matches(Object arg0) {
      return arg0 != null && arg0 instanceof Element;
      
    }
    
  };

  static String getAtt(Element e, String name) {
    if (e.getAttribute(name) != null) {
      return e.getAttributeValue(name);
    } 
    return "";
    
  }
  
  @SuppressWarnings("unchecked")
  private void findComments(Element comments, List<Element> commentList) throws JDOMException, IOException {    
    
    Pattern cp = PatternCompiler.compile("comment_(\\d+)");
    
    if (comments.getName().equals("div")) {
        Matcher m = cp.matcher(getAtt(comments, "id")); 
        
        if (m.matches())  {          
          removeCommentControls(comments, m);          
          commentList.add(comments);
        }
    }
    
    for (Element e : (List<Element>) comments.getContent(filterElementsOnly)) {
      findComments(e, commentList);
    }        
    
  }


  private void removeCommentControls(Element comments, Matcher matcher)
      throws JDOMException, IOException {
    
    XPathUtils xp = new XPathUtils();
    String commentID = matcher.group(1);
    Element commentControlElement = xp.getElement(comments, "//x:div[@id = 'comment_sub_" + commentID + "']");
    commentControlElement.getParent().removeContent(commentControlElement);
  }
  

}
