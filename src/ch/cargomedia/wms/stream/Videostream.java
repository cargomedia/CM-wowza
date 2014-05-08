package ch.cargomedia.wms.stream;

import java.util.Calendar;

public class Videostream {
  protected long startTimestamp;
  protected int clientId;
  protected String data = "";

  public Videostream(String data, Integer clientId) {
    this.data = data;
    this.clientId = clientId;
    this.startTimestamp = this._initStartTime();
  }

  public static Videostream create(String data, Integer clientId) {
    return new Videostream(data, clientId);
  }

  protected long _initStartTime() {
    return Calendar.getInstance().getTimeInMillis() / 1000;
  }

  public String getData() {
    return this.data;
  }

  public int getClientId() {
    return this.clientId;
  }

  public long getStartTimestamp() {
    return this.startTimestamp;
  }
}
