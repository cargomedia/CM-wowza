package ch.cargomedia.wms.stream;

import java.util.HashMap;

public class VideostreamList<K, V> extends HashMap<K, V> {

  public synchronized V need(Object key) throws Exception {
    if (!this.containsKey(key)) {
      throw new Exception("VideostreamList does not contain: " + String.valueOf(key));
    }
    return this.get(key);
  }
}
