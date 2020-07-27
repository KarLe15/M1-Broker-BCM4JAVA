package publisher_subscriber_bcm4java.fr.sorbonne_u.scenario;

import org.apache.commons.math3.util.Pair;
import publisher_subscriber_bcm4java.fr.sorbonne_u.beans.MessageFilter;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.MessageFilterI;

import java.util.List;

public class StepComponentSubscriber extends StepComponent {
    private String topic;
    List<Pair<Object, Object>> filters;

    public StepComponentSubscriber(
        OperationType operation,
        String topic,
        List<Pair<Object, Object>> filters
    ) {
        super(operation);
        this.topic = topic;
        this.filters = filters;
    }

    public String getTopic() {
        return topic;
    }
    public MessageFilterI getFilter(){
        if (filters.isEmpty()){
            return null;
        }
        MessageFilter res = new MessageFilter();
        for (Pair<Object, Object> pair : filters) {
            res.addProperty(pair.getFirst(), pair.getValue());
        }
        return res;
    }
}
