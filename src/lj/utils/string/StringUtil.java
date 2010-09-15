package lj.utils.string;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class StringUtil {
  
  public static String loadFile(String fileName) throws Exception {
    
    File f = new File(fileName);
    InputStreamReader in = new InputStreamReader(new FileInputStream(fileName),
        "utf-8");
    char[] charBuffer = new char[(int) f.length()];
    in.read(charBuffer);
    return new String(charBuffer);
    
  }
}
