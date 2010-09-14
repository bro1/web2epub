package lj.epub.cakephp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lj.epub.sd.FTPPublisher;
import lj.epub.sd.utils.EpubUtils;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import bro1.utils.http.HttpUtil;
import bro1.utils.log.Log;
import bro1.utils.regex.PatternCompiler;
import bro1.utils.xml.XMLUtils;
import bro1.utils.xml.XPathUtils;

import com.adobe.dp.epub.opf.Publication;
import com.adobe.dp.epub.ops.OPSDocument;


/**
 * This class downloads the full cake php manual, splits everything into numbered sections, 
 * does a little bit of cleanup and saves everything as 
 * 
 * @author Linas Jakucionis linasj@gmail.com
 *
 */
public class CakeSite {

	static Map<String, String> map = new HashMap<String, String>();

	static Logger logger = Log.getLogger(CakeSite.class);

	public static void main(String[] args) throws Exception {

		CakeSite site = new CakeSite();

		Document content = site
				.getContent("http://book.cakephp.org/complete/876/The-Manual");

		Publication pub = EpubUtils.createPublication("CakePHP13", "http://bro1.centras.info/epubsd/CakePHP13-manual.epub");

		XPathUtils x = new XPathUtils();
		
		Element toce = x.getElement(content, "//x:div[@id='toc']");
		toce.detach();
		
		List<Element> listOfHeaders = x.listElements(content,
				"//x:h1|//x:h2|//x:h3|//x:h4|//x:h5|//x:h6");

		buildMap(site, listOfHeaders);

		int i = 1;
		for (Element headerElement : listOfHeaders) {
			if (site.isNumbered(headerElement)) {
				String title = site.getTitle(headerElement);

				Document storyDoc = site.createDoc(headerElement);
				site.removeExtraContent(storyDoc);
				site.fixInternalRefs(storyDoc);

				OPSDocument body = EpubUtils.addStory(pub, title, i);
				site.attachStoryToEpub(pub, body, storyDoc, new URL(
						"http://book.cakephp.org/complete/876/"));

			}

			i++;

		}

		String fileName = EpubUtils.saveEpub(pub, "cakephp-1.3-manual");

		FTPPublisher ftp = new FTPPublisher();
		ftp.publish("cakephpmanual", new File(fileName));

	}

	private void fixInternalRefs(Document storyDoc) 
	  throws JDOMException, IOException {
		
		XPathUtils xpath = new XPathUtils();

		List<Element> linkElements = xpath.listElements(storyDoc, "//x:a");

		for (Element link : linkElements) {
			String linkValue = link.getAttributeValue("href");
			
			if (linkValue.contains("#") && !linkValue.equals("#")) {
				String[] vv = linkValue.split("#");
				linkValue = vv[0];
			}
			
			if (map.containsKey(linkValue)) {
				link.setAttribute("href", map.get(linkValue));
				System.out.println("Replaced:" + map.get(linkValue));
			} else {
				linkValue = link.getAttributeValue("href");
				if (linkValue.startsWith("/")) {
					link.setAttribute("href", "http://book.cakephp.org/" + linkValue);
				}
			}

		}

	}

	private static void buildMap(CakeSite site, List<Element> listOfElements)
			throws JDOMException, IOException {
		
		int i = 1;
		for (Element e : listOfElements) {
			if (site.isNumbered(e)) {
				String id = site.getID(e);
				if (id != null) {					
					
					Pattern pattern = PatternCompiler.compile("^(.*)-(\\d+)$");
					Matcher matcher = pattern.matcher(id);
					if (matcher.matches()) {						
						String[] strings = id.split("-");
						String key = "/view/" + matcher.group(2) + "/" + matcher.group(1);
						site.map.put(key, "main" + i + ".html");
					}
				}
				
			}
			
			i++;
		}
	}

	private Document createDoc(Element e) throws JDOMException, IOException {
		Document document = new Document();
		Element html = new Element("div");
		document.setRootElement(html);

		Element p = e.getParentElement();
		List<Element> children = p.getChildren();

		boolean start = false;
		for (Element a : children) {
			start = start || a.equals(e);
			if (start) {
				if (!a.equals(e)) {
					String n = a.getName();
					if ((n.equals("h1") || n.equals("h2") || n.equals("h3")
							|| n.equals("h4") || n.equals("h5") || n
							.equals("h6"))
							&& isNumbered(a)) {
						break;
					}
					
					html.addContent((Element) a.clone());
				}

			}

		}

		return document;

	}

	private void attachStoryToEpub(Publication pub, OPSDocument doc,
			Document storyDoc, URL url) throws Exception {
		Element domFullArticle = storyDoc.getRootElement();

		com.adobe.dp.epub.ops.Element element = EpubUtils.convertDomToEpub(pub, doc,
				domFullArticle, url);
		doc.getBody().add(element);

	}



	protected void removeExtraContent(Document e) throws JDOMException,
			IOException {
		XPathUtils xpath = new XPathUtils();
		
		List<Element> l = xpath.listElements(e, "//x:div[@class='options']");
		for (Element a : l) {
			a.detach();
		}

		List<Element> ecl = xpath.listElements(e, "//x:div[@class='comments']");
		for (Element ec : ecl) {
			ec.detach();
		}
		
		
		// The code snippets are duplicated (color coded/plain text), let's remove plain text
		List<Element> csl = xpath.listElements(e, "//x:pre[@class='code']");
		for (Element cs : csl) {
			cs.detach();
		}

	}

	private boolean isNumbered(Element e) throws JDOMException, IOException {

		Element ae = e.getChild("a", e.getNamespace());
		String number = (ae == null ? "" : ae.getTextNormalize());

		return !number.equals("#");
	}

	private String getID(Element e) throws JDOMException, IOException {

		return e.getAttributeValue("id");

	}

	private String getTitle(Element e) throws JDOMException, IOException {

		Element ae = e.getChild("a", e.getNamespace());
		String title = (ae == null ? " " : ae.getTextNormalize()) + " "
				+ e.getTextNormalize();

		return title;
	}

	private Document getContent(String url) throws Exception {

		String content = new HttpUtil().getUrl(url);

		XMLUtils xml = new XMLUtils();
		Document doc = xml.buildDocument(content);
		return doc;

	}
}
