package ch.cargomedia.wms.transcoder;

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
    String[] cmd = new String[]{"ffmpeg", "-threads", "1", "-i", inputStream, "-acodec", "libfaac", "-vcodec", "copy", "-ar", "22050", "-y",
        outputStream, "-loglevel", "quiet"};
    try {
      Process process = Runtime.getRuntime().exec(cmd);
      process.waitFor();

      if (process.exitValue() != 0) {
        throw new Exception("transcorder exited with code: " + process.exitValue());
      }

      File streamFile = new File(inputStream);
      streamFile.delete();
    } catch (Exception e) {
      WMSLoggerFactory.getLogger(null).error("Error while transcoding : " + outputStream + " Message: " + e.getMessage());
    }
  }

}
