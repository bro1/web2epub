package lj.epub.stuff;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

import lj.epub.sd.SiteSkeleton;
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

import com.adobe.dp.epub.opf.Publication;
import com.adobe.dp.epub.ops.OPSDocument;

public class StuffSite extends SiteSkeleton {

	static Logger logger = Log.getLogger(StuffSite.class);

	public static void main(String[] args) throws Exception {

		StuffSite site = new StuffSite();
		site.doWork();

	}

	@Override
	protected void doWork() throws Exception {

		Document content = getContent("http://www.stuff.co.nz/");

		List<LinkInformation> linksToStories = getLinksToStories(content);

		Publication pub = EpubUtils.createPublication(
				"Stuff",
				"http://bro1.centras.info/epub/stuff"
						+ new SimpleDateFormat("yyyyMMddHHmmss")
								.format(new GregorianCalendar().getTime())
						+ ".epub");

		int i = 1;
		for (LinkInformation link : linksToStories) {

			Document storyDoc = getContent(link.getUrlToDownload());

			try {
				String title = getTitle(storyDoc);

				OPSDocument body = EpubUtils.addStory(pub, title, i);

				attachStoryToEpub(pub, body, storyDoc,
						new URL(link.getUrlToDownload()));

				setProgress((int) (i / (double) linksToStories.size() * 100));
				
				i++;
			} catch (Exception e) {
				logger.error("Something wrong processing the following URL: "
						+ link.getUrlToDownload(), e);
			}
		}

		String fileName = EpubUtils.saveEpub(pub, "stuff");

		setEnable();
		
	}

	private void attachStoryToEpub(Publication pub, OPSDocument doc,
			Document storyDoc, URL url) throws Exception {
		XPathUtils xpath = new XPathUtils();
		Element domFullArticle = xpath.getElement(storyDoc,
				"//x:div[@id='left_col']");

		cleanupStory(domFullArticle);

		com.adobe.dp.epub.ops.Element z = EpubUtils.convertDomToEpub(pub, doc,
				domFullArticle, url);
		doc.getBody().add(z);

	}

	private void cleanupStory(Element domFullArticle) throws Exception {

		removeByXPath(domFullArticle, "//x:h1");

		removeDivsByID(domFullArticle, "story_features_empty");
		removeDivsByID(domFullArticle, "adSTORYBODY");
		removeDivsByID(domFullArticle, "adsponsored_links_box");
		removeDivsByID(domFullArticle, "sharebox");
		removeDivsByID(domFullArticle, "related_box");
		removeDivsByID(domFullArticle, "adRELEVANTOFFER1");
		removeDivsByID(domFullArticle, "postcomment_box");

		removeDivsByClass(domFullArticle, "toolbox_item");
	}

	private void removeDivsByID(Element domFullArticle, String string)
			throws Exception {

		XPathUtils xpath = new XPathUtils();
		Element el = xpath.getElement(domFullArticle, "//x:div[@id='" + string
				+ "']");
		if (el != null) {
			el.getParent().removeContent(el);
		}
	}

	private void removeDivsByClass(Element domFullArticle, String string)
			throws Exception {

		XPathUtils xpath = new XPathUtils();
		List<Element> list = xpath.listElements(domFullArticle,
				"//x:div[@class='" + string + "']");

		for (Element el : list) {
			el.getParent().removeContent(el);
		}

	}

	private void removeByXPath(Element domFullArticle, String xpathExpression)
			throws Exception {

		XPathUtils xpath = new XPathUtils();
		List<Element> list = xpath
				.listElements(domFullArticle, xpathExpression);

		for (Element el : list) {
			el.getParent().removeContent(el);
		}

	}

	private String getTitle(Document story) throws JDOMException, IOException {

		XPathUtils x = new XPathUtils();
		Element titleElement = x.getElement(story, "//x:h1");
		String title = titleElement.getTextNormalize();

		return title;
	}

	private List<LinkInformation> getLinksToStories(Document contentDoc)
			throws Exception {

		removeDivsByID(contentDoc.getRootElement(), "footer-sitemap");
		removeDivsByID(contentDoc.getRootElement(), "other-newspapers");
		removeDivsByID(contentDoc.getRootElement(), "pagetop_masthead");
		removeDivsByID(contentDoc.getRootElement(), "welcome_user");
		removeDivsByID(contentDoc.getRootElement(), "greybar");
		removeDivsByID(contentDoc.getRootElement(), "customise");
		removeDivsByID(contentDoc.getRootElement(), "right_col");
		removeDivsByID(contentDoc.getRootElement(), "right_col");

		SimpleLinkInformationProducer sip = new SimpleLinkInformationProducer(
				"http://www.stuff.co.nz");

		Extractor extractor = new Extractor();
		lj.utils.filter.Filter urlFilter = new RegexpFilter(
				"^\\/(?!rss\\/).*[^/]$");
		extractor.extract(contentDoc, urlFilter, sip);

		return extractor.getList();
	}

	private Document getContent(String url) throws Exception {

		String content = new HttpUtil().getUrl(url);

		XMLUtils xml = new XMLUtils();
		Document doc = xml.buildDocument(content);
		return doc;

	}
}
