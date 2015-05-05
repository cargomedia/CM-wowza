package ch.cargomedia.wms.transcoder;

import ch.cargomedia.wms.Application;
import ch.cargomedia.wms.Utils;
import ch.cargomedia.wms.process.ProcessSequence;
import ch.cargomedia.wms.stream.VideostreamPublisher;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.stream.IMediaStream;

import java.io.File;
import java.util.TimerTask;

public class Thumbnailer extends TimerTask {

  private ProcessSequence _processSequence = new ProcessSequence();

  private VideostreamPublisher _stream;
  private String _input;
  private String _pathBinCm;
  private int _width;
  private int _height;


  public Thumbnailer(VideostreamPublisher stream, IMediaStream mediaStream) {
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
      _processSequence.addCommand(new String[]{
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
      _processSequence.addCommand(new String[]{
          _pathBinCm,
          "video-stream",
          "import-thumbnail",
          String.valueOf(_stream.getStreamChannelId()),
          output.getAbsolutePath(),
      });
      _processSequence.runAll();

    } catch (InterruptedException e) {
      WMSLoggerFactory.getLogger(null).info("Thumbnail creation interrupted.");
    } catch (Exception e) {
      WMSLoggerFactory.getLogger(null).error("Cannot create thumbnail: " + e.getMessage());
    }
    output.delete();
  }

  public void interrupt() {
    this.cancel();
    _processSequence.interrupt();
  }
}
