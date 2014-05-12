package ch.cargomedia.wms;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
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

  public static String exec(String[] command) throws Exception {
    ProcessBuilder builder = new ProcessBuilder(command);
    builder.redirectErrorStream(true);
    Process process = builder.start();

    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    String line;
    String output = "";
    while ((line = reader.readLine()) != null) {
      output += line + "\n";
    }

    if (process.waitFor() != 0) {
      throw new Exception(String.format("Command exited with code `%s`. \nCommand: %s \nOutput: \n%s",
          process.exitValue(), StringUtils.join(command, " "), output));
    }

    return output;
  }

}
