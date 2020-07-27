package publisher_subscriber_bcm4java.fr.sorbonne_u.greffons;

import fr.sorbonne_u.components.AbstractPlugin;
import fr.sorbonne_u.components.ComponentI;
import fr.sorbonne_u.components.PluginI;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.publisher.interfaces.PublisherMessageI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscription.OutboundSubscriptionI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.MessageFilterI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.ports.OutboundPortSubscription;
import publisher_subscriber_bcm4java.fr.sorbonne_u.ports.PublisherOutboundPortMessage;
import publisher_subscriber_bcm4java.fr.sorbonne_u.beans.CVMMyDebug;

public class PublisherPlugin 
extends		AbstractPlugin
implements PluginI{

	private static final long serialVersionUID = 1L;

	private String outboundPortMessageURI;
	private String outboundPortSubscriptionURI;

	protected PublisherOutboundPortMessage outboundPortMessage;
	protected OutboundPortSubscription outboundPortSubscription;

	private PublisherPlugin(){
		super();
	}
	private PublisherPlugin(String outboundPortMessageURI, String outboundPortSubscriptionURI) {
		super();
		assert outboundPortMessageURI 		!= null;
		assert outboundPortSubscriptionURI 	!= null;
		this.outboundPortMessageURI = outboundPortMessageURI;
		this.outboundPortSubscriptionURI = outboundPortSubscriptionURI;
	}
	public PublisherPlugin(
		String pluginsUri,
		String outboundPortMessageURI,
		String outboundPortSubscriptionURI
	) throws Exception {
		super();
		assert pluginsUri 					!= null;
		assert outboundPortMessageURI 		!= null;
		assert outboundPortSubscriptionURI 	!= null;
		this.setPluginURI(pluginsUri);
		this.outboundPortMessageURI = outboundPortMessageURI;
		this.outboundPortSubscriptionURI = outboundPortSubscriptionURI;
	}

	public String getPublicationPortURIService() throws Exception {
		return this.outboundPortSubscription.getPublicationPortURI();
	}
	public String getOutboundPortMessageURI() {
		return this.outboundPortMessageURI;
	}
	public String getOutboundPortSubscriptionURI() {
		return this.outboundPortSubscriptionURI;
	}

	// ==========================================================================
	//                       Life Cycle
	// ==========================================================================

	public void	installOn(ComponentI owner	) throws Exception{
		super.installOn(owner);
		assert owner != null;
		assert owner.isRequiredInterface(PublisherMessageI.class);
		assert owner.isRequiredInterface(OutboundSubscriptionI.class);

		this.outboundPortMessage =
			new PublisherOutboundPortMessage(outboundPortMessageURI, this.owner);
		this.outboundPortMessage.localPublishPort();

		this.outboundPortSubscription =
			new OutboundPortSubscription(outboundPortSubscriptionURI, this.owner);
		this.outboundPortSubscription.localPublishPort();
	}
	
	public void initialise() throws Exception{
		super.initialise();
	}
	
	public void finalise() throws Exception{
		super.finalise();
	}
	
	public void uninstall() throws Exception {
		super.uninstall();
		this.outboundPortMessage.unpublishPort();
		this.outboundPortSubscription.unpublishPort();	
	}


	// ==========================================================================
	//                       Message Services
	// ==========================================================================

	public Void publishService(IMessage m, String topic) throws Exception {
		outboundPortMessage.publish(m, topic);
		return null;
	}

	public Void publishService(IMessage m, String[] topics) throws Exception {
		outboundPortMessage.publish(m, topics);
		return null;
	}

	public Void publishService(IMessage[] ms, String topic) throws Exception {
		outboundPortMessage.publish(ms, topic);
		return null;
	}

	public Void publishService(IMessage[] ms, String[] topics) throws Exception {
		outboundPortMessage.publish(ms, topics);
		return null;
	}


	// ==========================================================================
	//                     Topics Services
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

	// ==========================================================================
	//                     Subscription Services
	// ==========================================================================

	public void subscribeService(String topic) throws Exception {
		this.outboundPortSubscription.subscribe(topic, this.outboundPortMessageURI);
	}

	public void subscribeService(String[] topics) throws Exception {
		this.outboundPortSubscription.subscribe(topics, this.outboundPortMessageURI);
	}

	public void subscribeService(String topic, MessageFilterI filter) throws Exception {
		this.outboundPortSubscription.subscribe(topic, filter, this.outboundPortMessageURI);
	}

	public void modifyFilterService(String topic, MessageFilterI newfilter) throws Exception {
		this.outboundPortSubscription.modifyFilter(topic, newfilter, this.outboundPortMessageURI);
	}

	public void unsubscribeService(String topic) throws Exception {
		this.outboundPortSubscription.unsubscribe(topic, this.outboundPortMessageURI);
	}

}
