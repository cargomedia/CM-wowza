package ch.cargomedia.wms.transcoder;

import ch.cargomedia.wms.Utils;
import ch.cargomedia.wms.module.eventhandler.ConnectionsListener;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.stream.IMediaStream;
import org.apache.log4j.Logger;

import java.io.File;

public class Archiver extends Thread {

  private File _input;

  public Archiver(IMediaStream stream) {
    IApplicationInstance appInstance = ConnectionsListener.appInstance;
    _input = new File(appInstance.getStreamStoragePath() + "/" + stream.getName() + ".mp4");
  }

  public void run() {
    File file = _getFile();
    if (null != file) {
      try {
        _submitFile(file);
      } catch (Exception e) {
        WMSLoggerFactory.getLogger(null).error("Cannot submit archive: " + e.getMessage());
      }
      file.delete();
    }
  }

  private File _getFile() {
    File file = Utils.getTempFile("mp4");
    String[] command = new String[]{
        "ffmpeg",
        "-threads", "1",
        "-i", _input.getAbsolutePath(),
        "-acodec", "libfaac",
        "-vcodec", "copy",
        "-ar", "22050",
        "-y",
        "-loglevel", "warning",
        file.getAbsolutePath(),
    };
    try {
      Utils.exec(command);
      _input.delete();
    } catch (Exception e) {
      file = null;
      WMSLoggerFactory.getLogger(null).error("Error while transcoding: " + e.getMessage());
    }
    return file;
  }

  private void _submitFile(File file) throws Exception {
    // todo
    Logger.getLogger("ch.cargomedia.wms.module.eventhandler.StreamListener").info("hello: " + file.getAbsolutePath());
  }
}