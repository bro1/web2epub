package lj.utils.net.http;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;


public class EncodedResponseHandler implements ResponseHandler<String> {

  private String encoding;
  
  public EncodedResponseHandler(String encoding) {
    this.encoding = encoding; 
  }
  
  @Override
  public String handleResponse(HttpResponse response)
      throws ClientProtocolException, IOException {
        
    StatusLine statusLine = response.getStatusLine();

    if (statusLine.getStatusCode() >= 300) {
      throw new HttpResponseException(statusLine.getStatusCode(), statusLine
          .getReasonPhrase());
    }

    HttpEntity entity = response.getEntity();
    return entity == null ? null : EntityUtils.toString(entity, encoding);
  }

}
