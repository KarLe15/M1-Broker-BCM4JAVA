package publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces;

public interface ReceptionImplementationI {
	void acceptMessage(IMessage m) throws Exception;
	void acceptMessages(IMessage[] m) throws Exception;
}
