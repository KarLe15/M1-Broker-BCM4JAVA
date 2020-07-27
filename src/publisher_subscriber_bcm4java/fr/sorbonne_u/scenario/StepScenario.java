package publisher_subscriber_bcm4java.fr.sorbonne_u.scenario;

public class StepScenario extends Step {
    protected Cible cible;

    public StepScenario(OperationType operation, Cible cible) {
        super(operation);
        this.cible = cible;
    }

    public String getURI(){
        return this.cible.URICible;
    }
    public CibleType getCibleType(){
        return this.cible.type;
    }

    public ScenarioComponent getScenarioComponent(){
        return this.cible.subScenario;
    }
    public int getExpected() {
        if (this.getCibleType().equals(CibleType.PUBLISHER)) {
            return -1;
        }
        return this.cible.expected;
    }
}
