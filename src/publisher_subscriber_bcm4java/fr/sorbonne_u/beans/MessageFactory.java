package publisher_subscriber_bcm4java.fr.sorbonne_u.beans;

import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessageFactory;

import java.io.Serializable;

/**
 * Message Factory 
 */

public class MessageFactory implements IMessageFactory {
	
	/**
	 * counter 				- debugging tool
	 */
	private static int compteur = 0;
	/**
	 * @param msg 			- The message content in the form of a Serializable
	 * @param providersURI 	- URI's provider
	 * @return 				- Instanciate a new message
	 */
	@Override
	public IMessage newMessage(Serializable msg, String providersURI) {
		String uriMessage = generateUIDForMessage();
		long time = System.currentTimeMillis();
		TimeStamp stamp = getTimeStamp(time, providersURI);
		return new Message(msg,uriMessage,stamp);
	}
	/**
	 * @return				- Message indicating state of counter
	 */
	@Override
	public String generateUIDForMessage() {
		return "Msg " + (++compteur);
	}
	/**
	 * 
	 * @param time			- Time
	 * @param providersURI	- URI of a user
	 * @return				- Timestamp
	 */
	@Override
	public TimeStamp getTimeStamp(long time, String providersURI) {
		return new TimeStamp(time, providersURI);
	}
}
