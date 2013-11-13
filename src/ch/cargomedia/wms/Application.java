package ch.cargomedia.wms;

import ch.cargomedia.wms.stream.Videostream;
import ch.cargomedia.wms.stream.VideostreamList;
import ch.cargomedia.wms.stream.VideostreamPublisher;

public class Application {

	private static Application _instance;

	private VideostreamList<Integer, Videostream> _videostreamList = new VideostreamList<Integer, Videostream>();
	private VideostreamList<String, VideostreamPublisher> _videostreamPublisherList = new VideostreamList<String, VideostreamPublisher>();

	private Application() {
	}

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

}
