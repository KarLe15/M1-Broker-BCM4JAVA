package publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces;

import java.io.Serializable;

import publisher_subscriber_bcm4java.fr.sorbonne_u.beans.Properties;
import publisher_subscriber_bcm4java.fr.sorbonne_u.beans.TimeStamp;


public interface IMessage extends Serializable{
	public String getURI();
	public TimeStamp getTimeStamp();
	public Properties getProperties();
	public Serializable getPayload();

}
