package ch.cargomedia.wms.transcoder;

import ch.cargomedia.wms.Utils;
import com.wowza.wms.logging.WMSLoggerFactory;

import java.io.File;

public class Transcoder extends Thread {

  private String inputStream;
  private String outputStream;

  public Transcoder(String inputStream, String outputStream) {
    this.inputStream = inputStream;
    this.outputStream = outputStream;
  }

  public void run() {
    String[] command = new String[]{
        "ffmpeg",
        "-threads", "1",
        "-i", inputStream,
        "-acodec", "libfaac",
        "-vcodec", "copy",
        "-ar", "22050",
        "-y",
        "-loglevel", "warning",
        outputStream,
    };
    try {
      Utils.exec(command);
      File streamFile = new File(inputStream);
      streamFile.delete();
    } catch (Exception e) {
      WMSLoggerFactory.getLogger(null).error("Error while transcoding `" + inputStream + "`: " + e.getMessage());
    }
  }

}
