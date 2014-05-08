package ch.cargomedia.wms.http;

import ch.cargomedia.wms.Application;
import ch.cargomedia.wms.stream.VideostreamList;
import ch.cargomedia.wms.stream.VideostreamPublisher;
import com.wowza.wms.http.HTTProvider2Base;
import com.wowza.wms.http.IHTTPRequest;
import com.wowza.wms.http.IHTTPResponse;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.vhost.IVHost;
import flexjson.JSONSerializer;

import java.io.OutputStream;

public class HTTPConnectionStatus extends HTTProvider2Base {

  @Override
  public void onHTTPRequest(IVHost vhost, IHTTPRequest req, IHTTPResponse resp) {
    if (!doHTTPAuthentication(vhost, req, resp))
      return;

    VideostreamList<String, VideostreamPublisher> publishers = Application.getInstance().getVideostreamPublisherList();

    String publisherJSON = "{}";
    if (publishers != null) {
      JSONSerializer serializer = new JSONSerializer().exclude("*.class");
      publisherJSON = serializer.serialize(publishers);
    }

    try {
      resp.setHeader("Content-Type", "application/json");
      resp.setHeader("Connection", "close");
      OutputStream out = resp.getOutputStream();
      byte[] outBytes = publisherJSON.getBytes();
      out.write(outBytes);
      out.close();
    } catch (Exception e) {
      WMSLoggerFactory.getLogger(null).error("HTTPConnection failed: " + e.toString());
    }
  }
}
