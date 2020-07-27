package publisher_subscriber_bcm4java.fr.sorbonne_u.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.BrokersCommunicationI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;

public class BrokersCommunicationConnector
        extends AbstractConnector
        implements BrokersCommunicationI {
    @Override
    public void publish(IMessage m, String topic, String brokerURI) throws Exception {
        ((BrokersCommunicationI)this.offering).publish(m, topic, brokerURI);
    }

    @Override
    public void publish(IMessage m, String[] topics, String brokerURI) throws Exception {
        ((BrokersCommunicationI)this.offering).publish(m, topics, brokerURI);
    }

    @Override
    public void publish(IMessage[] ms, String topic, String brokerURI) throws Exception {
        ((BrokersCommunicationI)this.offering).publish(ms, topic, brokerURI);
    }

    @Override
    public void publish(IMessage[] ms, String[] topics, String brokerURI) throws Exception {
        ((BrokersCommunicationI)this.offering).publish(ms, topics, brokerURI);
    }

    @Override
    public void createTopic(String topic, String brokerURI) throws Exception {
        ((BrokersCommunicationI)this.offering).createTopic(topic, brokerURI);
    }

    @Override
    public void createTopics(String[] topics, String brokerURI) throws Exception {
        ((BrokersCommunicationI)this.offering).createTopics(topics, brokerURI);
    }

    @Override
    public void destroyTopic(String topic, String brokerURI) throws Exception {
        ((BrokersCommunicationI)this.offering).destroyTopic(topic, brokerURI);
    }
}
