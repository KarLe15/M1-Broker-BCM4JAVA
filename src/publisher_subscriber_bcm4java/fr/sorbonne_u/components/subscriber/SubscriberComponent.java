package publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscriber;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.exceptions.PreconditionException;
import publisher_subscriber_bcm4java.fr.sorbonne_u.beans.CVMMyDebug;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscriber.interfaces.SubscriberMessageI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscription.OutboundSubscriptionI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.connectors.SubscriptionBrokerConnector;
import publisher_subscriber_bcm4java.fr.sorbonne_u.greffons.SubscriberPlugin;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.MessageFilterI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.ports.OutboundPortSubscription;
import publisher_subscriber_bcm4java.fr.sorbonne_u.ports.SubscriberInboundPortMessage;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@OfferedInterfaces(offered = {
        SubscriberMessageI.class,
})
@RequiredInterfaces(required = {
        OutboundSubscriptionI.class,
})
public class SubscriberComponent extends AbstractComponent {

    private static          AtomicInteger   cptSubscriber            = new AtomicInteger(0);
    public 	static 	final 	String 			SUBSCRIBER_PLUGIN_SUFFIX = "_subscriberPlugin";
    public 	static 	final 	String 			MESSAGE_PORT_SUFFIX 	 = "_messagePort";
    public 	static 	final 	String          SUBSCRIPTION_PORT_SUFFIX = "_subscriptionPort";

    private static  final   int             SCHEDULABLE_THREAD       = 1;
    private static  final   int             RUNNING_THREAD           = 1;

    protected               String          myURI;
    private                 SubscriberPlugin subscriberPlugin;

    private 			 	String			brokerInboundPortSubscriptionURI;

    protected SubscriberComponent(
            String URI,
            String brokerInboundPortSubscriptionURI
    ) throws Exception {
        super(URI, SCHEDULABLE_THREAD, RUNNING_THREAD);
        assert URI != null : new PreconditionException("uri can't be null!");
        myURI = URI;
        assert brokerInboundPortSubscriptionURI != null :
             new PreconditionException("broker uri can't be null!");
        this.brokerInboundPortSubscriptionURI = brokerInboundPortSubscriptionURI;
        String publisherPluginURI 			= this.myURI + SUBSCRIBER_PLUGIN_SUFFIX;
        String inboundPortMessageURI 		= publisherPluginURI + MESSAGE_PORT_SUFFIX;
        String outboundPortSubscriptionURI 	= publisherPluginURI + SUBSCRIPTION_PORT_SUFFIX;
        this.subscriberPlugin = new SubscriberPlugin(
            publisherPluginURI,
            inboundPortMessageURI,
            outboundPortSubscriptionURI
        );
        this.installPlugin(this.subscriberPlugin);

        this.tracer.setTitle(myURI);
        this.tracer.setRelativePosition(2, cptSubscriber.getAndIncrement());
        this.toggleTracing();
        this.toggleLogging();
    }

    // ==========================================================================
    //                                  HELPERS
    // ==========================================================================
    /**
     * Display the log according to the DEBUG_MODE  
     * @param log 		- A message
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
    private void logMessageHandler(String log, IMessage message){
        StringBuilder toLogIntense = new StringBuilder()
            .append(log)
            .append(":\n{\n")
            .append("\tcontent : ").append(message.getPayload()).append(",\n")
            .append("\tsender  : ").append(message.getTimeStamp().getTimestamper()).append(",\n")
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

    // ==========================================================================
    //                                  LifeCycle
    // ==========================================================================

    @Override
    public void start() throws ComponentStartException {
        logMessageHandler("Starting : " + myURI );
        super.start();
        try{
            this.doPortConnection(
                this.subscriberPlugin.getOutboundPortSubscriptionURI(),
                this.brokerInboundPortSubscriptionURI,
                SubscriptionBrokerConnector.class.getCanonicalName()
            );
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void execute() throws Exception {
        super.execute();
//        this.scheduleTask((ignore) -> {
//            try {
//                subscribeService("Java infer");
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        },50, TimeUnit.MILLISECONDS);
//        subscribeService("Java infer");
//        logMessageHandler("executing : " + myURI);
    }
    @Override
    public void finalise() throws Exception {
        this.subscriberPlugin.finalise();
        logMessageHandler("Stopping :" + myURI);
        super.finalise();
    }

    @Override
    public void shutdown() throws ComponentShutdownException {
        try{
            this.doPortDisconnection(this.subscriberPlugin.getOutboundPortSubscriptionURI());
//            this.subscriberPlugin.uninstall();
        } catch (Exception e){
            e.printStackTrace();
        }
        logMessageHandler("Shut down :" + myURI);
        super.shutdown();
    }


    // ==========================================================================
    //                       Messages Services
    // ==========================================================================
    /**
     * The publisher alerts the subscriber that a new message has been sent (a message from a topic he has subscribed to) 
     * @param iMessage 		- A message sent by the publisher alerts the subscriber
     * @throws Exception
     */
    public Void acceptMessageService(IMessage iMessage) throws Exception{
        StringBuilder msg = new StringBuilder()
            .append(myURI)
            .append(" : has received a message from ")
            .append(iMessage.getTimeStamp()
            .getTimestamper());
        logMessageHandler(msg.toString(), iMessage);
        return null;
    }
    /**
     * The publisher alerts the subscriber that new message has been sent (message from topics he may have subscribed to) 
     * @param iMessage 		- A list of messages sent by the publisher alerts the subscriber
     * @throws Exception
     */
    public Void acceptMessagesService(IMessage [] iMessages){
        StringBuilder msg = new StringBuilder()
            .append(myURI)
            .append(" : has received a message from ")
            .append(iMessages[0].getTimeStamp()
            .getTimestamper());
        logMessageHandler(msg.toString());
//        if(AbstractCVM.DEBUG_MODE.contains(CVMMyDebug.SIMPLE_LOGGING)) {
//            StringBuilder msg = new StringBuilder();
//            msg.append(myURI).append(" : has received messages : \n");
//            for (IMessage m : iMessages) {
//                msg.append("\t").append(m.getURI()).append('\n');
//            }
//            logMessage(msg.toString());
//        }
        return null;
    }


