package ch.cargomedia.wms;

public final class Config {
  public static final String RPC_UNSUBSCRIBE = "CM_Stream_Video.unsubscribe";
  public static final String RPC_UNPUBLISH = "CM_Stream_Video.unpublish";
  public static final String RPC_SUBSCRIBE = "CM_Stream_Video.subscribe";
  public static final String RPC_PUBLISH = "CM_Stream_Video.publish";
  public static final String XMLPROPERTY_THUMBNAIL_AND_ARCHIVE_PATH = "ThumbnailAndArchivePath";
  public static final String XMLPROPERTY_THUMBNAIL_WIDTH = "ThumbnailWidth";
  public static final Integer BUCKETS_COUNT = 10000;
  public static final int THUMBNAILS_INTERVAL = 10000;
  public static final int THUMBNAILER_FFMPEG_RETRY_COUNT = 10;
}