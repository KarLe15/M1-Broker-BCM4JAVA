package publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import publisher_subscriber_bcm4java.fr.sorbonne_u.beans.CVMMyDebug;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.interfaces.BrokerPublisherI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.interfaces.BrokerSubscriberI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.interfaces.BrokerSubscriptionI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.connectors.BrokerSubscriberMessageConnector;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.BrokersCommunicationI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.MessageFilterI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.ports.*;

@OfferedInterfaces(offered = {
		BrokerPublisherI.class,
		BrokerSubscriptionI.class,
		BrokersCommunicationI.class
})
@RequiredInterfaces(required = {
		BrokerSubscriberI.class,
		BrokersCommunicationI.class
})
/**
 * This class is the broker component centralizing every communication.
 * Contains all elements regarding communications with ports.
 */
public class BrokerComponent extends AbstractComponent implements BrokersCommunicationI {

	private AtomicInteger cptMessagesReceived = new AtomicInteger(0);
	private AtomicInteger cptMessagesSent = new AtomicInteger(0);
	private AtomicInteger cptMessagesDestroyed = new AtomicInteger(0);
	private Map<Integer, AtomicInteger> mapDebug = new ConcurrentHashMap<>(){
		{
			put(0, new AtomicInteger(0));
			put(1, new AtomicInteger(0));
			put(2, new AtomicInteger(0));
			put(3, new AtomicInteger(0));
		}
	};
	
	private static final int 	SCHEDULED_THREAD = 3;
	private static final int 	RUNNING_THREAD = 3;
	private static final int 	SENDING_THREAD = 3;
	private 	   final String SENDING_EXECUTOR_POOL = "sendingPool";

	private static final String OUTBOUND_MESSAGE_PORT_SUFFIX = "_outMessage_";
	private static final String INBOUND_MESSAGE_PORT_SUFFIX  = "_inMessage_";
	//Annuary containing all elements of database handling
	private 			 Annuaire annuaire;
	//Map linking subscriber inbound port to broker outbound port
	private 			 Map<String, String> subscriberPortToOutboundPort;
	//List of inbounds ports of the broker from publisher
	private 			 List<BrokerInboundPortMessage> 	incomingMessagePort;
	//List of outbounds ports of the broker to subscriber
	private 			 List<BrokerOutboundPortMessage> sendingMessagePort;
	//Reference to the inbound port in the subscription way
	private 			 InboundPortSubscription subscriptionPort;

	private InboundPortBrokers brokersCommunicationIn;
	private OutboundPortBrokers brokerCommunicationOut;

	private 			 AtomicInteger cptSendingPort;
	private 			 AtomicInteger cptReceivingPort;
	//URI of the component
	private 			 String myURI;

//	private ExecutorService tasksToSend;

	protected BrokerComponent(
			String 	 URI,
			String   inboundPortSubscriptionURI
	) throws Exception {
		super(URI, RUNNING_THREAD, SCHEDULED_THREAD);
		assert URI != null;
		this.myURI = URI;
		this.annuaire 		  = new Annuaire();
		this.cptSendingPort   = new AtomicInteger(0);
		this.cptReceivingPort = new AtomicInteger(0);
		this.subscriberPortToOutboundPort = new ConcurrentHashMap<>();

		// initialise le port de Subscription
		this.subscriptionPort = new InboundPortSubscription(inboundPortSubscriptionURI, this);
		this.subscriptionPort.publishPort();

		// Initialise ports from Publishers
		this.incomingMessagePort = new Vector<>();
		// Initialise ports to Subscribers
		this.sendingMessagePort = new Vector<>();

		this.createNewExecutorService(SENDING_EXECUTOR_POOL, SENDING_THREAD, false);
		this.tracer.setTitle(myURI);
		this.tracer.setRelativePosition(1, 1);
		this.toggleTracing();
		this.toggleLogging();

	}
	protected BrokerComponent(
		String 	 URI,
		String   inboundPortSubscriptionURI,
		String 	 inboundPortBrokersURI,
		String   outboundPortBrokersURI
	) throws Exception {
		this(URI, inboundPortSubscriptionURI);
		System.out.println("Creating a broker component multiJVM");
		this.brokerCommunicationOut = new OutboundPortBrokers(outboundPortBrokersURI, this);
		this.brokersCommunicationIn = new InboundPortBrokers(inboundPortBrokersURI, this);
		this.brokersCommunicationIn.publishPort();
		this.brokerCommunicationOut.publishPort();
	}



