package ch.cargomedia.wms.stream;

import com.wowza.util.MD5DigestUtils;
import com.wowza.wms.media.model.MediaCodecInfoVideo;
import com.wowza.wms.stream.IMediaStream;

public class VideostreamPublisher extends Videostream {

  private VideostreamList<Integer, VideostreamSubscriber> _subscribers = new VideostreamList<Integer, VideostreamSubscriber>();
  private String _streamName;
  private Integer _width;
  private Integer _height;
  private Integer _streamId = 0;
  private Integer _thumbnailCount = 0;
  private String _clientIdMD5Hash = "";
  private Long _startTime;

  private VideostreamPublisher(String data, Integer clientId, String streamName, Integer width, Integer height, String md5hash) {
    super(data, clientId);
    this._streamName = streamName;
    this._height = height;
    this._width = width;
    this._startTime = System.currentTimeMillis();
    this._clientIdMD5Hash = md5hash;
  }

  public static VideostreamPublisher factory(Videostream videoStream, IMediaStream stream, MediaCodecInfoVideo mciv) {
    String md5hash = MD5DigestUtils.generateHash(String.valueOf(stream.getClientId()));
    return new VideostreamPublisher(videoStream.data, videoStream.clientId, stream.getName(), mciv.getVideoWidth(), mciv.getVideoHeight(), md5hash);
  }

  public VideostreamList<Integer, VideostreamSubscriber> getSubscribers() {
    return _subscribers;
  }

  public synchronized void setStreamId(Integer streamId) {
    this._streamId = streamId;
  }

  public synchronized void setThumbnailCount(Integer thumbnailCount) {
    this._thumbnailCount = thumbnailCount;
  }

  public synchronized void setClientIdMD5Hash(String clientIdMD5Hash) {
    this._clientIdMD5Hash = clientIdMD5Hash;
  }

  public String getStreamName() {
    return _streamName;
  }

  public Integer getWidth() {
    return _width;
  }

  public Integer getHeight() {
    return _height;
  }

  public Integer getStreamId() {
    return _streamId;
  }

  public Integer getThumbnailCount() {
    return _thumbnailCount;
  }

  public String getClientIdMD5Hash() {
    return _clientIdMD5Hash;
  }

  @SuppressWarnings("unused")
  public Long getStartTime() {
    return _startTime;
  }

}
