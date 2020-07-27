package publisher_subscriber_bcm4java.fr.sorbonne_u.scenario;

import java.util.Iterator;
import java.util.List;

public abstract class ScenarioComponent implements Iterable<StepComponent> {
    List<StepComponent> steps;

    ScenarioComponent(List<StepComponent> steps) {
        this.steps = steps;
    }

    @Override
    public Iterator<StepComponent> iterator() {
        return steps.iterator();
    }
}
