package publisher_subscriber_bcm4java.fr.sorbonne_u.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.interfaces.BrokerSubscriberI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.ReceptionCI;

public class BrokerOutboundPortMessage extends AbstractOutboundPort implements BrokerSubscriberI, OfferedI {
    public BrokerOutboundPortMessage(String uri, ComponentI owner) throws Exception {
        super(uri, BrokerSubscriberI.class, owner);
        assert uri != null && owner != null;
    }

    public BrokerOutboundPortMessage(ComponentI owner) throws Exception {
        super(BrokerSubscriberI.class, owner);
    }

    @Override
    public void acceptMessage(IMessage m) throws Exception {
        ((ReceptionCI)this.connector).acceptMessage(m);
    }

    @Override
    public void acceptMessages(IMessage[] m) throws Exception {
        ((ReceptionCI)this.connector).acceptMessages(m);
    }
}
