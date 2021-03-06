package ch.cargomedia.wms.transcoder;

import ch.cargomedia.wms.Application;
import ch.cargomedia.wms.Utils;
import ch.cargomedia.wms.process.ProcessSequence;
import ch.cargomedia.wms.stream.VideostreamPublisher;
import com.wowza.wms.logging.WMSLoggerFactory;

import java.io.File;

public class Archiver extends Thread {

  private ProcessSequence _processSequence = new ProcessSequence();

  private VideostreamPublisher _stream;
  private File _input;
  private String _pathBinCm;

  public Archiver(VideostreamPublisher stream) {
    Application application = Application.getInstance();
    _stream = stream;
    _input = new File(application.getStreamStoragePath() + "/" + stream.getStreamName() + ".mp4");
    _pathBinCm = application.getConfig().getCmBinPath();
  }

  public void run() {
    File output = Utils.getTempFile("mp4");
    try {
      _processSequence.addCommand(new String[]{
          "ffmpeg",
          "-threads", "1",
          "-i", _input.getAbsolutePath(),
          "-acodec", "libfaac",
          "-vcodec", "copy",
          "-ar", "22050",
          "-y",
          "-loglevel", "warning",
          output.getAbsolutePath(),
      });
      _processSequence.addCommand(new String[]{
          _pathBinCm,
          "media-streams",
          "import-archive",
          String.valueOf(_stream.getStreamChannelId()),
          output.getAbsolutePath(),
      });
      _processSequence.runAll();

    } catch (Exception e) {
      WMSLoggerFactory.getLogger(null).error("Cannot create archive: " + e.getMessage());
    }
    output.delete();
    _input.delete();
  }
}
