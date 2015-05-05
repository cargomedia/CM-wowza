package ch.cargomedia.wms;

import com.wowza.wms.application.WMSProperties;

public final class Config {
  public static final String RPC_UNSUBSCRIBE = "CM_VideoStream_Service.unsubscribe";
  public static final String RPC_UNPUBLISH = "CM_VideoStream_Service.unpublish";
  public static final String RPC_SUBSCRIBE = "CM_VideoStream_Service.subscribe";
  public static final String RPC_PUBLISH = "CM_VideoStream_Service.publish";

  private WMSProperties _properties;

  public Config(WMSProperties properties) {
    _properties = properties;
  }

  public String getCmBinPath() {
    return this._getPropertyString("cm_bin_path");
  }

  public Integer getThumbnailWidth() {
    return _properties.getPropertyInt("ThumbnailWidth", 240);
  }

  public Integer getThumbnailInterval() {
    return 10000;
  }

  public String getRpcUrl() {
    return this._getPropertyString("RPCUrl");
  }

  private String _getPropertyString(String key) {
    String value = _properties.getPropertyStr(key);
    if (null == value || 0 == value.length()) {
      throw new RuntimeException("Missing config `" + key + "`.");
    }
    return value;
  }
}