	// ==========================================================================
	//                                  HELPERS
	// ==========================================================================
	/**
	 * @param urisToMessage			- List of Uris
	 * @return						- List of Port corresponding to uris given
	 */
	private List<BrokerOutboundPortMessage> getPortsFromUris(List<String> urisToMessage) {
		return urisToMessage.stream().
			map((uri -> (BrokerOutboundPortMessage)this.findPortFromURI(uri))).
			collect(Collectors.toList());
	}
	/**
	 * Create ports dynamically
	 * @return						- URI of newly created Inbound Port
	 * @throws Exception
	 */
	private String getUriInboundPortMessage() throws Exception {
		String newUri = getNewUriInboundPortMessage();
		BrokerInboundPortMessage newPort = new BrokerInboundPortMessage(newUri, this);
		newPort.publishPort();
		this.incomingMessagePort.add(newPort);
		return newUri;
	}
	/**
	 * Create and connect publisher, broker and subscriber
	 * Method required by subscriber
	 * @param inboundPortURI		- URI inbound Port 
	 * @return						- URI Outbound Port
	 * @throws Exception
	 */
	private String getURIOutboundPortMessage(String inboundPortURI) throws Exception {
		String tempUri = this.subscriberPortToOutboundPort.get(inboundPortURI);
		if(tempUri != null){
			return tempUri;
		}
		String uriNewPort = getNewUriOutboundPortMessage();
		this.subscriberPortToOutboundPort.put(inboundPortURI,uriNewPort);
		BrokerOutboundPortMessage newPort = new BrokerOutboundPortMessage(uriNewPort,this);
		this.sendingMessagePort.add(newPort);
		newPort.publishPort();
		// connection with the port
		this.doPortConnection(
			uriNewPort,
			inboundPortURI,
			BrokerSubscriberMessageConnector.class.getCanonicalName()
		);
		return uriNewPort;
	}
	/**
	 * 					- Update counter
	 * @return			- URI outbound port message
	 */
	private String getNewUriOutboundPortMessage() {
		return this.myURI + OUTBOUND_MESSAGE_PORT_SUFFIX + this.cptSendingPort.getAndIncrement();
	}
	/**
	 * 					- Update counter
	 * @return			- URI inbound port message
	 */
	private String getNewUriInboundPortMessage() {
		return this.myURI + INBOUND_MESSAGE_PORT_SUFFIX + this.cptReceivingPort.getAndIncrement();
	}
	/**
	 * 					- Display the message according to DEBUG_MODE 
	 * @param log		- String wanted to be displayed
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
	 * 					- Display the message according to DEBUG_MODE 
	 * @param log		- String wanted to be displayed 
	 * @param message	- Message 
	 * @param topic		- Topic
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
	 * 					- Display the message according to DEBUG_MODE 
	 * @param log		- String wanted to be displayed
	 * @param message	- Message
	 * @param topics	- List of topics
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
	 * 					- Display the message according to DEBUG_MODE 
	 * @param log		- String wanted to be displayed
	 * @param messages	- List of messages
	 * @param topic	- Topic
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
	 * 					- Display the message according to DEBUG_MODE 
	 * @param log		- String wanted to be displayed
	 * @param messages	- List of messages
	 * @param topics	- List of topics
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
	//                     Life Cycle
	// ==========================================================================
	/**
	 * Start the life cycle
	 * @throws ComponentStartException
	 */
	@Override
	public void start() throws ComponentStartException {
		logMessageHandler("Starting :" + myURI );
		super.start();
	}
	/**
	 * Method :		- Unpublishing all ports of lists of ports 
	 * 				- display logs for debugging 
	 * 				- Call finalise of super
	 * @throws Exception
	 */
	@Override
	public void finalise() throws Exception {
		for( BrokerInboundPortMessage bim : incomingMessagePort){
			bim.unpublishPort();
		}
		for( BrokerOutboundPortMessage bom : sendingMessagePort){
			bom.unpublishPort();
			this.doPortDisconnection(bom.getPortURI());
		}
		this.subscriptionPort.unpublishPort();
		if (this.brokerCommunicationOut != null && this.brokersCommunicationIn != null) {
			this.brokerCommunicationOut.unpublishPort();
			this.brokersCommunicationIn.unpublishPort();
			this.doPortDisconnection(this.brokerCommunicationOut.getPortURI());
		}
		logMessageHandler("Stopping :" + myURI);
		logMessage("has sent : " + this.cptMessagesSent + " messages");
		logMessage("has received : " + this.cptMessagesReceived + " messages");
		logMessage("has destroyed : " + this.cptMessagesDestroyed + " messages");
		for(Integer key : this.mapDebug.keySet()) {
			logMessage("has to send : " + mapDebug.get(key) + " messages to " + key + " personnes" );
		}
		super.finalise();
	}
	/**
	 * Method stopping life cycle
	 * @throws ComponentShutdownException
	 */
	@Override
	public void shutdown() throws ComponentShutdownException {
		logMessageHandler("Shutting down " + myURI);
		super.shutdown();
	}
	/**
	 * Method stopping life cycle
	 * @throws ComponentShutdownException
	 */
	@Override
	public void shutdownNow() throws ComponentShutdownException {
		logMessageHandler("Shutting down NOW " + myURI);
		super.shutdownNow();
	}


