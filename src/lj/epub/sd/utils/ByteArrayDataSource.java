package lj.epub.sd.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.adobe.dp.epub.io.DataSource;

public class ByteArrayDataSource extends DataSource {

  public ByteArrayDataSource(byte[] bytes) {
    this.bytes = bytes;
  }

  byte[] bytes;

  @Override
  public InputStream getInputStream() throws IOException {

    return new ByteArrayInputStream(bytes);
  }

}
