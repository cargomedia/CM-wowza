package ch.cargomedia.wms.transcoder;

import ch.cargomedia.wms.Config;
import ch.cargomedia.wms.Utils;
import ch.cargomedia.wms.module.eventhandler.ConnectionsListener;
import ch.cargomedia.wms.stream.VideostreamPublisher;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.stream.IMediaStream;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.TimerTask;

public class Thumbnailer extends TimerTask {

  private VideostreamPublisher _videostreamPublisher;
  private IMediaStream _stream;

  public Thumbnailer(VideostreamPublisher videostreamPublisher, IMediaStream stream) {
    this._videostreamPublisher = videostreamPublisher;
    this._stream = stream;
  }

  public void run() {
    File file = _captureThumbnail();
    if (null != file) {
      try {
        _submitThumbnail(file);
      } catch (Exception e) {
        WMSLoggerFactory.getLogger(null).error("Cannot submit thumbnail: " + e.getMessage());
      }
      file.delete();
    }
  }

  private File _captureThumbnail() {
    File file = Utils.getTempFile();
    IApplicationInstance appInstance = ConnectionsListener.appInstance;
    String inputStream = "rtmp://127.0.0.1/" + appInstance.getApplication().getName() + "/" + _stream.getName();
    int width = appInstance.getProperties().getPropertyInt(Config.XMLPROPERTY_THUMBNAIL_WIDTH, 240);
    int height = (int) ((double) width / ((_videostreamPublisher.getWidth() / (double) _videostreamPublisher.getHeight())));
    String[] command = new String[]{
        "ffmpeg",
        "-threads", "1",
        "-i", inputStream,
        "-an",
        "-vcodec", "png",
        "-vframes", "1",
        "-f", "image2",
        "-s", String.valueOf(width) + "x" + String.valueOf(height),
        "-y",
        "-loglevel", "quiet",
        file.getAbsolutePath(),
    };
    ProcessBuilder processbuilder = new ProcessBuilder(command);
    try {
      Process process = processbuilder.start();
      process.waitFor();
    } catch (Exception e) {
      file = null;
      WMSLoggerFactory.getLogger(null).error("Cannot capture thumbnail: " + e.getMessage());
    }
    return file;
  }

  private void _submitThumbnail(File file) throws Exception {
    // todo
    Logger.getLogger("ch.cargomedia.wms.module.eventhandler.StreamListener").info("hello: " + file.getAbsolutePath());
  }
}