package ch.cargomedia.wms.http;

import com.wowza.wms.client.IClient;
import com.wowza.wms.http.HTTProvider2Base;
import com.wowza.wms.http.IHTTPRequest;
import com.wowza.wms.http.IHTTPResponse;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.vhost.IVHost;
import flexjson.JSONSerializer;

import java.io.OutputStream;
import java.util.HashMap;

public class HTTPKillFlashClient extends HTTProvider2Base {

  @Override
  public void onHTTPRequest(IVHost vhost, IHTTPRequest req, IHTTPResponse resp) {
    int clientId;
    HashMap<String, HashMap<String, String>> returnMap = new HashMap<String, HashMap<String, String>>();
    HashMap<String, String> returnKeyValues = new HashMap<String, String>();
    String strClientId;

    if (!doHTTPAuthentication(vhost, req, resp) || req.getMethod().equalsIgnoreCase("get"))
      return;

    req.parseBodyForParams();
    strClientId = req.getParameter("clientId");
    returnKeyValues.put("type", null);
    returnKeyValues.put("msg", "Request unsuccessful");
    returnKeyValues.put("isPublic", "true");
    returnMap.put("error", returnKeyValues);

    if (strClientId != null) {
      WMSLoggerFactory.getLogger(null).info("*** Attempting to stop stream with clientId " + strClientId);
      try {
        clientId = Integer.parseInt(strClientId);
        IClient client = vhost.getClient(clientId);
        if (client != null) {
          client.setShutdownClient(true);
          returnMap.remove("error");
          returnKeyValues.remove("type");
          returnKeyValues.remove("msg");
          returnKeyValues.remove("isPublic");
          returnKeyValues.put("result", "Id " + clientId + " killed");
          returnMap.put("success", returnKeyValues);
        }
      } catch (NumberFormatException e) {
        WMSLoggerFactory.getLogger(null).error("******* NaN: " + strClientId);
        returnKeyValues.put("type", "NumberFormatException");
        returnKeyValues.put("msg", "NaN: " + strClientId);
        returnKeyValues.put("isPublic", "true");
        returnMap.put("error", returnKeyValues);
      }
    }

    JSONSerializer serializer = new JSONSerializer().exclude("*.class");
    String returnJSONStr = serializer.serialize(returnMap);

    try {
      resp.setHeader("Content-Type", "application/json");
      OutputStream out = resp.getOutputStream();
      byte[] outBytes = returnJSONStr.getBytes();
      out.write(outBytes);
    } catch (Exception e) {
      WMSLoggerFactory.getLogger(null).error("*** HTTPProvider failed: " + e.toString());
    }
  }
}
