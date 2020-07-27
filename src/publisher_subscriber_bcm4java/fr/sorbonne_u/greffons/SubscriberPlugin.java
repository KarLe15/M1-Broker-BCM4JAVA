package publisher_subscriber_bcm4java.fr.sorbonne_u.greffons;

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.PluginI;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import publisher_subscriber_bcm4java.fr.sorbonne_u.beans.CVMMyDebug;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscriber.SubscriberComponent;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscriber.interfaces.SubscriberMessageI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscription.OutboundSubscriptionI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.MessageFilterI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.ports.OutboundPortSubscription;
import publisher_subscriber_bcm4java.fr.sorbonne_u.ports.SubscriberInboundPortMessage;

public class SubscriberPlugin 
extends		AbstractPlugin
implements PluginI{
    private static final long serialVersionUID = 1L;


    private String inboundPortMessageURI;
    private String outboundPortSubscriptionURI;

    private SubscriberInboundPortMessage inboundPortMessage;
    private OutboundPortSubscription outboundPortSubscription;

    private SubscriberPlugin() {
        super();
    }

    private SubscriberPlugin(
        String inboundPortMessageURI,
        String outboundPortSubscriptionURI
    ) {
        super();
        assert inboundPortMessageURI        != null;
        assert outboundPortSubscriptionURI  != null;
        this.inboundPortMessageURI = inboundPortMessageURI;
        this.outboundPortSubscriptionURI = outboundPortSubscriptionURI;
    }

    public SubscriberPlugin(
        String pluginsURI,
        String inboundPortMessageURI,
        String outboundPortSubscriptionURI
    ) {
        super();
        assert pluginsURI                   != null;
        assert inboundPortMessageURI        != null;
        assert outboundPortSubscriptionURI  != null;
        this.plugInURI = pluginsURI;
        this.inboundPortMessageURI = inboundPortMessageURI;
        this.outboundPortSubscriptionURI = outboundPortSubscriptionURI;
    }

    public String getInboundPortMessageURI() {
        return inboundPortMessageURI;
    }
    public String getOutboundPortSubscriptionURI() {
        return outboundPortSubscriptionURI;
    }

    // ==========================================================================
    //                       Life Cycle
    // ==========================================================================


    public void	installOn(ComponentI owner	) throws Exception{
		super.installOn(owner);
        assert owner != null;
        assert owner.isOfferedInterface(SubscriberMessageI.class);
        assert owner.isRequiredInterface(OutboundSubscriptionI.class);


        this.inboundPortMessage =
            new SubscriberInboundPortMessage(this.inboundPortMessageURI ,this.owner);
        this.inboundPortMessage.publishPort();


        this.outboundPortSubscription =
            new OutboundPortSubscription(this.outboundPortSubscriptionURI, this.owner);
        this.outboundPortSubscription.localPublishPort();

	} 
	
	public void initialise() throws Exception {
        super.initialise();
	}
	
	public void finalise() throws Exception {
		super.finalise();
	}
	
	public void uninstall() throws Exception {
		super.uninstall();
        this.inboundPortMessage.unpublishPort();
        this.outboundPortSubscription.unpublishPort();
	}
	
	// ==========================================================================
    //                       Messages Services
    // ==========================================================================
    public Void acceptMessageService(IMessage iMessage) throws Exception{
        // TODO :: must call owner
        // TODO :: find a better way
        ((SubscriberComponent)this.owner).acceptMessageService(iMessage);
        return null;
    }

    public Void acceptMessagesService(IMessage [] iMessages){
        // TODO :: must call owner
        // TODO :: find a better way
        ((SubscriberComponent)this.owner).acceptMessagesService(iMessages);
        return null;
    }



    // ==========================================================================
    //                     Topic Services
    // ==========================================================================


    public void createTopicService(String topic) throws Exception {
        this.outboundPortSubscription.createTopic(topic);
    }

    public void createTopicsService(String[] topic) throws Exception {
        this.outboundPortSubscription.createTopics(topic);
    }

    public void destroyTopicService(String topic) throws Exception {
        this.outboundPortSubscription.destroyTopic(topic);
    }

    public boolean isTopicService(String topic) throws Exception {
        return this.outboundPortSubscription.isTopic(topic);
    }

    public String[] getTopicsService() throws Exception {
        return this.outboundPortSubscription.getTopics();
    }

    public String getPublicationPortURIService() throws Exception {
        return this.outboundPortSubscription.getPublicationPortURI();
    }

    // ==========================================================================
    //                     Subscription Services
    // ==========================================================================

    public void subscribeService(String topic) throws Exception {
        this.outboundPortSubscription.subscribe(topic, this.inboundPortMessageURI);
    }

    public void subscribeService(String[] topics) throws Exception {
        this.outboundPortSubscription.subscribe(topics, this.inboundPortMessageURI);
    }

    public void subscribeService(String topic, MessageFilterI filter) throws Exception {
        this.outboundPortSubscription.subscribe(topic, filter, this.inboundPortMessageURI);
    }

    public void modifyFilterService(String topic,MessageFilterI newfilter) throws Exception {
        this.outboundPortSubscription.modifyFilter(topic, newfilter, this.inboundPortMessageURI);
    }

    public void unsubscribeService(String topic) throws Exception {
        this.outboundPortSubscription.unsubscribe(topic, this.inboundPortMessageURI);
    }
}
