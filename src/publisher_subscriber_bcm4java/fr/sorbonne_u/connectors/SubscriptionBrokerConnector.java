package publisher_subscriber_bcm4java.fr.sorbonne_u.connectors;

import fr.sorbonne_u.components.connectors.AbstractConnector;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.interfaces.BrokerSubscriptionI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscription.OutboundSubscriptionI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.MessageFilterI;

public class SubscriptionBrokerConnector
    extends AbstractConnector
    implements OutboundSubscriptionI {


    @Override
    public void createTopic(String topic) throws Exception {
        ((BrokerSubscriptionI)this.offering).createTopic(topic);
    }

    @Override
    public void createTopics(String[] topic) throws Exception {
        ((BrokerSubscriptionI)this.offering).createTopics(topic);
    }

    @Override
    public void destroyTopic(String topic) throws Exception {
        ((BrokerSubscriptionI)this.offering).destroyTopic(topic);
    }

    @Override
    public boolean isTopic(String topic) throws Exception {
        return ((BrokerSubscriptionI)this.offering).isTopic(topic);
    }

    @Override
    public String[] getTopics() throws Exception {
        return ((BrokerSubscriptionI)this.offering).getTopics();
    }

    @Override
    public String getPublicationPortURI() throws Exception {
        return ((BrokerSubscriptionI)this.offering).getPublicationPortURI();
    }

    @Override
    public void subscribe(String topic, String inboundPortURI) throws Exception {
        ((BrokerSubscriptionI)this.offering).subscribe(topic, inboundPortURI);
    }

    @Override
    public void subscribe(String[] topics, String inboundPortURI) throws Exception {
        ((BrokerSubscriptionI)this.offering).subscribe(topics, inboundPortURI);
    }

    @Override
    public void subscribe(String topic, MessageFilterI filter, String inboundPortURI) throws Exception {
        ((BrokerSubscriptionI)this.offering).subscribe(topic, filter, inboundPortURI);
    }

    @Override
    public void modifyFilter(String topic, MessageFilterI newfilter, String inboundPortURI) throws Exception {
        ((BrokerSubscriptionI)this.offering).modifyFilter(topic, newfilter, inboundPortURI);
    }

    @Override
    public void unsubscribe(String topic, String inboundPortURI) throws Exception {
        ((BrokerSubscriptionI)this.offering).unsubscribe(topic, inboundPortURI);
    }
}
