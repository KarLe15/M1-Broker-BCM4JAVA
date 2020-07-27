package publisher_subscriber_bcm4java.fr.sorbonne_u.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscriber.SubscriberComponent;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscriber.interfaces.SubscriberMessageI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.greffons.SubscriberPlugin;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;

public class SubscriberInboundPortMessage extends AbstractInboundPort implements SubscriberMessageI {

	private static final long serialVersionUID = 1L;

	public SubscriberInboundPortMessage(String uri, ComponentI owner) throws Exception {
        super(uri, SubscriberMessageI.class, owner);
    }

    @Override
    public void acceptMessage(IMessage m) throws Exception{
	    this.getOwner().handleRequestSync(
	        owner -> {
	            return ((SubscriberComponent)this.getOwner()).acceptMessageService(m);
            }
        );
    }

    @Override
    public void acceptMessages(IMessage[] m) throws Exception{
        this.getOwner().handleRequestSync(
            owner -> {
                return ((SubscriberComponent)this.getOwner()).acceptMessagesService(m);
            }
        );
    }
}
