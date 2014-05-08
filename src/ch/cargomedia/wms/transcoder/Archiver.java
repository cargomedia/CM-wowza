package ch.cargomedia.wms.transcoder;

import ch.cargomedia.wms.Application;
import ch.cargomedia.wms.Utils;
import ch.cargomedia.wms.module.eventhandler.ConnectionsListener;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.stream.IMediaStream;

import java.io.File;

public class Archiver extends Thread {

  private File _input;

  public Archiver(IMediaStream stream) {
    IApplicationInstance appInstance = ConnectionsListener.appInstance;
    _input = new File(appInstance.getStreamStoragePath() + "/" + stream.getName() + ".mp4");
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
          "wowza",
          "import-archive",
          "--file=" + output.getAbsolutePath(),
      });

    } catch (Exception e) {
      WMSLoggerFactory.getLogger(null).error("Cannot create archive: " + e.getMessage());
    }
    output.delete();
    _input.delete();
  }
}
