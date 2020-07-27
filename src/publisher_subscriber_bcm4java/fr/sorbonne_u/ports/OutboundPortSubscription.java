package publisher_subscriber_bcm4java.fr.sorbonne_u.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractOutboundPort;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscription.OutboundSubscriptionI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.MessageFilterI;

public class OutboundPortSubscription
        extends AbstractOutboundPort
        implements OutboundSubscriptionI {

    public OutboundPortSubscription(String uri, ComponentI owner) throws Exception{
        super(uri, OutboundSubscriptionI.class,owner);
        assert uri != null && owner != null;
    }

    @Override
    public void createTopic(String topic) throws Exception {
        ((OutboundSubscriptionI)this.connector).createTopic(topic);
    }

    @Override
    public void createTopics(String[] topic) throws Exception {
        ((OutboundSubscriptionI)this.connector).createTopics(topic);
    }

    @Override
    public void destroyTopic(String topic) throws Exception {
        ((OutboundSubscriptionI)this.connector).destroyTopic(topic);
    }

    @Override
    public boolean isTopic(String topic) throws Exception {
        return ((OutboundSubscriptionI)this.connector).isTopic(topic);
    }

    @Override
    public String[] getTopics() throws Exception {
        return ((OutboundSubscriptionI)this.connector).getTopics();
    }

    @Override
    public String getPublicationPortURI() throws Exception {
        return ((OutboundSubscriptionI)this.connector).getPublicationPortURI();
    }

    @Override
    public void subscribe(String topic, String inboundPortURI) throws Exception {
        ((OutboundSubscriptionI)this.connector).subscribe(topic, inboundPortURI);
    }

    @Override
    public void subscribe(String[] topics, String inboundPortURI) throws Exception {
        ((OutboundSubscriptionI)this.connector).subscribe(topics, inboundPortURI);
    }

    @Override
    public void subscribe(String topic, MessageFilterI filter, String inboundPortURI) throws Exception {
        ((OutboundSubscriptionI)this.connector).subscribe(topic, filter, inboundPortURI);
    }

    @Override
    public void modifyFilter(String topic, MessageFilterI newfilter, String inboundPortURI) throws Exception {
        ((OutboundSubscriptionI)this.connector).modifyFilter(topic, newfilter, inboundPortURI);
    }

    @Override
    public void unsubscribe(String topic, String inboundPortURI) throws Exception {
        ((OutboundSubscriptionI)this.connector).unsubscribe(topic, inboundPortURI);
    }
}
