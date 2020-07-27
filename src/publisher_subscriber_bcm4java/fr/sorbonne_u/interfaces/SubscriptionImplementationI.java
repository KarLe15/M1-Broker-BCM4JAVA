package publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces;

public interface SubscriptionImplementationI {
	public void subscribe(String topic, String inboundPortURI) throws Exception;
	public void subscribe(String[] topics, String inboundPortURI) throws Exception;
	public void subscribe(String topic, MessageFilterI filter, String inboundPortURI) throws Exception;
	public void modifyFilter(String topic, MessageFilterI newfilter, String inboundPortURI) throws Exception;
	public void unsubscribe(String topic, String inboundPortURI) throws Exception;

}
