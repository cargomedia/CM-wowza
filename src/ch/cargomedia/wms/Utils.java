package ch.cargomedia.wms;

import java.io.File;
import java.util.UUID;

public class Utils {

  public static File getTempFile(String extension) {
    String dirPath = System.getProperty("java.io.tmpdir") + "/" + "wowza-cm";
    File dir = new File(dirPath);
    if (!dir.exists()) {
      dir.mkdirs();
    }
    String filename = UUID.randomUUID().toString();
    if (null != extension) {
      filename += "." + extension;
    }
    return new File(dir + "/" + filename);
  }

  public static File getTempFile() {
    return getTempFile(null);
  }

}
