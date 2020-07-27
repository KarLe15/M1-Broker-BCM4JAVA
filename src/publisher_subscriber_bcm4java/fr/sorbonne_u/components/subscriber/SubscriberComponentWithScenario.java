package publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscriber;

import fr.sorbonne_u.components.annotations.OfferedInterfaces;
import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscriber.interfaces.SubscriberMessageI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscription.OutboundSubscriptionI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.MessageFilterI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.scenario.ScenarioComponent;
import publisher_subscriber_bcm4java.fr.sorbonne_u.scenario.StepComponent;
import publisher_subscriber_bcm4java.fr.sorbonne_u.scenario.StepComponentSubscriber;

import java.util.concurrent.atomic.AtomicInteger;

@OfferedInterfaces(offered = {
    SubscriberMessageI.class,
})
@RequiredInterfaces(required = {
    OutboundSubscriptionI.class,
})
public class SubscriberComponentWithScenario extends SubscriberComponent {
    private ScenarioComponent scenario;
    private int expected;
    private AtomicInteger cptMessage = new AtomicInteger(0);
    protected SubscriberComponentWithScenario(
        String URI,
        String brokerInboundPortSubscriptionURI,
        ScenarioComponent scenario,
        int expected
    ) throws Exception {
        super(URI, brokerInboundPortSubscriptionURI);
        this.scenario = scenario;
        this.expected = expected;
    }

    @Override
    public void execute() throws Exception {
        super.execute();
        for (StepComponent tmp: scenario) {
            StepComponentSubscriber step = (StepComponentSubscriber) tmp;
            String topic = step.getTopic();
            MessageFilterI filter = step.getFilter();
            if (filter == null){
                this.subscribeService(topic);
            } else {
                this.subscribeService(topic,filter);
            }
        }
    }

    @Override
    public Void acceptMessageService(IMessage iMessage) throws Exception {
        super.acceptMessageService(iMessage);
        this.cptMessage.incrementAndGet();
        return null;
    }

    @Override
    public Void acceptMessagesService(IMessage[] iMessages) {
        super.acceptMessagesService(iMessages);
        this.cptMessage.getAndAdd(iMessages.length);
        return null;
    }

    @Override
    public void finalise() throws Exception {
        super.finalise();
        logMessage("expected : " + expected + " , got : " + this.cptMessage);
        if(expected != this.cptMessage.get()) {
            throw new Exception(this.myURI + " expected : " + expected + " , got : " + this.cptMessage);
        }
    }
}
