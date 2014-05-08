package ch.cargomedia.wms;

import ch.cargomedia.wms.module.eventhandler.ConnectionsListener;
import ch.cargomedia.wms.stream.VideostreamPublisher;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.stream.IMediaStream;

import java.io.File;

public class Utils {
  public static final int MP4_LIVESTREAM = 0;
  public static final int MP4_ARCHIVESTREAM = 1;

  public static String[] getArchiveFilePaths(IMediaStream stream, VideostreamPublisher videostreamPublisher) {
    String[] files = new String[2];
    IApplicationInstance appInstance = ConnectionsListener.appInstance;
    int streamId = videostreamPublisher.getStreamId();
    String md5Hash = videostreamPublisher.getClientIdMD5Hash();
    files[MP4_LIVESTREAM] = appInstance.getStreamStoragePath() + "/" + stream.getName() + ".mp4";
    String storageDir = Utils.getStoragePath(videostreamPublisher);
    files[MP4_ARCHIVESTREAM] = storageDir + "/" + String.valueOf(streamId) + "-" + md5Hash + "-" + "original" + ".mp4";
    return files;
  }

  public static String getStoragePath(VideostreamPublisher publisher) {
    int streamId = publisher.getStreamId();
    IApplicationInstance appInstance = ConnectionsListener.appInstance;
    String storagePath = appInstance.getProperties().getPropertyStr(Config.XMLPROPERTY_THUMBNAIL_AND_ARCHIVE_PATH) + "/"
        + streamId % Config.BUCKETS_COUNT;
    File storageDir = new File(storagePath);
    if (!storageDir.exists()) {
      storageDir.mkdirs();
    }
    return appInstance.getProperties().getPropertyStr(Config.XMLPROPERTY_THUMBNAIL_AND_ARCHIVE_PATH) + "/"
        + streamId % Config.BUCKETS_COUNT;
  }

  public static String getThumbnailStoragePath(VideostreamPublisher publisher) {
    int streamId = publisher.getStreamId();
    String md5Hash = publisher.getClientIdMD5Hash();
    return getStoragePath(publisher) + "/" + String.valueOf(streamId) + "-" + md5Hash + "-thumbs";
  }

  public static Integer getThumbnailCount(String path) {
    File[] thumbnailFiles = new File(path).listFiles();
    if (null == thumbnailFiles) {
      return 0;
    }
    return thumbnailFiles.length;
  }

}
