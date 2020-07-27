package publisher_subscriber_bcm4java.fr.sorbonne_u.scenario;

public abstract class Step {
    protected OperationType operation;

    public Step(OperationType operation) {
        this.operation = operation;
    }

    public OperationType getOperation() {
        return operation;
    }
}
