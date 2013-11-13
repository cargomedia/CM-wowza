package ch.cargomedia.wms.transcoder;

import ch.cargomedia.wms.Config;
import ch.cargomedia.wms.module.eventhandler.ConnectionsListener;
import ch.cargomedia.wms.stream.VideostreamPublisher;

import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.logging.WMSLoggerFactory;
import com.wowza.wms.stream.IMediaStream;
import java.io.File;


public class Thumbnailer extends Thread {

	private Process process;
	private VideostreamPublisher _videostreamPublisher;
	private IMediaStream _stream;
	private String _storagePath;
	private boolean _reRun = true;

	public Thumbnailer(VideostreamPublisher videostreamPublisher, IMediaStream stream, String storagePath) {
		_videostreamPublisher = videostreamPublisher;
		_stream = stream;
		_storagePath = storagePath;
	}

	@Override
	public void run() {
		try {
			Integer count = 0;
			while (_reRun) {
				Integer thumbnailStartNumber = _videostreamPublisher.getThumbnailCount() + 1;
				if (count > 0) {
					WMSLoggerFactory.getLogger(null).error("Thumbnailer restarted " + count + " times");
					if (count >= Config.THUMBNAILER_FFMPEG_RETRY_COUNT) {
						throw new Exception("Thumbnailer - Giving up (Restarted " + count + " times)");
					}
				}
				count++;

				IApplicationInstance appInstance = ConnectionsListener.appInstance;
				String inputStream = "rtmp://127.0.0.1/" + appInstance.getApplication().getName() + "/" + _stream.getName();
				File storageDir = new File(_storagePath);
				if (!storageDir.exists()) {
					if (!storageDir.mkdirs()) {
						throw new Exception("Storage Path Not Created (" + _storagePath + ")" );
					}
				}
				int thumbnailWidth = appInstance.getProperties().getPropertyInt(Config.XMLPROPERTY_THUMBNAIL_WIDTH, 240);
				int thumbnailHeight = (int) ((double) thumbnailWidth / ((_videostreamPublisher.getWidth() / (double) _videostreamPublisher.getHeight())));
				Integer thumbnailInterval = Config.THUMBNAILS_INTERVAL;
				Float intervalFPS = (float) 1 / (thumbnailInterval / 1000);

				String[] command = new String[]{"ffmpeg", "-threads", "1", "-i", inputStream, "-an", "-vcodec", "png", "-f", "image2",
						"-s", String.valueOf(thumbnailWidth) + "x" + String.valueOf(thumbnailHeight),
						"-r", String.valueOf(intervalFPS), "-start_number", String.valueOf(thumbnailStartNumber),
						"-y", "-loglevel", "quiet", _storagePath + "/%d.png"};
				ProcessBuilder processbuilder = new ProcessBuilder(command);
				process = processbuilder.start();
				process.waitFor();
			}
		} catch (Exception e) {
			WMSLoggerFactory.getLogger(null).error("Thumbnail Generator Failure: " + e.getMessage());
		}
	}

	public void killRunningProcess() {
		if (null == this.process) {
			return;
		}
		_reRun = false;
		this.process.destroy();
	}
}
