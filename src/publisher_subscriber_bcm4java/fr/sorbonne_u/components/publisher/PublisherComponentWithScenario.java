package publisher_subscriber_bcm4java.fr.sorbonne_u.components.publisher;

import fr.sorbonne_u.components.annotations.RequiredInterfaces;
import org.apache.commons.math3.util.Pair;
import publisher_subscriber_bcm4java.fr.sorbonne_u.beans.MessageFactory;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.publisher.interfaces.PublisherMessageI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.subscription.OutboundSubscriptionI;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessageFactory;
import publisher_subscriber_bcm4java.fr.sorbonne_u.scenario.ScenarioComponent;
import publisher_subscriber_bcm4java.fr.sorbonne_u.scenario.StepComponent;
import publisher_subscriber_bcm4java.fr.sorbonne_u.scenario.StepComponentPublisher;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 *
 */

@RequiredInterfaces(required = {
    PublisherMessageI.class,
    OutboundSubscriptionI.class,
})
public class PublisherComponentWithScenario extends PublisherComponent {

    private ScenarioComponent scenario;
    private IMessageFactory factory = new MessageFactory();
    private AtomicInteger   cptMessage = new AtomicInteger(0);

    protected PublisherComponentWithScenario(
        String URI,
        String brokerInboundSubscriptionPort,
        ScenarioComponent scenario
    ) throws Exception {
        super(URI, brokerInboundSubscriptionPort);
        this.scenario = scenario;
    }

    @Override
    public void execute() throws Exception {
        super.execute();
        this.scheduleTask((ignore) -> {
            runScenario();
        },1000, TimeUnit.MILLISECONDS);
    }

    private void runScenario() {
        try {
            for (StepComponent tmp : scenario) {
                StepComponentPublisher step = (StepComponentPublisher) tmp;
                List<Pair<Object, Object>> filter = step.getFilters();
                IMessage message = factory.newMessage(step.getContent(), myURI);
                if (!filter.isEmpty()) {
                    for (Pair<Object, Object> pair : filter) {
                        // TODO :: now only strings works
                        message.getProperties().putProp((String) pair.getFirst(), (String) pair.getSecond());
                    }
                }
                List<String> topics = step.getTopics();
                if (topics.size() == 1) {
                    this.publishService(message, topics.get(0));
                } else {
                    String[] t = topics.stream().toArray(String[]::new);
                    this.publishService(message, t);
                }
                cptMessage.getAndIncrement();
                Thread.sleep(step.getTimeToWait());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finalise() throws Exception {
        super.finalise();
        logMessage("has sent : " + this.cptMessage + " messages");
    }
}
