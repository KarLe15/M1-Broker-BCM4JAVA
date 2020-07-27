package publisher_subscriber_bcm4java.fr.sorbonne_u.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.interfaces.BrokerPublisherI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.publisher.interfaces.PublisherMessageI;

public class PublisherBrokerMessageConnector
		extends AbstractConnector
		implements PublisherMessageI {

	@Override
	public void publish(IMessage m, String topic) throws Exception {
		
		((BrokerPublisherI)this.offering).publish(m, topic);
	}

	@Override
	public void publish(IMessage m, String[] topics) throws Exception {
		
		((BrokerPublisherI)this.offering).publish(m, topics);
	}

	@Override
	public void publish(IMessage[] ms, String topic) throws Exception {
		
		((BrokerPublisherI)this.offering).publish(ms, topic);
	}

	@Override
	public void publish(IMessage[] ms, String[] topics) throws Exception {
		
		((BrokerPublisherI)this.offering).publish(ms, topics);
	}

}
