package bro1.utils.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Log {

  @SuppressWarnings("unchecked")
  public static Logger getLogger(Class clazz) {
    return Logger.getLogger(clazz);
  }
  
	static {		
		try {
			URL logFileURL = Log.class.getResource("/log.properties");		
			PropertyConfigurator.configure(logFileURL);
		} catch (Exception e) {
			System.out.println("Could not open log file log.properties. Please make sure it is located in the classpath.");
			e.printStackTrace();
		}		
	}

	public static void debug(String msg) {
		Logger logger = Logger.getLogger("minej");
		logger.debug(msg);
	}
	
	public static void info(String msg) {
		Logger logger = Logger.getLogger("minej");
		logger.info(msg);
	}
	
	public static void warn(String msg) {
		Logger logger = Logger.getLogger("minej");
		logger.warn(msg);
	}
  
  public static void warn(String msg, Throwable throwable) {
    Logger logger = Logger.getLogger("minej");
    logger.warn(msg, throwable);
  }

  public static void err(Throwable exception) {
    Logger logger = Logger.getLogger("minej");
    
    // Log the stack trace to the error
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    exception.printStackTrace(pw);
    logger.error(sw.toString());
  }

  public static void err(String msg) {
    Logger logger = Logger.getLogger("minej");
    logger.error(msg);
  }


}
