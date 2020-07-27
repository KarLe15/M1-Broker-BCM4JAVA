package publisher_subscriber_bcm4java.fr.sorbonne_u.multi_jvm;

import fr.sorbonne_u.components.AbstractComponent;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import fr.sorbonne_u.components.cvm.AbstractCVM;
import fr.sorbonne_u.components.cvm.AbstractDistributedCVM;
import fr.sorbonne_u.components.exceptions.ComponentShutdownException;
import fr.sorbonne_u.components.exceptions.ComponentStartException;
import fr.sorbonne_u.components.pre.dcc.connectors.DynamicComponentCreationConnector;
import fr.sorbonne_u.components.pre.dcc.interfaces.DynamicComponentCreationI;
import fr.sorbonne_u.components.pre.dcc.ports.DynamicComponentCreationOutboundPort;
import publisher_subscriber_bcm4java.fr.sorbonne_u.DistributedCVM;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.BrokerComponent;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.publisher.PublisherComponentWithScenario;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscriber.SubscriberComponentWithScenario;
import publisher_subscriber_bcm4java.fr.sorbonne_u.connectors.BrokerSubscriberMessageConnector;
import publisher_subscriber_bcm4java.fr.sorbonne_u.connectors.BrokersCommunicationConnector;
import publisher_subscriber_bcm4java.fr.sorbonne_u.scenario.*;

import java.util.*;

@RequiredInterfaces(required = {
        DynamicComponentCreationI.class
})
public class MultiJvmAssembler extends AbstractComponent {

    private static final String BROKER_SUFFIXE                           = "Broker" ;
    private static final String BROKER_INBOUND_SUBSCRIPTION_PORT_SUFFIXE = "SubscriptionPort" ;
    private static final String BROKERS_INTER_INBOUND_PORT_SUFFIXE = "InterCommInboundPort" ;
    private static final String BROKERS_INTER_OUTBOUND_PORT_SUFFIXE = "InterCommOutboundPort";

    private final String brokerUri;
    private final String brokerInboundPortUri;
    private final String brokersCommunicationInboundPortURI;
    private final String brokersCommunicationOutboundPortURI;

    private String[] allJvmUris;
    private Scenario scenario;
    protected Set<String> deployerURIs;
    protected String jvmURI;
    protected DynamicComponentCreationOutboundPort dccOutPort;

    protected MultiJvmAssembler(String jvmURI, String reflexionURI, Scenario scenario, String[] allJvmUris) throws Exception {
        super(reflexionURI, 1,0);
        this.scenario = scenario;
        this.allJvmUris = allJvmUris;
        this.jvmURI = jvmURI;
        this.brokerUri = jvmURI + BROKER_SUFFIXE;
        this.brokerInboundPortUri = this.brokerUri + BROKER_INBOUND_SUBSCRIPTION_PORT_SUFFIXE;
        this.brokersCommunicationInboundPortURI = this.brokerUri + BROKERS_INTER_INBOUND_PORT_SUFFIXE;
        this.brokersCommunicationOutboundPortURI = this.brokerUri + BROKERS_INTER_OUTBOUND_PORT_SUFFIXE;
        this.tracer.setTitle(jvmURI + " Assembler") ;
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

    public void connectBetweenBrokers() throws Exception {
        String currentBrokerURI = this.dccOutPort.createComponent(
                BrokerComponent.class.getCanonicalName(),
                new Object[]{
                        this.brokerUri,
                        brokerInboundPortUri,
                        brokersCommunicationInboundPortURI,
                        brokersCommunicationOutboundPortURI
                }
        );
        assert currentBrokerURI.equals(this.brokerUri);
        assert this.dccOutPort.isDeployedComponent(this.brokerUri);
        this.dccOutPort.startComponent(this.brokerUri);
        assert this.dccOutPort.isStartedComponent(this.brokerUri);
        this.dccOutPort.executeComponent(this.brokerUri);
    }

    @Override
    public void	finalise() throws Exception {
        logMessage("Finalising : Assembler");
        this.deployerURIs.clear();
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

    @Override
    public void execute() throws Exception {
        logMessage("Executing : Assembler");
        int thisIndex = Arrays.asList(allJvmUris).indexOf(jvmURI);
        int otherIndex = (thisIndex + 1) % allJvmUris.length;
        String otherBrokerUri = allJvmUris[otherIndex] + BROKER_SUFFIXE;
        String otherBrokerInboundPort = otherBrokerUri + BROKERS_INTER_INBOUND_PORT_SUFFIXE;

        System.out.println("Before Port is connected to other broker");
        this.doPortConnection(
                brokersCommunicationOutboundPortURI,
                otherBrokerInboundPort,
                BrokersCommunicationConnector.class.getCanonicalName()
        );
        System.out.println("Port is connected to other broker");
        assert this.isPortConnected(brokersCommunicationOutboundPortURI);
        for(StepScenario step : scenario) {
            if (! step.getOperation().equals(OperationType.CREATE)) {
                System.exit(-3);
            }
            addComponent(step);
        }
        System.out.println("Created all Componenets");

        for(String uri: this.deployerURIs) {
            this.dccOutPort.startComponent(uri);
            assert this.dccOutPort.isStartedComponent(uri);
        }
        for(String uri: this.deployerURIs) {
            this.dccOutPort.executeComponent(uri);
        }
        super.execute();
    }

    private void addComponent(StepScenario step) throws Exception {
        String className = getComponentClassName(step);
        Object[] constructorParams = getConstructorParams(step);
        String uriComponentURI;
        uriComponentURI = this.dccOutPort.createComponent(
                className,
                constructorParams
        );
        assert this.dccOutPort.isDeployedComponent(uriComponentURI);
        this.deployerURIs.add(uriComponentURI);
    }

    private Object[] getConstructorParams(StepScenario step) {
        String componentURI = step.getURI();
        ScenarioComponent stepsComponents = step.getScenarioComponent();
        List<Object> params = new ArrayList<>();
        params.add(componentURI);
        params.add(brokerInboundPortUri);
        params.add(stepsComponents);
        if (step.getCibleType().equals(CibleType.SUBSCRIBER)) {
            params.add(step.getExpected());
        }
        return params.toArray();
    }

    private String getComponentClassName(StepScenario step) throws Exception {
        String className;
        switch (step.getCibleType()) {
            case PUBLISHER:
                className = PublisherComponentWithScenario.class.getCanonicalName();
                break;
            case SUBSCRIBER:
                className = SubscriberComponentWithScenario.class.getCanonicalName();
                break;
            default:
                throw new Exception("cannot find component for " + step.getCibleType());
        }
        return className;
    }

}
