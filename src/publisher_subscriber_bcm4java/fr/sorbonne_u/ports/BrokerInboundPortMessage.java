package publisher_subscriber_bcm4java.fr.sorbonne_u.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.BrokerComponent;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.interfaces.BrokerPublisherI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;

public class BrokerInboundPortMessage extends AbstractInboundPort implements BrokerPublisherI {

	private static final long serialVersionUID = 1L;
	
	public BrokerInboundPortMessage(String uri, ComponentI owner) throws Exception {
		super(uri, BrokerPublisherI.class, owner);
	}

	@Override
	public void publish(IMessage m, String topic)  throws Exception{
		if(this.owner instanceof BrokerComponent)
		this.getOwner().handleRequestSync(
				owner -> {
					BrokerComponent broker = ((BrokerComponent)owner);
					return broker.publishService(m, topic);
				}
		);
	}

	@Override
	public void publish(IMessage m, String[] topics)  throws Exception{
		if(this.owner instanceof BrokerComponent)
			this.getOwner().handleRequestSync(
					o -> ((BrokerComponent)o).publishService(m, topics)
			);
	}

	@Override
	public void publish(IMessage[] ms, String topic)  throws Exception{
		if(this.owner instanceof BrokerComponent)
			this.getOwner().handleRequestSync(
					o -> ((BrokerComponent)o).publishService(ms, topic)
			);
	}

	@Override
	public void publish(IMessage[] ms, String[] topics)  throws Exception{
		if(this.owner instanceof BrokerComponent)
			this.getOwner().handleRequestSync(
					o -> ((BrokerComponent)o).publishService(ms, topics)
			);
	}


}
