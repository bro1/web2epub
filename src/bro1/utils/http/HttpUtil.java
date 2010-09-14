package bro1.utils.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;

import bro1.utils.log.Log;
import bro1.utils.string.StringUtil;

public class HttpUtil {

	private static Logger logger = Log.getLogger(HttpUtil.class);

	private static boolean debugMode = false;

	private String encoding = "utf-8";

	private boolean dynamicEncodingDetermination = false;

	private static DefaultHttpClient httpclient;

	static {

		httpclient = new DefaultHttpClient();
		HttpParams h = httpclient.getParams();
		HttpClientParams.setRedirecting(h, true);
		HttpClientParams.setCookiePolicy(h,
				org.apache.http.client.params.CookiePolicy.RFC_2109);
		HttpParams hp = httpclient.getParams();

		HttpConnectionParams.setConnectionTimeout(hp, 30000);
		HttpConnectionParams.setSoTimeout(hp, 90000);
		HttpConnectionParams.getTcpNoDelay(hp);

		httpclient.setParams(hp);
	}

	public void setTimeout(int timeoutSeconds) {
		HttpParams hp = httpclient.getParams();
		HttpConnectionParams.setConnectionTimeout(hp, timeoutSeconds * 1000);

	}

	public String getUrl(String url) throws Exception {
		return getUrl(url, encoding);
	}

	public void setCookie(String name, String value, String domain) {

		logger.debug("Added cookie " + "name: '" + name + "', value: '" + value
				+ "', domain: '" + domain + "'");

		BasicClientCookie c = new BasicClientCookie(name, value);
		c.setDomain(domain);
		httpclient.getCookieStore().addCookie(c);
	}

	public String getUrl(String url, String encoding) throws Exception {

		logger.debug("Attempting to get url: " + url);

		if (isDebugMode()) {
			if (fileExists(url)) {
				return getFile(url);
			}
		}

		HttpGet httpget = new HttpGet(url);

		httpget.addHeader("Cache-Control", "no-cache");

		ResponseHandler<String> responseHandler = new EncodedResponseHandler(
				encoding);
		String responseBody = httpclient.execute(httpget, responseHandler);

		if (isDebugMode()) {
			saveFile(url, responseBody);
		}

		return responseBody;

	}

	String hashUrl(String url) {
		return "../cache/httputildebug-" + url.hashCode() + ".bin";
	}

	private void saveFile(String url, String content) throws Exception {
		String fileName = hashUrl(url);
		Downloader d = new Downloader();
		d.writeToFile(fileName, content);
	}

	private String getFile(String url) throws Exception {

		String fileName = hashUrl(url);

		logger.info("Not downloading content from " + url
				+ " getting the content from the file named " + fileName);

		return StringUtil.loadFile(fileName);
	}

	private boolean fileExists(String url) {

		File file = new File(hashUrl(url));

		logger.info("Cached file for url " + url
				+ (file.exists() ? " exists" : " does not exist"));
		return file.exists();
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isDynamicEncodingDetermination() {
		return dynamicEncodingDetermination;
	}

	public void setDynamicEncodingDetermination(
			boolean dynamicEncodingDetermination) {
		this.dynamicEncodingDetermination = dynamicEncodingDetermination;
	}

	public static void setDebugMode(boolean debugMode) {
		HttpUtil.debugMode = debugMode;
		logger.debug("debug mode is set to: " + debugMode);
	}

	public static boolean isDebugMode() {
		return debugMode;
	}

	public byte[] getBinaryUrl(String url) throws Exception {

		if (isDebugMode()) {
			if (fileExists(url)) {
				logger.info("Running in http debug mode - file exists, not downloading binary file with URL: '"
						+ url + "'");
				return getCachedBinFile(url);
			}
		}

		logger.info("Downloading binary file with URL: '" + url + "'");
		HttpGet httpget = new HttpGet(url);

		ResponseHandler<byte[]> responseHandler = new BinaryResponseHandler();
		byte[] responseBody = httpclient.execute(httpget, responseHandler);

		if (isDebugMode()) {
			FileOutputStream cacheFileOutputStream = new FileOutputStream(
					hashUrl(url));
			cacheFileOutputStream.write(responseBody);
		}

		return responseBody;

	}

	private byte[] getCachedBinFile(String url) throws Exception {

		String fileName = hashUrl(url);
		return getBytesFromFile(new File(fileName));
	}

	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		// Get the size of the file
		long length = file.length();

		if (length > Integer.MAX_VALUE) {
			// File is too large
		}

		// Create the byte array to hold the data
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "
					+ file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

}
