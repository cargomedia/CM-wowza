package ch.cargomedia.wms;

import ch.cargomedia.wms.stream.Videostream;
import ch.cargomedia.wms.stream.VideostreamList;
import ch.cargomedia.wms.stream.VideostreamPublisher;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.logging.WMSLoggerFactory;

public class Application {

  private static Application _instance;

  private Config _config = null;
  private IApplicationInstance _appInstance = null;

  private VideostreamList<Integer, Videostream> _videostreamList = new VideostreamList<Integer, Videostream>();
  private VideostreamList<String, VideostreamPublisher> _videostreamPublisherList = new VideostreamList<String, VideostreamPublisher>();

  public static Application getInstance() {
    if (null == _instance) {
      _instance = new Application();
    }
    return _instance;
  }

  public VideostreamList<Integer, Videostream> getVideostreamList() {
    return this._videostreamList;
  }

  public VideostreamList<String, VideostreamPublisher> getVideostreamPublisherList() {
    return this._videostreamPublisherList;
  }

  public Config getConfig() {
    if (null == _config) {
      _config = new Config(this.getAppInstance().getProperties());
    }
    return _config;
  }

  public String getName() {
    return this.getAppInstance().getApplication().getName();
  }

  public String getStreamStoragePath() {
    return this.getAppInstance().getStreamStoragePath();
  }

  public void setAppInstance(IApplicationInstance appInstance) {
    WMSLoggerFactory.getLogger(null).info("setAppInstance: " + appInstance.getName());
    this._appInstance = appInstance;
  }

  public IApplicationInstance getAppInstance() {
    if (null == _appInstance) {
      throw new RuntimeException("Missing appInstance");
    }
    return _appInstance;
  }

}
