package publisher_subscriber_bcm4java.fr.sorbonne_u.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.PublicationCI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.publisher.interfaces.PublisherMessageI;

public class PublisherOutboundPortMessage extends AbstractOutboundPort implements PublisherMessageI {
	private static final long serialVersionUID = 1L;

	public PublisherOutboundPortMessage(String uri, ComponentI owner) throws Exception {
		super(uri, PublisherMessageI.class, owner);
		assert uri != null && owner != null;
	}

	@Override
	public void publish(IMessage m, String topic) throws Exception {
		((PublicationCI)this.connector).publish(m, topic);
	}

	@Override
	public void publish(IMessage m, String[] topics) throws Exception {
		((PublicationCI)this.connector).publish(m, topics);
	}

	@Override
	public void publish(IMessage[] ms, String topic) throws Exception {
		((PublicationCI)this.connector).publish(ms, topic);
	}

	@Override
	public void publish(IMessage[] ms, String[] topics) throws Exception {
		((PublicationCI)this.connector).publish(ms, topics);
	}
}
