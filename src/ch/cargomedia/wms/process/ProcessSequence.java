package ch.cargomedia.wms.process;

import java.util.ArrayList;

public class ProcessSequence {

  private ArrayList<String[]> _commandList = new ArrayList<String[]>();
  private InterruptibleProcess _processWorker = null;
  private Boolean _interrupted = false;

  public void addCommand(String[] command) {
    _commandList.add(command);
  }

  public void runAll() throws Exception {
    for (String[] command : _commandList) {
      if (_interrupted) {
        throw new InterruptedException();
      }
      _processWorker = new InterruptibleProcess(command);
      _processWorker.run();
      _processWorker.join(0);
      _processWorker.throwExceptionIfAny();
      _processWorker = null;
    }
    _commandList.clear();
  }

  public void interrupt() {
    _interrupted = true;
    if (null != _processWorker) {
      _processWorker.interrupt();
    }
  }
}
