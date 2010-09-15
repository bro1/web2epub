package lj.epub.sd.test;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import junit.framework.TestCase;
import lj.utils.net.http.HttpUtil;


public class TestImageDownload extends TestCase {

  public void testImageDownload() throws Exception {
    
    String url = "http://img.thedailywtf.com/images/200907/errord/spywareterminator ads.jpg";
    String s = URLEncoder.encode(url, "utf-8");
    System.out.println(s);
    HttpUtil http = new HttpUtil();    
    http.getBinaryUrl(url);
  }  
  
  public void testUrl() throws MalformedURLException {
    URL url = new URL("http://thedailywtf.com/Comments/The-Generic-Package.aspx?aaa=1");
    assertEquals("/Comments/The-Generic-Package.aspx", url.getPath());
    assertEquals("/Comments/The-Generic-Package.aspx?aaa=1", url.getFile());
    assertEquals("http", url.getProtocol());
    assertEquals(-1, url.getPort());
    assertEquals("thedailywtf.com", url.getHost());

  }
  
}
