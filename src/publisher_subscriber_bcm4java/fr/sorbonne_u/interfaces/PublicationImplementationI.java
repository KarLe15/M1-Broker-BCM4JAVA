package publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces;

public interface PublicationImplementationI {
	void publish(IMessage m, String topic) throws Exception;
	void publish(IMessage m, String[] topics)  throws Exception;
	void publish(IMessage[] ms, String topic) throws Exception;
	void publish(IMessage[] ms, String[] topics) throws Exception;
}
