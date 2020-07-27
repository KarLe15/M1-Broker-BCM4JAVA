package publisher_subscriber_bcm4java.fr.sorbonne_u.components.publisher;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.exceptions.PreconditionException;
import publisher_subscriber_bcm4java.fr.sorbonne_u.beans.CVMMyDebug;
import publisher_subscriber_bcm4java.fr.sorbonne_u.beans.MessageFactory;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscription.OutboundSubscriptionI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.connectors.PublisherBrokerMessageConnector;
import publisher_subscriber_bcm4java.fr.sorbonne_u.connectors.SubscriptionBrokerConnector;
import publisher_subscriber_bcm4java.fr.sorbonne_u.greffons.PublisherPlugin;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessageFactory;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.publisher.interfaces.PublisherMessageI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.MessageFilterI;

@RequiredInterfaces(required = {
		PublisherMessageI.class,
		OutboundSubscriptionI.class,
})
public class PublisherComponent extends AbstractComponent {

	private static 			AtomicInteger   cptPublisher 			 = new AtomicInteger(0);
	public 	static 	final 	String 			PUBLISHER_PLUGIN_SUFFIX  = "_publisherPlugin";
	public 	static 	final 	String 			MESSAGE_PORT_SUFFIX 	 = "_messagePort";
	public 	static 	final 	String 			SUBSCRIPTION_PORT_SUFFIX = "_subscriptionPort";

	private static 	final 	int 			SCHEDULABLE_THREAD 		= 1;
	private static 	final 	int 			RUNNING_THREAD 			= 1;
	
	//Message factory in order to create new message
	private 		final 	IMessageFactory factory					= new MessageFactory();
	//URI of the component
	protected 		final 	String 			myURI;
	//Plugin of publisher to facilitate communication between broker and publisher
	private 				PublisherPlugin publisherPlugin;
	//URI of inbound port broker subscription
	private 			 	String			brokerInboundPortSubscriptionURI;
	protected PublisherComponent(
			String URI,
			String brokerInboundSubscriptionPort
	) throws Exception{

		super(URI, RUNNING_THREAD,SCHEDULABLE_THREAD);
		assert URI != null : new PreconditionException("uri can't be null!");
		assert brokerInboundSubscriptionPort != null
			: new PreconditionException("uri brokerinboundport subscription can't be null");

		this.myURI = URI;
		this.brokerInboundPortSubscriptionURI = brokerInboundSubscriptionPort;
		String publisherPluginURI 			= this.myURI + PUBLISHER_PLUGIN_SUFFIX;
		String outboundPortMessageURI 		= publisherPluginURI + MESSAGE_PORT_SUFFIX;
		String outboundPortSubscriptionURI 	= publisherPluginURI + SUBSCRIPTION_PORT_SUFFIX;
		this.publisherPlugin = new PublisherPlugin(
			publisherPluginURI,
			outboundPortMessageURI,
			outboundPortSubscriptionURI
		);
		this.installPlugin(this.publisherPlugin);

		this.tracer.setTitle(myURI);
		this.tracer.setRelativePosition(0, cptPublisher.getAndIncrement());
		this.toggleTracing();
		this.toggleLogging();
	}


