package publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker;

import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.MessageFilterI;
/**
 * This class contains preferences attributes
 */
public class Preference {
	private String topic;
	
	private MessageFilterI filters;

	public Preference(String topic, MessageFilterI filters) {
		this.topic = topic;
		this.filters = filters;
	}

	public Preference(String topic) {
		this.topic = topic;
		this.filters = null;
	}

	
	
	public String getTopic() {
		return topic;
	}

	public MessageFilterI getFilters() {
		return filters;
	}

	public void setFilters(MessageFilterI filters) {
		this.filters = filters;
	}
	
	
	
}
