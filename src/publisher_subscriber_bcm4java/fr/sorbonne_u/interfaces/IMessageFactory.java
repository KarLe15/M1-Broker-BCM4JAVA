package publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces;

import publisher_subscriber_bcm4java.fr.sorbonne_u.beans.TimeStamp;

import java.io.Serializable;

public interface IMessageFactory {
	IMessage newMessage(Serializable msg, String providersURI);
	String generateUIDForMessage();
	TimeStamp getTimeStamp(long time, String providersURI);
}
