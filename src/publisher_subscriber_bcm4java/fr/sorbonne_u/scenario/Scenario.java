package publisher_subscriber_bcm4java.fr.sorbonne_u.scenario;

import java.util.Iterator;
import java.util.List;

public class Scenario implements Iterable<StepScenario>{
    List<StepScenario> stepList;

    public Scenario(List<StepScenario> stepList) {
        this.stepList = stepList;
    }

    @Override
    public Iterator<StepScenario> iterator() {
        return stepList.iterator();
    }
}
