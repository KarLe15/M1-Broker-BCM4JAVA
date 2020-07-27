package publisher_subscriber_bcm4java.fr.sorbonne_u.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.interfaces.BrokerSubscriberI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscriber.interfaces.SubscriberMessageI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;

public class BrokerSubscriberMessageConnector extends AbstractConnector implements BrokerSubscriberI {
    @Override
    public void acceptMessage(IMessage m) throws Exception {
        ((SubscriberMessageI)this.offering).acceptMessage(m);

    }

    @Override
    public void acceptMessages(IMessage[] m) throws Exception {
        ((SubscriberMessageI)this.offering).acceptMessages(m);
    }
}
