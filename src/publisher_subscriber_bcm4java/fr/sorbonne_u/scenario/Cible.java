package publisher_subscriber_bcm4java.fr.sorbonne_u.scenario;

public class Cible {
    public CibleType type;
    public String URICible;
    public ScenarioComponent subScenario;
    public int expected;

    public Cible(CibleType type, String URICible, ScenarioComponent subScenario, int expected) {
        this.type = type;
        this.URICible = URICible;
        this.subScenario = subScenario;
        this.expected = expected;
    }


}