	// ==========================================================================
	//                     Publisher  Services
	// ==========================================================================
	/**
	 * Method offered by our broker supposed to be called by the publisher when publisher wants to publish message
	 * Update our Annuaire with a new message concerning one topic 
	 * @param m			- Message sent
	 * @param topic		- Topic concerning the message
	 * @return
	 * @throws Exception
	 */
	public Void publishService(IMessage m, String topic) throws Exception {
		this.logMessageHandler(myURI + " : has received a message", m, topic);
		if (this.brokerCommunicationOut != null) {
			this.brokerCommunicationOut.publish(m, topic, this.myURI);
		}
		annuaire.addMessage(m,topic);
		this.runTask( SENDING_EXECUTOR_POOL, (ignore) -> {
			// ignore : Type ComponentI
			try {
				acceptMessageService(m);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		this.cptMessagesReceived.incrementAndGet();
		return null;
	}
	/**
	 * Update our Annuaire with a new message concerning multiple topics
	 * @param m			- Message sent
	 * @param topics	- List of topics concerning the message
	 * @return
	 * @throws Exception
	 */
	public Void publishService(IMessage m, String[] topics) throws Exception{
		this.logMessageHandler(myURI + " : has received a message", m, topics);
		if (this.brokerCommunicationOut != null) {
			this.brokerCommunicationOut.publish(m, topics, this.myURI);
		}
		annuaire.addMessage(m,  topics);
		this.runTask( SENDING_EXECUTOR_POOL ,(ignore) -> {
			try {
				acceptMessageService(m);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		this.cptMessagesReceived.incrementAndGet();
		return null;
	}
	/**
	 * Update our Annuaire with multiples messages concerning one topic
	 * @param ms		- List of messages
	 * @param topic		- Name of topic
	 * @return
	 * @throws Exception
	 */
	public Void publishService(IMessage[] ms, String topic) throws Exception{
		this.logMessageHandler(myURI + " : has received a message", ms, topic);
		if (this.brokerCommunicationOut != null) {
			this.brokerCommunicationOut.publish(ms, topic, this.myURI);
		}
		annuaire.addMessage(ms, topic);
		this.runTask(SENDING_EXECUTOR_POOL,  (ignore) -> {
			try {
				acceptMessagesService(ms);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return null;
	}
	/**
	 * Update our Annuaire with multiple messages concerning all topics in the list of topics 
	 * @param ms		- List of messages
	 * @param topics	- List of topics
	 * @return
	 * @throws Exception
	 */
	public Void publishService(IMessage[] ms, String[] topics) throws Exception {
		this.logMessageHandler(myURI + " : has received a message", ms, topics);
		if (this.brokerCommunicationOut != null) {
			this.brokerCommunicationOut.publish(ms, topics, this.myURI);
		}
		annuaire.addMessage(ms,  topics);
		this.runTask(SENDING_EXECUTOR_POOL, (ignore) -> {
			try {
				acceptMessagesService(ms);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		return null;
	}

	// ==========================================================================
	//                      SubscriberServices  Services
	// ==========================================================================
	/**
	 * Method called to dispatch message to every subscriber interested by the topic (subscribed to this topic and in the motif filter)
	 * Once message has been dispatched Annuaire is updated and the message is removed
	 * @param iMessage		- Message to dispatch
	 * @throws Exception
	 */
	public void acceptMessageService(IMessage iMessage) throws Exception {
		annuaire.getURIsToMessage(iMessage);
		List<BrokerOutboundPortMessage> outboundPorts = this.getPortsFromUris(
			annuaire.getURIsToMessage(
				iMessage
			)
		);

		AtomicInteger oldv = this.mapDebug.getOrDefault(outboundPorts.size(), new AtomicInteger(0));
		this.mapDebug.put(outboundPorts.size(), new AtomicInteger(oldv.incrementAndGet()));
		logMessageHandler("Sending message to " + outboundPorts.size() + " subscribers");
		for (BrokerOutboundPortMessage bom : outboundPorts) {
			bom.acceptMessage(iMessage);
			this.cptMessagesSent.incrementAndGet();
		}
		annuaire.destroyMessage(iMessage);
		this.cptMessagesDestroyed.incrementAndGet();
	}
	/**
	 * Method called to dispatch a bunch of messages to subscribers
	 * @param iMessages		- List of messages
	 * @throws Exception
	 */
	public void acceptMessagesService(IMessage [] iMessages) throws Exception{
		for(IMessage m : iMessages){
			acceptMessageService(m);
		}
	}

	// ==========================================================================
	//                      Topics Services
	// ==========================================================================

	/**
	 * Update Annuaire with a new topic
	 * @param topic		- Name of topic
	 * @return
	 * @throws Exception
	 */
	public Void createTopic(String topic) throws Exception {
		logMessageHandler(myURI + " has received a request to create '" + topic + "' topic");
		if (this.brokerCommunicationOut != null) {
			this.brokerCommunicationOut.createTopic(topic, this.myURI);
		}
		annuaire.createTopic( topic);
		return null;
	}
	/**
	 * Update Annuaire with new topics
	 * @param topic		- List of topics
	 * @return
	 * @throws Exception
	 */
	public Void createTopics(String[] topic) throws Exception {
		StringBuilder msg = new StringBuilder()
			.append(myURI)
			.append(" has sent a request to create topics : ")
			.append("\n[\n");
		for (String t : topic){
			msg.append("\t").append(t).append(",\n");
		}
		logMessageHandler(msg.toString());
		if (this.brokerCommunicationOut != null) {
			this.brokerCommunicationOut.createTopics(topic, this.myURI);
		}
		annuaire.createTopics(topic);
		return null;
	}
	/**
	 * Update the annuaire removing the topic
	 * @param topic		- Name of topic to destroy
	 * @return
	 * @throws Exception
	 */
	public Void destroyTopic(String topic) throws Exception {
		logMessageHandler(myURI + " has received a request to destroy '" + topic + "' topic");
		if (this.brokerCommunicationOut != null) {
			this.brokerCommunicationOut.destroyTopic(topic, this.myURI);
		}
		annuaire.destroyTopic(topic);
		return null;
	}
	/**
	 * @param topic
	 * @return			- True if the topic is in our databases
	 * @throws Exception
	 */
	public boolean isTopic(String topic) throws Exception {
		return annuaire.isTopic(topic);
	}
	/**
	 * @return			- List of all topics in database
	 * @throws Exception
	 */
	public String[] getTopics() throws Exception {
		return annuaire.getTopics();
	}
	/**
	 *	Create and give URI of new inbound port 
	 * @return			- URI 
	 * @throws Exception
	 */
	public String getPublicationPortURI() throws Exception {
		return getUriInboundPortMessage();
	}


	// ==========================================================================
	//                      Subscriptions Services
	// ==========================================================================
	/**
	 * Method offered by broker called by subscriber to subscribe to a topic
	 * Annuaire updated with new topic for a specific URI
	 * @param topic					- Name of topic
	 * @param inboundPortURI		- Inbound port of subscriber who wants to receive messages fro a topic
	 * @return
	 * @throws Exception
	 */
	public Void subscribe(String topic, String inboundPortURI) throws Exception {
		logMessageHandler(
			myURI + " has received a subscription request " + inboundPortURI + " : " + topic
		);
		String uriNewPort = getURIOutboundPortMessage(inboundPortURI);
		annuaire.addSubscription(uriNewPort, topic);
		return null;
	}
	/**
	 * Annuaire updated with new topics for a specific URI
	 * @param topics				- List of topics
	 * @param inboundPortURI		- Inbound port of the subscriber
	 * @return
	 * @throws Exception
	 */
	public Void subscribe(String[] topics, String inboundPortURI) throws Exception {
		StringBuilder msg = new StringBuilder()
			.append(myURI)
			.append(" has received a subscription request ")
			.append(inboundPortURI)
			.append(" : [\n");
		for (String topic : topics){
			msg.append(topic).append(",\n");
		}
			msg.append(" ]");
		logMessageHandler(msg.toString());
		String uriNewPort = getURIOutboundPortMessage(inboundPortURI);
		annuaire.addSubscription(uriNewPort, topics);
		return null;
	}
	/**
	 * Annuaire updated with a topic and MessageFilter (preference) for a specific URI
	 * @param topic
	 * @param filter				- MessageFilter
	 * @param inboundPortURI		- URI of inbound port of subscriber in order to receive message
	 * @return
	 * @throws Exception
	 */
	public Void subscribe(String topic, MessageFilterI filter, String inboundPortURI) throws Exception {
		logMessageHandler(
			myURI + " has received a subscription request" + inboundPortURI + " : " + topic
		);
		String uriNewPort = getURIOutboundPortMessage(inboundPortURI);
		annuaire.addSubscription(uriNewPort, topic, filter);
		return null;
	}

	public Void modifyFilter(String topic, MessageFilterI newfilter, String inboundPortURI) throws Exception {
		logMessageHandler(
			myURI + " has received a request to modify filter" + inboundPortURI + " : " + topic
		);
		String uriNewPort = getURIOutboundPortMessage(inboundPortURI);
		annuaire.modifyFilter(uriNewPort, topic, newfilter);
		return null;
	}
	/**
	 * Method offered by Broker for subscriber in order to unsubscribe
	 * Annuaire updated removing the topic of the list of the subscription of an uri
	 * @param topic
	 * @param inboundPortURI		- Inbound port of subscriber
	 * @return
	 * @throws Exception
	 */
	public Void unsubscribe(String topic, String inboundPortURI) throws Exception {
		logMessageHandler(
			myURI + " has received a unsubscription request" + inboundPortURI + " : " + topic
		);
		String uriNewPort = getURIOutboundPortMessage(inboundPortURI);
		annuaire.removeSubscription(uriNewPort, topic);
		return null;
	}


	// ==========================================================================
	//                      Inter Brokers Communication Services
	// ==========================================================================
	@Override
	public void publish(IMessage m, String topic, String brokerURI) throws Exception {
		if (this.myURI.equals(brokerURI)) {
			return;
		}
		this.runTask( (ignore) -> {
			try {
				publishService(m, topic);
				this.brokerCommunicationOut.publish(m, topic, brokerURI);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void publish(IMessage m, String[] topics, String brokerURI) throws Exception {
		if (this.myURI.equals(brokerURI)) {
			return;
		}
		this.runTask( (ignore) -> {
			try {
				publishService(m, topics);
				this.brokerCommunicationOut.publish(m, topics, brokerURI);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void publish(IMessage[] ms, String topic, String brokerURI) throws Exception {
		if (this.myURI.equals(brokerURI)) {
			return;
		}
		this.runTask( (ignore) -> {
			try {
				publishService(ms, topic);
				this.brokerCommunicationOut.publish(ms, topic, brokerURI);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void publish(IMessage[] ms, String[] topics, String brokerURI) throws Exception {
		if (this.myURI.equals(brokerURI)) {
			return;
		}
		this.runTask( (ignore) -> {
			try {
				publishService(ms, topics);
				this.brokerCommunicationOut.publish(ms, topics, brokerURI);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void createTopic(String topic, String brokerURI) throws Exception {
		if (this.myURI.equals(brokerURI)) {
			return;
		}
		this.runTask( (ignore) -> {
			try {
				createTopic(topic);
				this.brokerCommunicationOut.createTopic(topic, brokerURI);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void createTopics(String[] topics, String brokerURI) throws Exception {
		if (this.myURI.equals(brokerURI)) {
			return;
		}
		this.runTask( (ignore) -> {
			try {
				createTopics(topics);
				this.brokerCommunicationOut.createTopics(topics, brokerURI);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void destroyTopic(String topic, String brokerURI) throws Exception {
		if (this.myURI.equals(brokerURI)) {
			return;
		}
		this.runTask( (ignore) -> {
			try {
				destroyTopic(topic);
				this.brokerCommunicationOut.destroyTopic(topic, brokerURI);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
