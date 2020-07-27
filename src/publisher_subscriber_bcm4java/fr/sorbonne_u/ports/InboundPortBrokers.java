package publisher_subscriber_bcm4java.fr.sorbonne_u.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.BrokersCommunicationI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;

public class InboundPortBrokers extends AbstractInboundPort implements BrokersCommunicationI {

    public InboundPortBrokers(String uri, ComponentI owner) throws Exception {
        super(uri, BrokersCommunicationI.class, owner);
    }

    @Override
    public void publish(IMessage m, String topic, String brokerURI) throws Exception {
        if(this.owner instanceof BrokersCommunicationI) {
            this.getOwner().handleRequestSync(
                    owner -> {
                        BrokersCommunicationI broker = ((BrokersCommunicationI)owner);
                        broker.publish(m, topic, brokerURI);
                        return null;
                    }
            );
        }
    }

    @Override
    public void publish(IMessage m, String[] topics, String brokerURI) throws Exception {
        if(this.owner instanceof BrokersCommunicationI) {
            this.getOwner().handleRequestSync(
                    owner -> {
                        BrokersCommunicationI broker = ((BrokersCommunicationI)owner);
                        broker.publish(m, topics, brokerURI);
                        return null;
                    }
            );
        }
    }

    @Override
    public void publish(IMessage[] ms, String topic, String brokerURI) throws Exception {
        if(this.owner instanceof BrokersCommunicationI) {
            this.getOwner().handleRequestSync(
                    owner -> {
                        BrokersCommunicationI broker = ((BrokersCommunicationI)owner);
                        broker.publish(ms, topic, brokerURI);
                        return null;
                    }
            );
        }
    }

    @Override
    public void publish(IMessage[] ms, String[] topics, String brokerURI) throws Exception {
        if(this.owner instanceof BrokersCommunicationI) {
            this.getOwner().handleRequestSync(
                    owner -> {
                        BrokersCommunicationI broker = ((BrokersCommunicationI)owner);
                        broker.publish(ms, topics, brokerURI);
                        return null;
                    }
            );
        }
    }

    @Override
    public void createTopic(String topic, String brokerURI) throws Exception {
        if(this.owner instanceof BrokersCommunicationI) {
            this.getOwner().handleRequestSync(
                    owner -> {
                        BrokersCommunicationI broker = ((BrokersCommunicationI)owner);
                        broker.createTopic(topic, brokerURI);
                        return null;
                    }
            );
        }
    }

    @Override
    public void createTopics(String[] topics, String brokerURI) throws Exception {
        if(this.owner instanceof BrokersCommunicationI) {
            this.getOwner().handleRequestSync(
                    owner -> {
                        BrokersCommunicationI broker = ((BrokersCommunicationI)owner);
                        broker.createTopics(topics, brokerURI);
                        return null;
                    }
            );
        }
    }

    @Override
    public void destroyTopic(String topic, String brokerURI) throws Exception {
        if(this.owner instanceof BrokersCommunicationI) {
            this.getOwner().handleRequestSync(
                    owner -> {
                        BrokersCommunicationI broker = ((BrokersCommunicationI)owner);
                        broker.destroyTopic(topic, brokerURI);
                        return null;
                    }
            );
        }
    }
}
