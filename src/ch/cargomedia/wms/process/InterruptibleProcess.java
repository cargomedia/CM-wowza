package ch.cargomedia.wms.process;

import com.wowza.wms.logging.WMSLoggerFactory;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InterruptibleProcess extends Thread {

  private String[] _command;
  private BufferedReader _processReader = null;
  private Boolean _interrupted = false;

  private String _output = null;
  private Integer _exitCode = null;
  private Exception _exception = null;

  public InterruptibleProcess(String[] command) {
    _command = command;
  }

  public void run() {
    try {
      ProcessBuilder builder = new ProcessBuilder(_command);
      builder.redirectErrorStream(true);
      Process _process = builder.start();

      _processReader = new BufferedReader(new InputStreamReader(_process.getInputStream()));
      String line;
      _output = "";
      while ((line = _processReader.readLine()) != null) {
        _output += line + "\n";
      }

      if (_interrupted) {
        throw new InterruptedException();
      }

      _exitCode = _process.waitFor();
      if (0 != _exitCode) {
        throw new Exception(String.format("Command exited with code `%s`. \nCommand: %s \nOutput: \n%s",
            _process.exitValue(), StringUtils.join(_command, " "), _output));
      }
    } catch (Exception e) {
      _exception = e;
    }
  }

  @Override
  public void interrupt() {
    _interrupted = true;
    this._closeProcessReader();
    super.interrupt();
  }

  public String getOutput() {
    return _output;
  }

  public Integer getExitCode() {
    return _exitCode;
  }

  public Exception getException() {
    return _exception;
  }

  public void throwExceptionIfAny() throws Exception {
    if (null != _exception) {
      throw _exception;
    }
  }

  private void _closeProcessReader() {
    if (null != _processReader) {
      try {
        _processReader.close();
        _processReader = null;
      } catch (IOException e) {
        WMSLoggerFactory.getLogger(null).error("Cannot close process reader: " + e.getMessage());
      }
    }
  }
}
