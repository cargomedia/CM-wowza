package ch.cargomedia.wms.module.eventhandler;

import ch.cargomedia.wms.Application;
import ch.cargomedia.wms.rpc.RPC;
import ch.cargomedia.wms.stream.Videostream;
import ch.cargomedia.wms.stream.VideostreamList;
import ch.cargomedia.wms.stream.VideostreamPublisher;
import ch.cargomedia.wms.stream.VideostreamSubscriber;
import ch.cargomedia.wms.transcoder.Archiver;
import ch.cargomedia.wms.transcoder.Thumbnailer;
import com.wowza.wms.amf.AMFPacket;
import com.wowza.wms.application.WMSProperties;
import com.wowza.wms.media.model.MediaCodecInfoAudio;
import com.wowza.wms.media.model.MediaCodecInfoVideo;
import com.wowza.wms.stream.IMediaStream;
import com.wowza.wms.stream.IMediaStreamActionNotify3;
import org.apache.log4j.Logger;

import java.util.Timer;

public class StreamListener implements IMediaStreamActionNotify3 {

  private Videostream _videostream;
  private Thumbnailer _thumbnailer;

  public StreamListener(Videostream videostream) {
    this._videostream = videostream;
  }

  @Override
  public void onCodecInfoVideo(IMediaStream stream, MediaCodecInfoVideo mciv) {
    WMSProperties streamProperties = stream.getProperties();
    boolean isStreamPublished;
    synchronized (streamProperties) {
      isStreamPublished = streamProperties.getPropertyBoolean("isPublished", false);
      if (!isStreamPublished) {
        stream.getProperties().setProperty("isPublished", true);
      }
    }
    if (isStreamPublished) {
      return;
    }

    try {
      this._onPublish(stream, mciv);
      Logger.getLogger(this.getClass()).info("Publish success key: " + stream.getName() + " clientId: " + stream.getClientId());
    } catch (Exception e) {
      Logger.getLogger(this.getClass()).error("Publish error key: " + stream.getName() + " clientId: " + stream.getClientId() + " error: " + e.getMessage());
      stream.getClient().setShutdownClient(true);
    }
  }

  @Override
  public void onUnPublish(IMediaStream stream, String streamName, boolean isRecord, boolean isAppend) {
    try {
      this._onUnpublish(stream);
      Logger.getLogger(this.getClass()).info("Unpublish success key: " + stream.getName() + " clientId: " + stream.getClientId());
    } catch (Exception e) {
      Logger.getLogger(this.getClass()).error("Unpublish error key: " + stream.getName() + " clientId: " + stream.getClientId() + " error: " + e.getMessage());
      stream.getClient().setShutdownClient(true);
    }
  }

  @Override
  public void onPlay(IMediaStream stream, String streamName, double playStart, double playLen, int playReset) {
    if (stream.getClient().getIp().equals("127.0.0.1")) {
      return;
    }
    try {
      this._onSubscribe(stream);
      Logger.getLogger(this.getClass()).info("Subscribe success key: " + stream.getName() + " clientId: " + stream.getClientId());
    } catch (Exception e) {
      Logger.getLogger(this.getClass()).error("Subscribe error key: " + stream.getName() + " clientId: " + stream.getClientId() + " error: " + e.getMessage());
      stream.getClient().setShutdownClient(true);
    }
  }

  @Override
  public void onStop(IMediaStream stream) {
    if (stream.getClient().getIp().equals("127.0.0.1")) {
      return;
    }
    try {
      this._onUnsubscribe(stream);
      Logger.getLogger(this.getClass()).info("Unsubscribe success key: " + stream.getName() + " clientId: " + stream.getClientId());
    } catch (Exception e) {
      Logger.getLogger(this.getClass()).error("Unsubscribe error key: " + stream.getName() + " clientId: " + stream.getClientId() + " error: " + e.getMessage());
      stream.getClient().setShutdownClient(true);
    }
  }

  @Override
  public void onCodecInfoAudio(IMediaStream stream, MediaCodecInfoAudio mcia) {
  }

  @Override
  public void onMetaData(IMediaStream stream, AMFPacket amfp) {
  }

  @Override
  public void onPauseRaw(IMediaStream stream, boolean bln, double d) {
  }

  @Override
  public void onPause(IMediaStream stream, boolean isPause, double location) {
  }

  @Override
  public void onSeek(IMediaStream stream, double location) {
  }

  @Override
  public void onPublish(IMediaStream stream, String streamName, boolean isRecord, boolean isAppend) {
  }


  private void _onPublish(IMediaStream stream, MediaCodecInfoVideo mciv) throws Exception {
    VideostreamList<String, VideostreamPublisher> videostreamPublishList = Application.getInstance().getVideostreamPublisherList();
    VideostreamPublisher videostreamPublisher = VideostreamPublisher.factory(this._videostream, stream, mciv);
    if (videostreamPublishList.containsKey(stream.getName())) {
      throw new Exception("Publisher already in list");
    }

    RPC rpc = new RPC(stream.getClientId());
    int streamChannelId = rpc.getPublishStreamId(videostreamPublisher, stream.getName());
    videostreamPublisher.setStreamChannelId(streamChannelId);

    _thumbnailer = new Thumbnailer(videostreamPublisher, stream);
    Integer thumbnailInterval = Application.getInstance().getConfig().getThumbnailInterval();
    Timer timer = new Timer();
    timer.scheduleAtFixedRate(_thumbnailer, 0, thumbnailInterval);

    synchronized (videostreamPublishList) {
      videostreamPublishList.put(stream.getName(), videostreamPublisher);
    }
  }

  private void _onUnpublish(IMediaStream stream) throws Exception {
    VideostreamList<String, VideostreamPublisher> videostreamPublishList = Application.getInstance().getVideostreamPublisherList();
    VideostreamPublisher videostreamPublisher = videostreamPublishList.get(stream.getName());

    if (_thumbnailer != null) {
      _thumbnailer.interrupt();
    }

    if (videostreamPublisher != null) {
      synchronized (videostreamPublishList) {
        videostreamPublishList.remove(videostreamPublisher.getStreamName());
      }

      RPC rpc = new RPC(stream.getClient().getClientId());
      rpc.notifyUnpublish(stream.getName());

      Archiver transcoder = new Archiver(videostreamPublisher);
      transcoder.start();
    }
  }

  private void _onSubscribe(IMediaStream stream) throws Exception {
    VideostreamPublisher videostreamPublisher = Application.getInstance().getVideostreamPublisherList().need(stream.getName());
    VideostreamSubscriber videostreamSubscriber = VideostreamSubscriber.create(this._videostream);
    RPC rpc = new RPC(videostreamSubscriber.getClientId());
    if (!rpc.isAllowedToSubscribe(videostreamSubscriber, stream.getName())) {
      throw new Exception("Client not allowed to subscribe");
    }
    videostreamPublisher.getSubscribers().put(stream.getClientId(), videostreamSubscriber);
  }

  private void _onUnsubscribe(IMediaStream stream) throws Exception {
    VideostreamPublisher videostreamPublisher = Application.getInstance().getVideostreamPublisherList().need(stream.getName());
    videostreamPublisher.getSubscribers().remove(stream.getClientId());
    RPC rpc = new RPC(stream.getClientId());
    rpc.notifyUnsubscribe(stream.getName());
  }
}