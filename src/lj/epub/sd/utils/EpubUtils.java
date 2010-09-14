package lj.epub.sd.utils;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.apache.log4j.Logger;
import org.jdom.Content;
import org.jdom.Element;
import org.jdom.Text;
import org.jdom.filter.Filter;

import bro1.utils.http.HttpUtil;
import bro1.utils.log.Log;

import com.adobe.dp.epub.io.DataSource;
import com.adobe.dp.epub.io.OCFContainerWriter;
import com.adobe.dp.epub.ncx.TOCEntry;
import com.adobe.dp.epub.opf.NCXResource;
import com.adobe.dp.epub.opf.OPSResource;
import com.adobe.dp.epub.opf.Publication;
import com.adobe.dp.epub.opf.Resource;
import com.adobe.dp.epub.ops.HyperlinkElement;
import com.adobe.dp.epub.ops.ImageElement;
import com.adobe.dp.epub.ops.OPSDocument;

public class EpubUtils {

  static Logger logger = Log.getLogger(EpubUtils.class);

  static Filter elementAndTextFilter = new Filter() {

    private static final long serialVersionUID = 8485049179545105078L;

    @Override
    public boolean matches(Object arg0) {
      return arg0 != null && (arg0 instanceof Element || arg0 instanceof Text);
    }

  };

  public static OPSDocument addStory(Publication epub, String title, int counter) {

    // prepare table of contents
    NCXResource toc = epub.getTOC();
    TOCEntry rootTOCEntry = toc.getRootTOCEntry();

    // create new chapter resource
    OPSResource main = epub.createOPSResource("OPS/main" + counter + ".html");
    epub.addToSpine(main);

    // get chapter document
    OPSDocument mainDoc = main.getDocument();

    // add chapter to the table of contents
    TOCEntry mainTOCEntry = toc.createTOCEntry(title, mainDoc.getRootXRef());
    rootTOCEntry.add(mainTOCEntry);

    // chapter XHTML body element
    com.adobe.dp.epub.ops.Element body = mainDoc.getBody();

    // add a header
    com.adobe.dp.epub.ops.Element h1 = mainDoc.createElement("h1");
    h1.add(title);
    body.add(h1);

    return mainDoc;

  }

  public static Publication createPublication(String title, String identifier) {
    // create new EPUB document
    Publication epub = new Publication();

    // set up title, author and language
    epub.addDCMetadata("title", title
        + " - "
        + new SimpleDateFormat("yyyy/MM/dd").format(new GregorianCalendar()
            .getTime()));
    epub.addDCMetadata("creator", System.getProperty("user.name"));
    epub.addDCMetadata("language", "en");
    epub.addDCMetadata("identifier", identifier);

    return epub;
  }

  @SuppressWarnings("unchecked")
  public static com.adobe.dp.epub.ops.Element convertDomToEpub(Publication pub,
      OPSDocument mainDoc, Element domElement, URL url) throws Exception {

    com.adobe.dp.epub.ops.Element epubElement;

    if ("style".equalsIgnoreCase(domElement.getName()) ||
        "script".equalsIgnoreCase(domElement.getName())) {
      return null;
    }
    
    if ("img".equalsIgnoreCase(domElement.getName())) {
      logger.debug("Image found with source: '"
          + domElement.getAttributeValue("src") + "'");
      epubElement = dealWithImage(pub, mainDoc, domElement, url);
    } else if ("a".equalsIgnoreCase(domElement.getName())) {
      HyperlinkElement he = mainDoc
          .createHyperlinkElement(domElement.getName());

      String href = domElement.getAttributeValue("href");
      if (href != null && href.length() != 0) {
        he.setExternalHRef(href);
      }

      // TODO: what's up with the internal cross reference?
      epubElement = he;

    } else {
      epubElement = mainDoc.createElement(domElement.getName());
    }

    String id = domElement.getAttributeValue("id");

    String clazz = domElement.getAttributeValue("class");
    if (id != null && id.length() != 0) {
      epubElement.setId(id);
    }

    if (clazz != null && clazz.length() != 0) {
      epubElement.setClassName(clazz);
    }

    for (Content ele : (List<Content>) domElement
        .getContent(elementAndTextFilter)) {

      if (ele instanceof Element) {
        com.adobe.dp.epub.ops.Element epubChildElement = convertDomToEpub(pub,
            mainDoc, (Element) ele, url);
         if (epubChildElement != null) {
          epubElement.add(epubChildElement);
         }
      } else if (ele instanceof Text) {
        Text dText = (Text) ele;
        epubElement.add(dText.getText());
      }
    }

    return epubElement;

  }

