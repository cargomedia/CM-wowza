package ch.cargomedia.wms.transcoder;

import ch.cargomedia.wms.Application;
import ch.cargomedia.wms.Config;
import ch.cargomedia.wms.Utils;
import ch.cargomedia.wms.module.eventhandler.ConnectionsListener;
import ch.cargomedia.wms.stream.VideostreamPublisher;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.stream.IMediaStream;

import java.io.File;
import java.util.TimerTask;

public class Thumbnailer extends TimerTask {

  private String _input;
  private int _width;
  private int _height;

  public Thumbnailer(VideostreamPublisher videostreamPublisher, IMediaStream stream) {
    IApplicationInstance appInstance = ConnectionsListener.appInstance;
    _input = "rtmp://127.0.0.1/" + appInstance.getApplication().getName() + "/" + stream.getName();
    _width = appInstance.getProperties().getPropertyInt(Config.XMLPROPERTY_THUMBNAIL_WIDTH, 240);
    _height = (int) ((double) _width / ((videostreamPublisher.getWidth() / (double) videostreamPublisher.getHeight())));
  }

  public void run() {
    File output = Utils.getTempFile("png");
    try {

      Utils.exec(new String[]{
          "ffmpeg",
          "-threads", "1",
          "-i", _input,
          "-an",
          "-vcodec", "png",
          "-vframes", "1",
          "-f", "image2",
          "-s", String.valueOf(_width) + "x" + String.valueOf(_height),
          "-y",
          "-loglevel", "warning",
          output.getAbsolutePath(),
      });

      Utils.exec(new String[]{
          Application.getInstance().getCmBinPath(),
          "wowza",
          "import-thumbnail",
          "--file=" + output.getAbsolutePath(),
      });

    } catch (Exception e) {
      WMSLoggerFactory.getLogger(null).error("Cannot create thumbnail: " + e.getMessage());
    }
    output.delete();
  }
}
