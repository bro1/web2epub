package lj.utils.net.http;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

// TODO: add java doc
public class Downloader {

  // TODO: rename the class
  private String encoding = "utf8";

  


  public String load(String fileName) throws Exception {

    File f = new File(fileName);
    InputStreamReader in = new InputStreamReader(new FileInputStream(fileName),
        this.encoding);
    char[] charBuffer = new char[(int) f.length()];
    in.read(charBuffer);
    return new String(charBuffer);

  }

  public void writeToFile(String outputFileName, String contents)
      throws UnsupportedEncodingException, FileNotFoundException, IOException {
    Writer out = new BufferedWriter(new OutputStreamWriter(
        new FileOutputStream(outputFileName), this.encoding));
    out.write(contents);
    out.close();
  }

  public void setEncoding(String encoding) {
    this.encoding = encoding;
  }



}