    // ==========================================================================
    //                     Topics Services
    // ==========================================================================

    /**
     * Method called by the publisher to create a topic if it does not already exist 
     * @param topic 		- A topic 
     * @throws Exception	
     */
    public void createTopicService(String topic) throws Exception {
        this.subscriberPlugin.createTopicService(topic);
        logMessageHandler(myURI + " has sent a request to create '" + topic + "' topic");
    }
    /**
     * Method called by the publisher to create multiple topics if they do not already exist 
     * @param topic 		- A list of topics 
     * @throws Exception	
     */
    public void createTopicsService(String[] topic) throws Exception {
        StringBuilder msg = new StringBuilder()
            .append(myURI)
            .append(" has sent a request to create topics : ")
            .append("\n[\n");
        for (String t : topic){
            msg.append("\t").append(t).append(",\n");
        }
        logMessageHandler(msg.toString());
        this.subscriberPlugin.createTopicsService(topic);
    }
    /**
     * Method called by the subscriber when it receives all messages concerning a topic
     * @param topic 		- A topic
     * @throws Exception
     */
    public void destroyTopicService(String topic) throws Exception {
        logMessageHandler(myURI + " has sent a request to destroy '" + topic + "' topic");
        this.subscriberPlugin.destroyTopicService(topic);
    }
    
    /**
     * 
     * @param topic 		- A topic
     * @return True if a topic exists
     * @throws Exception
     */
    public boolean isTopicService(String topic) throws Exception {
        return this.subscriberPlugin.isTopicService(topic);
    }
    /**
     * @return An array of all topics the subscriber is subscribed to
     * @throws Exception
     */
    public String[] getTopicsService() throws Exception {
        return this.subscriberPlugin.getTopicsService();
    }
    /**
     * 
     * @return The URI of the outbound port of the publisher 
     * @throws Exception
     */
    public String getPublicationPortURIService() throws Exception {
        return this.subscriberPlugin.getPublicationPortURIService();
    }

    // ==========================================================================
    //                     Subscription Services
    // ==========================================================================

    /**
     * Method called by the Subscriber to subscribe to a topic (Broker)
     * @param topic 		- A topic
     * @throws Exception	
     */
    public void subscribeService(String topic) throws Exception {
        logMessageHandler(myURI + " has sent a request to subscribe to " + topic );
        this.subscriberPlugin.subscribeService(topic);
    }
    /**
     * Method called by the Subscriber to subscribe to a topic (Broker)
     * @param topic 		- A list of topics
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
        this.subscriberPlugin.subscribeService(topics);
    }
    /**
     * Method called by the subscriber to the broker to subscribe to a topic with certain specificities
     * @param topic			- A topic
     * @param filter		- A filter allowing the user to refine his subscription
     * @throws Exception
     */
    public void subscribeService(String topic, MessageFilterI filter) throws Exception {
        logMessageHandler(myURI + " has sent a request to subscribe to " + topic );
        this.subscriberPlugin.subscribeService(topic, filter);
    }
    /**
     * Method called by the subscriber to the broker to modify his existing subscription with a new filter
     * @param topic			- A topic
     * @param filter		- A filter allowing the user to refine his subscription
     * @throws Exception
     */
    public void modifyFilterService(String topic, MessageFilterI newfilter) throws Exception {
        logMessageHandler(myURI + " has sent a request to modify filter of " + topic );
        this.subscriberPlugin.modifyFilterService(topic, newfilter);
    }
    /**
     * Method called by the subscriber to the broker to unsubscribe to a topic
     * @param topic			- A topic
     * @throws Exception
     */
    public void unsubscribeService(String topic) throws Exception {
        logMessageHandler(myURI + " has sent a request to unsubscribe to " + topic );
        this.subscriberPlugin.unsubscribeService(topic);
    }
}
