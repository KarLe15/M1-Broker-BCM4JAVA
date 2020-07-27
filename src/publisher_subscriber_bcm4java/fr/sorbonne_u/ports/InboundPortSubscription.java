package publisher_subscriber_bcm4java.fr.sorbonne_u.ports;

import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.ports.AbstractInboundPort;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.BrokerComponent;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.interfaces.BrokerSubscriptionI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.MessageFilterI;

public class InboundPortSubscription
    extends AbstractInboundPort
    implements BrokerSubscriptionI {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InboundPortSubscription(String uri, ComponentI owner) throws Exception {
        super(uri, BrokerSubscriptionI.class, owner);
    }



    @Override
    public void createTopic(String topic) throws Exception{
    	if(this.owner instanceof BrokerComponent)
	        this.getOwner().handleRequestSync(
				owner -> {
					return ((BrokerComponent)owner).createTopic(topic);
				}
	        );
//    	if(this.owner instanceof Provider)
//    		this.getOwner().handleRequestSync(
//				owner -> {
//					return ((Provider)owner).createTopic(topic);
//				}
//            );
    }

    @Override
    public void createTopics(String[] topic) throws Exception {
    	if(this.owner instanceof BrokerComponent)
	        this.getOwner().handleRequestSync(
	                owner -> {
	                    return ((BrokerComponent)owner).createTopics(topic);
	                }
	        );
//    	if(this.owner instanceof Provider)
//	        this.getOwner().handleRequestSync(
//	                owner -> {
//	                    return ((Provider)owner).createTopics(topic);
//	                }
//	        );
    }

    @Override
    public void destroyTopic(String topic) throws Exception {
    	if(this.owner instanceof BrokerComponent)
	        this.getOwner().handleRequestSync(
	                owner -> {
	                    return ((BrokerComponent)owner).destroyTopic(topic);
	                }
	        );
//    	if(this.owner instanceof Provider)
//	    	this.getOwner().handleRequestSync(
//	                owner -> {
//	                    return ((BrokerComponent)owner).destroyTopic(topic);
//	                }
//	        );
    }

    @Override
    public boolean isTopic(String topic) throws Exception {
    	if(this.owner instanceof BrokerComponent)
	        return this.getOwner().handleRequestSync(
	                owner -> ((BrokerComponent)owner).isTopic(topic)
	        );
//    	if(this.owner instanceof Provider)
//	        return this.getOwner().handleRequestSync(
//	                owner -> ((Provider)owner).isTopic(topic)
//	        );
		return false;
    }

    @Override
    public String[] getTopics() throws Exception {
    	if(this.owner instanceof BrokerComponent)
	        return this.getOwner().handleRequestSync(
	                owner -> ((BrokerComponent)owner).getTopics()
	        );
//    	if(this.owner instanceof Provider)
//	        return this.getOwner().handleRequestSync(
//	                owner -> ((Provider)owner).getTopics()
//	        );
//
    	return null;
    }

    @Override
    public String getPublicationPortURI() throws Exception {
    	if(this.owner instanceof BrokerComponent)
	        return this.getOwner().handleRequestSync(
	                owner -> ((BrokerComponent)owner).getPublicationPortURI()
	        );
//    	if(this.owner instanceof Provider)
//	        return this.getOwner().handleRequestSync(
//	                owner -> ((Provider)owner).getPublicationPortURI()
//	        );
    	return null;
    }

    @Override
    public void subscribe(String topic, String inboundPortURI) throws Exception {
    	if(this.owner instanceof BrokerComponent)
	        this.getOwner().handleRequestSync(
	                owner -> ((BrokerComponent)owner).subscribe(topic, inboundPortURI)
	        );
//    	if(this.owner instanceof Provider)
//	        this.getOwner().handleRequestSync(
//	                owner -> ((Provider)owner).subscribe(topic, inboundPortURI)
//	        );
    }

    @Override
    public void subscribe(String[] topics, String inboundPortURI) throws Exception {
    	if(this.owner instanceof BrokerComponent)
	        this.getOwner().handleRequestSync(
	                owner -> {
	                    return ((BrokerComponent)owner).subscribe(topics, inboundPortURI);
	                }
	        );
//    	if(this.owner instanceof Provider)
//	        this.getOwner().handleRequestSync(
//	                owner -> {
//	                    return ((Provider)owner).subscribe(topics, inboundPortURI);
//	                }
//	        );
    	
    }

    @Override
    public void subscribe(String topic, MessageFilterI filter, String inboundPortURI) throws Exception {
    	if(this.owner instanceof BrokerComponent)
	        this.getOwner().handleRequestSync(
	                owner -> {
	                    return ((BrokerComponent)owner).subscribe(topic, filter, inboundPortURI);
	                }
	        );
//    	if(this.owner instanceof Provider)
//	        this.getOwner().handleRequestSync(
//	                owner -> {
//	                    return ((Provider)owner).subscribe(topic, filter, inboundPortURI);
//	                }
//	        );
    }

    @Override
    public void modifyFilter(String topic, MessageFilterI newfilter, String inboundPortURI) throws Exception {
    	if(this.owner instanceof BrokerComponent)
	        this.getOwner().handleRequestSync(
	                owner -> {
	                    return ((BrokerComponent)owner).modifyFilter(topic, newfilter, inboundPortURI);
	                }
	        );
//    	if(this.owner instanceof Provider)
//	        this.getOwner().handleRequestSync(
//	                owner -> {
//	                    return ((Provider)owner).modifyFilter(topic, newfilter, inboundPortURI);
//	                }
//	        );
    }

    @Override
    public void unsubscribe(String topic, String inboundPortURI) throws Exception {
    	if(this.owner instanceof BrokerComponent)
	        this.getOwner().handleRequestSync(
	                owner -> {
	                    return ((BrokerComponent)owner).unsubscribe(topic, inboundPortURI);
	                }
	        );
//    	if(this.owner instanceof Provider)
//	        this.getOwner().handleRequestSync(
//	                owner -> {
//	                    return ((BrokerComponent)owner).unsubscribe(topic, inboundPortURI);
//	                }
//	        );
    }
}
