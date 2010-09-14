package lj.epub.sd.test;

import java.io.File;

import junit.framework.TestCase;
import lj.epub.sd.FTPPublisher;

public class SlashDotFtpTest extends TestCase {

  
  public void testFtpUpload() throws Exception {
    FTPPublisher f = new FTPPublisher();
    f.publish("slashdot", new File("/home/bro1/projects/epubsd/shashdot-2009-08-03_01-46-52.epub"));
  }
  
}
