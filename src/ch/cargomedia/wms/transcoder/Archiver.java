package ch.cargomedia.wms.transcoder;

import ch.cargomedia.wms.Application;
import ch.cargomedia.wms.Utils;
import ch.cargomedia.wms.module.eventhandler.ConnectionsListener;
import ch.cargomedia.wms.stream.VideostreamPublisher;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.logging.WMSLoggerFactory;

import java.io.File;

public class Archiver extends Thread {

  private VideostreamPublisher _stream;
  private File _input;

  public Archiver(VideostreamPublisher stream) {
    IApplicationInstance appInstance = ConnectionsListener.appInstance;
    _stream = stream;
    _input = new File(appInstance.getStreamStoragePath() + "/" + stream.getStreamName() + ".mp4");
  }

  public void run() {
    File output = Utils.getTempFile("mp4");
    try {

      Utils.exec(new String[]{
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

      Utils.exec(new String[]{
          Application.getInstance().getCmBinPath(),
          "stream",
          "import-video-archive",
          String.valueOf(_stream.getStreamChannelId()),
          output.getAbsolutePath(),
      });

    } catch (Exception e) {
      WMSLoggerFactory.getLogger(null).error("Cannot create archive: " + e.getMessage());
    }
    output.delete();
    _input.delete();
  }
}
