package ch.cargomedia.wms.transcoder;

import ch.cargomedia.wms.Application;
import ch.cargomedia.wms.Utils;
import ch.cargomedia.wms.stream.VideostreamPublisher;
import com.wowza.wms.logging.WMSLoggerFactory;

import java.io.File;
import java.util.TimerTask;

public class Thumbnailer extends TimerTask {

  private VideostreamPublisher _stream;
  private String _input;
  private String _pathBinCm;
  private int _width;
  private int _height;

  public Thumbnailer(VideostreamPublisher stream) {
    Application application = Application.getInstance();
    _stream = stream;
    _input = "rtmp://127.0.0.1/" + application.getName() + "/" + stream.getStreamName();
    _pathBinCm = application.getConfig().getCmBinPath();
    _width = application.getConfig().getThumbnailWidth();
    _height = (int) ((double) _width / ((stream.getWidth() / (double) stream.getHeight())));
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
          _pathBinCm,
          "stream",
          "import-video-thumbnail",
          String.valueOf(_stream.getStreamChannelId()),
          output.getAbsolutePath(),
      });

    } catch (Exception e) {
      WMSLoggerFactory.getLogger(null).error("Cannot create thumbnail: " + e.getMessage());
    }
    output.delete();
  }
}
