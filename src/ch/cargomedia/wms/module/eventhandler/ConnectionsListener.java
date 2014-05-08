package ch.cargomedia.wms.module.eventhandler;

import ch.cargomedia.wms.Application;
import ch.cargomedia.wms.stream.Videostream;
import com.wowza.util.IOPerformanceCounter;
import com.wowza.wms.amf.AMFDataItem;
import com.wowza.wms.amf.AMFDataList;
import com.wowza.wms.amf.AMFDataObj;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.application.WMSProperties;
import com.wowza.wms.client.IClient;
import com.wowza.wms.module.IModuleOnStream;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.request.RequestFunction;
import com.wowza.wms.stream.IMediaStream;


public class ConnectionsListener extends ModuleBase implements IModuleOnStream {

  public static IApplicationInstance appInstance;

  @SuppressWarnings("unused")
  public void onAppStart(IApplicationInstance applInstance) {
    appInstance = applInstance;
  }

  @SuppressWarnings("unused")
  static public void onClientBWCheck(IClient client, RequestFunction function, AMFDataList params) {
    AMFDataObj statValues = new AMFDataObj();
    IOPerformanceCounter stats = client.getTotalIOPerformanceCounter();

    statValues.put("cOutBytes", new AMFDataItem(stats.getMessagesInBytes()));
    statValues.put("cInBytes", new AMFDataItem(stats.getMessagesOutBytes()));
    statValues.put("time", new AMFDataItem(params.getLong(PARAM1)));

    sendResult(client, params, statValues);
  }


  @SuppressWarnings("unused")
  public static void onConnect(IClient client, RequestFunction function, AMFDataList params) {
    client.getProperties().setProperty("data", params.getString(3));
  }

  public void onStreamCreate(IMediaStream stream) {
    String data = stream.getClient().getProperties().getPropertyStr("data");
    data = (data == null) ? "[]" : data;
    Videostream videostream = Videostream.create(data, stream.getClient().getClientId());
    Application.getInstance().getVideostreamList().put(stream.getClient().getClientId(), videostream);

    StreamListener streamListener = new StreamListener(videostream);
    WMSProperties streamProperties = stream.getProperties();
    synchronized (streamProperties) {
      streamProperties.setProperty("streamActionNotifier", streamListener);
    }
    stream.addClientListener(streamListener);
  }

  public void onStreamDestroy(IMediaStream stream) {
    StreamListener streamListener;
    WMSProperties streamProperties = stream.getProperties();
    synchronized (streamProperties) {
      streamListener = (StreamListener) stream.getProperties().get("streamActionNotifier");
    }
    if (streamListener != null) {
      stream.removeClientListener(streamListener);
    }

    Application.getInstance().getVideostreamList().remove(stream.getClient().getClientId());
  }

}