	// ==========================================================================
	//                                  HELPERS
	// ==========================================================================
	private int iteration = 0;
	/**
	 * Method starting a scenario
	 */
	private void action() {
		++iteration;
		if(iteration > 10 ) {
			return;
		}
		IMessage m = factory.newMessage("JMD "+iteration, this.myURI );
		try {
			publishService(m, "Java infer");
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.scheduleTask(
				new AbstractComponent.AbstractTask() {
					@Override
					public void run() {
						action();
					}
				},
				10, TimeUnit.MILLISECONDS
		);
	}
	/**
	 * Display log according to the DEBUG_MODE
	 * @param log 			- Message to be displayed 
	 */
	private void logMessageHandler(String log) {
		StringBuilder toLogIntense = new StringBuilder()
			.append(log);
		if(AbstractCVM.DEBUG_MODE.contains(CVMMyDebug.INTENSE_LOGGING)) {
			logMessage(toLogIntense.toString());
		} else if (AbstractCVM.DEBUG_MODE.contains(CVMMyDebug.SIMPLE_LOGGING)) {
			logMessage(log);
		}
		if (AbstractCVM.DEBUG_MODE.contains(CVMMyDebug.PRINTING_INTENSE)) {
			AbstractCVM.getCVM().logDebug(CVMMyDebug.PRINTING_INTENSE, toLogIntense.toString());
		}
	}
	/**
	 * Display message topic and log according to the DEBUG_MODE
	 * @param log
	 * @param message
	 * @param topic
	 */
	private void logMessageHandler(String log, IMessage message, String topic){
		StringBuilder toLogIntense = new StringBuilder()
			.append(log)
			.append(":\n{\n")
			.append("\tcontent : ").append(message.getPayload()).append(",\n")
			.append("\tsender  : ").append(message.getTimeStamp().getTimestamper()).append(",\n")
			.append("\ttopic  : ").append(topic).append(",\n")
			.append("}");
		if(AbstractCVM.DEBUG_MODE.contains(CVMMyDebug.INTENSE_LOGGING)) {
			logMessage(toLogIntense.toString());
		} else if (AbstractCVM.DEBUG_MODE.contains(CVMMyDebug.SIMPLE_LOGGING)) {
			logMessage(log);
		}
		if (AbstractCVM.DEBUG_MODE.contains(CVMMyDebug.PRINTING_INTENSE)) {
			AbstractCVM.getCVM().logDebug(CVMMyDebug.PRINTING_INTENSE, toLogIntense.toString());
		}
	}
	/**
	 * Display message topics and log according to the DEBUG_MODE
	 * @param log
	 * @param message
	 * @param topic				- List of topics
	 */
	private void logMessageHandler(String log, IMessage message, String[] topics){
		StringBuilder toLogIntense = new StringBuilder()
			.append(log)
			.append(":\n{\n")
			.append("\tcontent : ").append(message.getPayload()).append(",\n")
			.append("\tsender  : ").append(message.getTimeStamp().getTimestamper()).append(",\n")
			.append("\ttopic  : [ ");
		for (String topic : topics){
			toLogIntense.append("\t\t").append(topic).append(",\n");
		}
			toLogIntense.append("\t]").append("}");
		if(AbstractCVM.DEBUG_MODE.contains(CVMMyDebug.INTENSE_LOGGING)) {
			logMessage(toLogIntense.toString());
		} else if (AbstractCVM.DEBUG_MODE.contains(CVMMyDebug.SIMPLE_LOGGING)) {
			logMessage(log);
		}
		if (AbstractCVM.DEBUG_MODE.contains(CVMMyDebug.PRINTING_INTENSE)) {
			AbstractCVM.getCVM().logDebug(CVMMyDebug.PRINTING_INTENSE, toLogIntense.toString());
		}
	}
	/**
	 * Display messages topic and log according to the DEBUG_MODE
	 * @param log
	 * @param message			- List of Messages
	 * @param topic				- Topic
	 */
	private void logMessageHandler(String log, IMessage[] messages, String topic){
		StringBuilder toLogIntense = new StringBuilder()
			.append(log)
			.append("[\n");
		for (IMessage message : messages) {
			toLogIntense.append("\t{\n")
				.append("\t\tcontent : ").append(message.getPayload()).append(",\n")
				.append("\t\tsender  : ").append(message.getTimeStamp().getTimestamper()).append(",\n")
				.append("\t\ttopic  : ").append(topic).append(",\n")
				.append("\t},\n");
		}
			toLogIntense.append("]");
		if(AbstractCVM.DEBUG_MODE.contains(CVMMyDebug.INTENSE_LOGGING)) {
			logMessage(toLogIntense.toString());
		} else if (AbstractCVM.DEBUG_MODE.contains(CVMMyDebug.SIMPLE_LOGGING)) {
			logMessage(log);
		}
		if (AbstractCVM.DEBUG_MODE.contains(CVMMyDebug.PRINTING_INTENSE)) {
			AbstractCVM.getCVM().logDebug(CVMMyDebug.PRINTING_INTENSE, toLogIntense.toString());
		}
	}
	/**
	 * Display messages topics and log according to the DEBUG_MODE
	 * @param log
	 * @param messages			- List of messages
	 * @param topics			- List of topics
	 */
	private void logMessageHandler(String log, IMessage[] messages, String[] topics){
		StringBuilder toLogIntense = new StringBuilder()
			.append(log)
			.append("[\n");
		for (IMessage message : messages) {
			toLogIntense.append("\t{\n")
				.append("\t\tcontent : ").append(message.getPayload()).append(",\n")
				.append("\t\tsender  : ").append(message.getTimeStamp().getTimestamper()).append(",\n")
				.append("\t\ttopics  : [");
			for (String topic : topics){
				toLogIntense.append("\t\t\t").append(topic).append(",\n");
			}
			toLogIntense.append("]\n").append("\t},\n");
		}
		toLogIntense.append("]");
		if(AbstractCVM.DEBUG_MODE.contains(CVMMyDebug.INTENSE_LOGGING)) {
			logMessage(toLogIntense.toString());
		} else if (AbstractCVM.DEBUG_MODE.contains(CVMMyDebug.SIMPLE_LOGGING)) {
			logMessage(log);
		}
		if (AbstractCVM.DEBUG_MODE.contains(CVMMyDebug.PRINTING_INTENSE)) {
			AbstractCVM.getCVM().logDebug(CVMMyDebug.PRINTING_INTENSE, toLogIntense.toString());
		}
	}

	// ==========================================================================
	//                                  LifeCycle
	// ==========================================================================
	/**
	 * Start life cycle of the component connecting publisher with broker and plugin
	 * @throws ComponentStartException
	 */
	@Override
	public void start() throws ComponentStartException {
		logMessageHandler("Starting :" + myURI);
		super.start();
		try {
			this.doPortConnection(
				this.publisherPlugin.getOutboundPortSubscriptionURI(),
				this.brokerInboundPortSubscriptionURI,
				SubscriptionBrokerConnector.class.getCanonicalName()
			);
			String inboundPortBroker = this.getPublicationPortURIService();
			this.doPortConnection(
				this.publisherPlugin.getOutboundPortMessageURI(),
				inboundPortBroker,
				PublisherBrokerMessageConnector.class.getCanonicalName()
			);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void execute() throws Exception{
		super.execute();
		logMessageHandler("Executing :" + myURI );
//		this.scheduleTask(
//			new AbstractComponent.AbstractTask() {
//				@Override
//				public void run() {
//					action();
//				}
//			},
//			1000, TimeUnit.MILLISECONDS
//		);
	}
	/**
	 * Call finalise of publisherPlugin, disconnecting Outbound ports with plugin and subscriber
	 * @throws Exception
	 */
	@Override
	public void finalise() throws Exception {
		this.publisherPlugin.finalise();
		logMessageHandler("Stopping :" + myURI );
		super.finalise();
		this.doPortDisconnection(this.publisherPlugin.getOutboundPortMessageURI());
		this.doPortDisconnection(this.publisherPlugin.getOutboundPortSubscriptionURI());
	}
	/**
	 * Shutdown lifecycle display log
	 * @throws ComponentShutdownException
	 */
	@Override
	public void shutdown() throws ComponentShutdownException {
		try {
//			this.publisherPlugin.uninstall();
		} catch (Exception e) {
			e.printStackTrace();
		}
		logMessageHandler("Shutting down :" + myURI );
		super.shutdown();
	}

	// ==========================================================================
	//                       Message Services
	// ==========================================================================
	/**
	 * PublishService requires a method from broker in order to publish a message.
	 * Call publisherPlugin.publishService which will use the method from broker 
	 * publisherPlugin handle all connections between ports between publisher and broker
	 * @param m				- Message
	 * @param topic			- Topic of the message
	 * @throws Exception
	 */
	public void publishService(IMessage m, String topic) throws Exception {
		this.publisherPlugin.publishService(m, topic);
		this.logMessageHandler(myURI + " : has sent a message", m, topic);
	}
	/**
	 * 
	 * @param m				- Message
	 * @param topics		- Lists of topics
	 * @throws Exception
	 */
	public void publishService(IMessage m, String[] topics) throws Exception {
		this.publisherPlugin.publishService(m, topics);
		this.logMessageHandler(myURI + " : has sent a message", m, topics);
	}
	/**
	 * 	
	 * @param ms			- List of messages
	 * @param topic			- Topic
	 * @throws Exception
	 */
	public void publishService(IMessage[] ms, String topic) throws Exception {
		this.publisherPlugin.publishService(ms, topic);
		this.logMessageHandler(myURI + " : has sent"+ ms.length +" message(s)", ms, topic);
	}
	/**
	 * 
	 * @param ms			- List of messages
	 * @param topics		- List of topics
	 * @return
	 * @throws Exception
	 */
	public Void publishService(IMessage[] ms, String[] topics) throws Exception {
		this.publisherPlugin.publishService(ms, topics);
		this.logMessageHandler(myURI + " : has sent"+ ms.length +" message(s)", ms, topics);
		return null;
	}

	// ==========================================================================
	//                    		Topic Services
	// ==========================================================================
	/**
	 * Use a method offered by broker in order to create topic
	 * Call publisherPlugin in order to handle connections between ports
	 * @param topic
	 * @throws Exception
	 */
	public void createTopicService(String topic) throws Exception {
		this.publisherPlugin.createTopicService(topic);
		this.logMessageHandler(myURI + " has sent a request to create '" + topic + "' topic");
	}

	public void createTopicsService(String[] topic) throws Exception {
		this.publisherPlugin.createTopicsService(topic);
		StringBuilder msg = new StringBuilder()
			.append(myURI)
			.append(" has sent a request to create topics : ")
			.append("\n[\n");
		for (String t : topic){
			msg.append("\t").append(t).append(",\n");
		}
		logMessageHandler(msg.toString());
	}

	public void destroyTopicService(String topic) throws Exception {
		logMessageHandler(myURI + " has sent a request to destroy '" + topic + "' topic");
		this.publisherPlugin.destroyTopicService(topic);
	}

	public boolean isTopicService(String topic) throws Exception {
		return this.publisherPlugin.isTopicService(topic);
	}

	public String[] getTopicsService() throws Exception {
		return this.publisherPlugin.getTopicsService();
	}

	public String getPublicationPortURIService() throws Exception {
		return this.publisherPlugin.getPublicationPortURIService();
	}

	// ==========================================================================
	//                     Subscription Services
	// ==========================================================================
	/**
	 * send a request of subscribe by a subscriber on publisher
	 * @param topic
	 * @throws Exception
	 */
	public void subscribeService(String topic) throws Exception {
		logMessageHandler(myURI + " has sent a request to subscribe to " + topic );
		this.publisherPlugin.subscribeService(topic);
	}
	/**
	 * 
	 * @param topics		- List of topics
	 * @throws Exception
	 */
	public void subscribeService(String[] topics) throws Exception {
		StringBuilder msg = new StringBuilder()
			.append(myURI)
			.append(" has sent a request to subscribe to topics : ")
			.append("\n[\n");
		for (String t : topics){
			msg.append("\t").append(t).append(",\n");
		}
		logMessageHandler(msg.toString());
		this.publisherPlugin.subscribeService(topics);
	}
	/**
	 * 
	 * @param topic 		- Topic
	 * @param filter		- Preference
	 * @throws Exception
	 */
	public void subscribeService(String topic, MessageFilterI filter) throws Exception {
		logMessageHandler(myURI + " has sent a request to subscribe to " + topic );
		this.publisherPlugin.subscribeService(topic, filter);
	}
	/**
	 * Modify filter of a topic already created
	 * @param topic
	 * @param newfilter
	 * @throws Exception
	 */
	public void modifyFilterService(String topic, MessageFilterI newfilter) throws Exception {
		logMessageHandler(myURI + " has sent a request to modify filter of " + topic );
		this.publisherPlugin.modifyFilterService(topic, newfilter);
	}
	/**
	 * unsubscribe to topic
	 * @param topic
	 * @throws Exception
	 */
	public void unsubscribeService(String topic) throws Exception {
		logMessageHandler(myURI + " has sent a request to unsubscribe to " + topic );
		this.publisherPlugin.unsubscribeService(topic);
	}

}
