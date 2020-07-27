package publisher_subscriber_bcm4java.fr.sorbonne_u.beans;

import java.io.Serializable;

import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;
/**
 * This class contains all the messages attributes and getters
 */
public class Message implements IMessage {

	private String URI;
	private Serializable payload;
	private Properties properties;
	private TimeStamp timeStamp;
	
	protected Message(Serializable content, String URI, TimeStamp timeStamp) {
		this.payload = content;
		this.properties = new Properties();
		this.timeStamp = timeStamp;
		this.URI = URI;
	}
	/**
	 * @return uri of the message
	 */
	@Override
	public String getURI() {
		return URI;
	}
	/**
	 * @return custom timestamp
	 */
	@Override
	public TimeStamp getTimeStamp() {
		return timeStamp;
	}
	/**
	 * @return properties
	 */
	@Override
	public Properties getProperties() {
		return properties;
	}
	
	@Override
	public Serializable getPayload() {
		return payload;
	}

}
