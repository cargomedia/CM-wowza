package ch.cargomedia.wms.transcoder;

import ch.cargomedia.wms.Utils;
import ch.cargomedia.wms.stream.VideostreamPublisher;

import java.util.TimerTask;

public class ThumbnailCount extends TimerTask {
	private String storagePath;
	private VideostreamPublisher videostreamPublisher;

	public ThumbnailCount(VideostreamPublisher videostreamPublisher, String storagePath) {
		this.storagePath = storagePath;
		this.videostreamPublisher = videostreamPublisher;
	}

	public void run() {
		Integer thumbnailCount = Utils.getThumbnailCount(storagePath);
		this.videostreamPublisher.setThumbnailCount(thumbnailCount);
	}

	public void shutdown() {
		this.cancel();
	}
}