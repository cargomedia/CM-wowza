package ch.cargomedia.wms.stream;

public class VideostreamSubscriber extends Videostream {

	private VideostreamSubscriber(String data, Integer clientId) {
		super(data, clientId);
	}

	public static VideostreamSubscriber create(Videostream videostream) {
		return new VideostreamSubscriber(videostream.data, videostream.clientId);
	}

}
