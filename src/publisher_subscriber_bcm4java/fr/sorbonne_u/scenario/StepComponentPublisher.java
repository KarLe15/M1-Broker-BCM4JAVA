package publisher_subscriber_bcm4java.fr.sorbonne_u.scenario;

import org.apache.commons.math3.util.Pair;
import publisher_subscriber_bcm4java.fr.sorbonne_u.beans.MessageFilter;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.MessageFilterI;

import java.util.List;

public class StepComponentPublisher extends StepComponent {
    private String content;
    private List<Pair<Object, Object>> filters;
    private int timeToWait;
    private List<String> topics;

    public StepComponentPublisher(
        OperationType               operation,
        String                      content,
        List<Pair<Object, Object>>  filters,
        int                         timeToWait,
        List<String>                topics
    ) {
        super(operation);
        this.content = content;
        this.filters = filters;
        this.timeToWait = timeToWait;
        this.topics = topics;
    }



    public String getContent() {
        return content;
    }

    public List<Pair<Object, Object>> getFilters(){
        return filters;
    }
    public List<String> getTopics() {
        return this.topics;
    }
    public int getTimeToWait() {
        return timeToWait;
    }
}
