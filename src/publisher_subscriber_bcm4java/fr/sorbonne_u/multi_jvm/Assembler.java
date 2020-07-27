package publisher_subscriber_bcm4java.fr.sorbonne_u.multi_jvm;


import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.sorbonne_u.components.pre.dcc.interfaces.DynamicComponentCreationI;
import fr.sorbonne_u.components.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.BrokerComponent;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.publisher.PublisherComponent;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscriber.SubscriberComponent;

import java.util.HashSet;
import java.util.Set;

@RequiredInterfaces(required = {
    DynamicComponentCreationI.class
})
public class Assembler extends AbstractComponent {

    public    static final String ASSEMBLER_SUFFIXE                          = "_assembler" ;
    protected static final String BROKER_URI 								 = "Broker" ;
    protected static final String BROKER_INBOUND_SUBSCRIPTION_PORT_URI 		 = "broFromSubscriptionPort" ;

    protected static final String PUBLISHER_URI 							 = "Publisher" ;

    protected static final String SUBSCRIBER_1_URI 							 = "Subscriber1" ;

    protected static final String SUBSCRIBER_2_URI 							 = "Subscriber2" ;

    protected String jvmURI;
    protected DynamicComponentCreationOutboundPort dccOutPort;

    protected String brokerURI;
    protected String uriPublisherURI;
    protected String uriSubscriber1URI;
    protected String uriSubscriber2URI;

    protected Set<String> deployerURIs;

    protected Assembler(String jvmURI) throws Exception{
        super(1,0);
        this.jvmURI = jvmURI;
        this.tracer.setTitle("Assembler") ;
        this.tracer.setRelativePosition(1, 2) ;
        this.toggleTracing();
        this.toggleLogging();
        this.deployerURIs = new HashSet<>();
    }

    @Override
    public void start() throws ComponentStartException {
        super.start();
        logMessage("Starting : Assembler");
        try {
            this.dccOutPort = new DynamicComponentCreationOutboundPort(this);
            this.dccOutPort.publishPort();

            // Connecting to the DCC
            this.doPortConnection(
                this.dccOutPort.getPortURI(),
                this.jvmURI + AbstractCVM.DCC_INBOUNDPORT_URI_SUFFIX,
                DynamicComponentCreationConnector.class.getCanonicalName()
            );

        } catch (Exception e) {
            throw new ComponentStartException(e) ;
        }
    }

    public void execute() throws Exception {
        super.execute();
        logMessage("Executing : Assembler");
        //Creation des broker
        this.brokerURI = this.dccOutPort.createComponent(
            BrokerComponent.class.getCanonicalName(),
            new Object[]{
                BROKER_URI,
                BROKER_INBOUND_SUBSCRIPTION_PORT_URI
            }
        );
        assert this.dccOutPort.isDeployedComponent(this.brokerURI);


        // TODO :: Add for loop with scenario
        //Creation des publisher
        this.uriPublisherURI = this.dccOutPort.createComponent(
            PublisherComponent.class.getCanonicalName(),
            new Object[]{
                PUBLISHER_URI,
                BROKER_INBOUND_SUBSCRIPTION_PORT_URI
            }
        );
        assert this.dccOutPort.isDeployedComponent(this.uriPublisherURI);

        // TODO :: Add for loop with scenario
        //Creation des subscriber
        this.uriSubscriber1URI = this.dccOutPort.createComponent(
            SubscriberComponent.class.getCanonicalName(),
            new Object[]{
                SUBSCRIBER_1_URI,
                BROKER_INBOUND_SUBSCRIPTION_PORT_URI
            }
        );
        assert this.dccOutPort.isDeployedComponent(this.uriSubscriber1URI);

        this.deployerURIs.add(this.brokerURI);
        this.deployerURIs.add(this.uriPublisherURI);
        this.deployerURIs.add(this.uriSubscriber1URI);
        for(String uri: this.deployerURIs) {
             this.dccOutPort.startComponent(uri);
        }
        for(String uri: this.deployerURIs) {
            this.dccOutPort.executeComponent(uri);
        }
    }

    @Override
    public void	finalise() throws Exception {
        logMessage("Finalising : Assembler");
//        for (String uri : this.deployerURIs) {
//            if (!this.dccOutPort.isFinalisedComponent(uri)){
//                this.dccOutPort.finaliseComponent(uri) ;
//            }
//        }
//        for (String uri : this.deployerURIs) {
//            if (! this.dccOutPort.isShutdownComponent(uri)){
//                this.dccOutPort.shutdownComponent(uri) ;
//            }
//        }
        this.deployerURIs.clear() ;

        this.doPortDisconnection(this.dccOutPort.getPortURI()) ;
        super.finalise();
    }

    @Override
    public void	shutdown() throws ComponentShutdownException {
        logMessage("Shutting down : Assembler");
        try {
            this.dccOutPort.unpublishPort() ;
        } catch (Exception e) {
            throw new ComponentShutdownException(e) ;
        }
        super.shutdown();
    }
}
