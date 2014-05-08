package ch.cargomedia.wms.rpc;

import ch.cargomedia.wms.Config;
import ch.cargomedia.wms.module.eventhandler.ConnectionsListener;
import ch.cargomedia.wms.stream.VideostreamPublisher;
import ch.cargomedia.wms.stream.VideostreamSubscriber;
import com.wowza.wms.application.WMSProperties;
import com.wowza.wms.logging.WMSLoggerFactory;
import flexjson.JSONSerializer;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.Vector;

public class RPC {

  private String rpcUrl;
  private Integer clientId;

  public RPC(Integer clientId) {
    WMSProperties streamProperties = ConnectionsListener.appInstance.getProperties();
    this.clientId = clientId;
    this.rpcUrl = streamProperties.getPropertyStr("RPCUrl");
  }

  public int getPublishStreamId(VideostreamPublisher videostream, String streamKey) throws Exception {
    Vector<Object> params = new Vector<Object>();
    params.add(streamKey);
    params.add(String.valueOf(this.clientId));
    params.add(videostream.getStartTimestamp());
    params.add(videostream.getWidth());
    params.add(videostream.getHeight());
    params.add(videostream.getThumbnailCount());
    params.add(videostream.getData());

    String channelId;
    channelId = getPostHttp(Config.RPC_PUBLISH, params);
    return Integer.parseInt(channelId);
  }

  public Boolean isAllowedToSubscribe(VideostreamSubscriber videostream, String streamKey) {
    Vector<Object> params = new Vector<Object>();
    params.add(streamKey);
    params.add(String.valueOf(this.clientId));
    params.add(videostream.getStartTimestamp());
    params.add(videostream.getData());
    try {
      getPostHttp(Config.RPC_SUBSCRIBE, params);
      return true;
    } catch (Exception ex) {
      return false;
    }
  }

  public void notifyUnsubscribe(String streamKey) throws Exception {
    Vector<Object> params = new Vector<Object>();
    params.add(streamKey);
    params.add(String.valueOf(this.clientId));
    getPostHttp(Config.RPC_UNSUBSCRIBE, params);
  }

  public void notifyUnpublish(String streamKey, Integer thumbnailCount) throws Exception {
    Vector<Object> params = new Vector<Object>();
    params.add(streamKey);
    params.add(thumbnailCount);
    params.add(String.valueOf(this.clientId));
    getPostHttp(Config.RPC_UNPUBLISH, params);
  }

  public String getPostHttp(String method, Vector<Object> params) throws Exception {
    WMSProperties requestPayload = new WMSProperties();
    requestPayload.setProperty("method", method);
    requestPayload.setProperty("params", params);
    JSONSerializer serializer = new JSONSerializer().exclude("*.class");
    String requestJson = serializer.deepSerialize(requestPayload);

    try {
      HttpURLConnection httpConnection = (HttpURLConnection) new URL(this.rpcUrl).openConnection();
      httpConnection.setDoOutput(true);
      OutputStreamWriter writer = new OutputStreamWriter(httpConnection.getOutputStream());
      writer.write(requestJson);
      writer.flush();
      Scanner scanner;

      if (httpConnection.getResponseCode() != 200) {
        throw new Exception("HTTP response code `" + httpConnection.getResponseCode() + "`.");
      }

      scanner = new Scanner(httpConnection.getInputStream());
      scanner.useDelimiter("\\Z");
      String response = scanner.next();
      JSONObject json = (JSONObject) new JSONParser().parse(response);
      JSONObject jsonSuccess = (JSONObject) json.get("success");

      return String.valueOf(jsonSuccess.get("result"));
    } catch (Exception e) {
      throw new Exception("RPC call to `" + this.rpcUrl + "` (" + requestJson + ") failed: " + e.getMessage());
    }
  }
}