  private static com.adobe.dp.epub.ops.Element dealWithImage(Publication pub,
      OPSDocument mainDoc, Element domElement, URL url2) throws Exception {

    HttpUtil http = new HttpUtil();

    String url = domElement.getAttributeValue("src");

    url = url.replace(" ", "%20");
    url = url.replace("\\", "/");

    if (url.startsWith("/")) {
      url = EpubUtils.getImageUrl(url2, url);
    }

    // if resource already exists - use it
    Resource resource = getResource(pub, "" + url.hashCode());

    // if resource does not exist - download it and use it
    if (resource == null) {

      byte[] ba = null;

      try {
        ba = http.getBinaryUrl(url);
      } catch (Exception e) {
        logger.error("Could not retrieve image", e);
        return null;
      }

      DataSource dataSource = new ByteArrayDataSource(ba);

      ImageType imageType = getFormatName(ba);
      if (imageType != null) {
        resource = pub.createBitmapImageResource("OPS/" + url.hashCode() + "."
            + imageType.toString(), imageType.getMimeType(), dataSource);
      }
    }

    // create image element and add the resource
    ImageElement img = mainDoc.createImageElement("img");
    img.setImageResource(resource);

    return img;
  }

  /**
   * Method to get the resource from the epub based on a partial name.
   * <p>
   * The class is design to deal with images in the following way:
   * <ul>
   * <li>when retrieving image from the web calculate the has of the URL
   * <li>check if a resource matching the has already exists in the epub
   * <ul>
   * <li>if the resource does not exist download image and store it as
   * OPS/&lt;urlhashcode>.&lt;ext>
   * <li>if the resource exists just reuse the resource from the epub
   * </ul>
   * </ul>
   * 
   * This approach allows to avoid unnecessary downloads of images repeated
   * throughout a website (epub).
   * 
   * @param pub
   * @param resourceNamePartial
   * 
   * @return null if resource not found or the image resource matching the
   *         partial name
   */
  @SuppressWarnings("unchecked")
  private static Resource getResource(Publication pub,
      String resourceNamePartial) {

    Iterator<Resource> resourceIterator = pub.resources();

    while (resourceIterator.hasNext()) {
      Resource resource = resourceIterator.next();

      String resourceNameFull = resource.getName();

      if (resourceNameFull.startsWith("OPS/")
          && (resourceNameFull.endsWith(".png")
              || resourceNameFull.endsWith(".jpg") || resourceNameFull
              .endsWith(".gif"))) {

        CharSequence resourceNamePartialExtracted = resourceNameFull
            .subSequence(4, resourceNameFull.length() - 4);

        if (resourceNamePartial.equals(resourceNamePartialExtracted)) {
          return resource;
        }
      }

    }

    return null;

  }

  /**
   * Returns the format name of the image in the InputStream 'o'.
   * 
   * @return null if the format is not known.
   */
  private static ImageType getFormatName(byte[] buf) {

    try {

      ByteArrayInputStream o = new ByteArrayInputStream(buf);
      // Create an image input stream on the image
      ImageInputStream iis = ImageIO.createImageInputStream(o);

      // Find all image readers that recognize the image format
      Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
      if (!iter.hasNext()) {
        // No readers found
        return null;
      }

      // Use the first reader
      ImageReader reader = iter.next();

      // Close stream
      iis.close();

      // Return the format name
      return ImageType.getType(reader.getFormatName());
    } catch (IOException e) {
    }
    // The image could not be read
    return null;
  }

  private static String getImageUrl(URL pageURL, String relatedURL) {

    if (relatedURL.startsWith("/")) {
      String baseURL = pageURL.getProtocol() + "://" + pageURL.getHost()
          + (pageURL.getPort() == -1 ? "" : ":" + pageURL.getPort());
      return baseURL + relatedURL;
    }

    return relatedURL;

  }

  private static String getTimeStamp() {
    return new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
        .format(new GregorianCalendar().getTime());
  }

  public static String saveEpub(Publication epub, String prefix)
      throws FileNotFoundException, IOException {

    String timeStamp = getTimeStamp();

    String fileName = prefix + "-" + timeStamp + ".epub";

    // save EPUB to an OCF container
    OCFContainerWriter writer = new OCFContainerWriter(new FileOutputStream(
        fileName));
    epub.serialize(writer);

    return fileName;

  }

}
