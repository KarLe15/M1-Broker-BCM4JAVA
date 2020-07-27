package publisher_subscriber_bcm4java.fr.sorbonne_u.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.BrokersCommunicationI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;

public class OutboundPortBrokers extends AbstractOutboundPort implements BrokersCommunicationI {
    public OutboundPortBrokers(String uri, ComponentI owner) throws Exception {
        super(uri, BrokersCommunicationI.class, owner);
        assert uri != null && owner != null;
    }

    @Override
    public void publish(IMessage m, String topic, String brokerURI) throws Exception {
        ((BrokersCommunicationI)this.connector).publish(m, topic, brokerURI);
    }

    @Override
    public void publish(IMessage m, String[] topics, String brokerURI) throws Exception {
        ((BrokersCommunicationI)this.connector).publish(m, topics, brokerURI);
    }

    @Override
    public void publish(IMessage[] ms, String topic, String brokerURI) throws Exception {
        ((BrokersCommunicationI)this.connector).publish(ms, topic, brokerURI);
    }

    @Override
    public void publish(IMessage[] ms, String[] topics, String brokerURI) throws Exception {
        ((BrokersCommunicationI)this.connector).publish(ms, topics, brokerURI);
    }

    @Override
    public void createTopic(String topic, String brokerURI) throws Exception {
        ((BrokersCommunicationI)this.connector).createTopic(topic, brokerURI);
    }

    @Override
    public void createTopics(String[] topics, String brokerURI) throws Exception {
        ((BrokersCommunicationI)this.connector).createTopics(topics, brokerURI);
    }

    @Override
    public void destroyTopic(String topic, String brokerURI) throws Exception {
        ((BrokersCommunicationI)this.connector).destroyTopic(topic, brokerURI);
    }
}
