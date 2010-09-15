package lj.epub.wtf;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

import lj.epub.sd.FTPPublisher;
import lj.utils.epub.EpubUtils;
import lj.utils.filter.RegexpFilter;
import lj.utils.filter.SimpleLinkInformationProducer;
import lj.utils.links.Extractor;
import lj.utils.links.LinkInformation;
import lj.utils.log.Log;
import lj.utils.net.http.HttpUtil;
import lj.utils.xml.XMLUtils;
import lj.utils.xml.XPathUtils;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Parent;


import com.adobe.dp.epub.opf.Publication;
import com.adobe.dp.epub.ops.OPSDocument;

public class WTFSite {

  static Logger logger = Log.getLogger(WTFSite.class);
  
  public static void main(String[] args) throws Exception {

    WTFSite site = new WTFSite();
    
    Document content = site.getContent("http://thedailywtf.com/");

    List<LinkInformation> linksToStories = site.getLinksToStories(content);
    
    Publication pub = EpubUtils.createPublication("TheDailyWTF", "http://bro1.centras.info/epub/thedailywtf" 
    		+ new SimpleDateFormat("yyyyMMddHHmmss")
    .format(new GregorianCalendar().getTime()) + ".epub");
    
    int i = 1;
    for (LinkInformation link: linksToStories) {
      
      Document storyDoc = site.getContent(link.getUrlToDownload());
      
      String title = site.getTitle(storyDoc);
      
      OPSDocument body = EpubUtils.addStory(pub, title, i);      
      
      site.attachStoryToEpub(pub, body, storyDoc, new URL(link.getUrlToDownload()));
      
      i++;
    }
    
    String fileName = EpubUtils.saveEpub(pub, "thedailywtf");
    
    FTPPublisher ftp = new FTPPublisher();
    ftp.publish("thedailywtf", new File(fileName));

    
  }

  private void attachStoryToEpub(Publication pub, OPSDocument doc, Document storyDoc, URL url) throws Exception {
    XPathUtils xpath = new XPathUtils();
    Element domFullArticle = xpath.getElement(storyDoc, "//x:div[@id='ArticleFull']");    
    
    com.adobe.dp.epub.ops.Element z = EpubUtils.convertDomToEpub(pub, doc, domFullArticle, url);
    doc.getBody().add(z);
    
    addComments(pub, doc, storyDoc, url);
        
    String path = url.getPath();
    path = path.substring(path.lastIndexOf("/") + 1);
    
    List<LinkInformation> zzzz = getLinksToMoreComments(storyDoc, path);
    
    for (LinkInformation otherCommentPage : zzzz) {
      Document d = getContent(otherCommentPage.getUrlToDownload());
      addComments(pub, doc, d, url);
    }

    
    
  }

  private void addComments(Publication pub, OPSDocument doc, Document storyDoc,
      URL url) throws Exception {
    
    XPathUtils xpath = new XPathUtils();
    
    List<Element> cl = xpath.listElements(storyDoc, "//x:div[@class='CommentContainer']");
    for (Element e : cl) {
      
      removeButtons(e);
      removeTable(e);
      
      com.adobe.dp.epub.ops.Element com = EpubUtils.convertDomToEpub(pub, doc, e, url);
      doc.getBody().add(com);
    }
    
  }

  /**
   * Remove the table as the table here is a single cell table anyway.
   * 
   * @param e
   * @throws Exception
   */
  protected void removeTable(Element e) throws Exception {
    
    XPathUtils xpath = new XPathUtils();
    Element commentBodyElement = xpath.getElement(e, "descendant-or-self::x:div[@class='CommentBodyText']");    
    Element tableElement = xpath.getElement(commentBodyElement, "ancestor::x:table[position() = last()]");
    
    commentBodyElement.getParent().removeContent(commentBodyElement);
    
    Parent tableParent = tableElement.getParent();
    
    int idx = tableParent.indexOf(tableElement);
    tableParent.removeContent(tableElement);
    ((Element) tableParent).addContent(idx, commentBodyElement);    
    
  }

  protected void removeButtons(Element e) throws JDOMException, IOException {
     XPathUtils xpath = new XPathUtils();
     Element el = xpath.getElement(e, "//x:div[@class='CommentButtons']");
     el.getParent().removeContent(el);
  }

  private String getTitle(Document story) throws JDOMException, IOException {    
    XPathUtils x = new XPathUtils();
    Element titleElement = x.getElement(story, "//x:h1");
    String title = titleElement.getTextNormalize();
    
    String standardPrefix = "Comment On ";
    
    if (title.startsWith(standardPrefix)) {
      title = title.substring(standardPrefix.length());
    }    
    
    return title;
  }

  private List<LinkInformation> getLinksToStories(Document contentDoc) throws Exception {

    SimpleLinkInformationProducer sip = new SimpleLinkInformationProducer("http://thedailywtf.com");
    
    Extractor extractor = new Extractor();
    lj.utils.filter.Filter urlFilter = new RegexpFilter("\\/Comments\\/.*\\.aspx$");
    extractor.extract(contentDoc, urlFilter, sip);
        
    return extractor.getList();
  }
  
  
  private List<LinkInformation> getLinksToMoreComments(Document contentDoc, String path) throws Exception {

    SimpleLinkInformationProducer sip = new SimpleLinkInformationProducer("http://thedailywtf.com/Comments/");
    
    String s = Pattern.quote(path);
    
    Extractor extractor = new Extractor();
    lj.utils.filter.Filter urlFilter = new RegexpFilter(s + "\\?pg=\\d+$");
    extractor.extract(contentDoc, urlFilter, sip);
        
    return extractor.getList();
  }
  

  private Document getContent(String url ) throws Exception {
        
    
    String content = new HttpUtil().getUrl(url);    
    
    XMLUtils xml = new XMLUtils();
    Document doc = xml.buildDocument(content);
    return doc;
    
    
  }
}
