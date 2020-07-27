package publisher_subscriber_bcm4java.fr.sorbonne_u.beans;

import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;
/**
 * This class contains a custom timestamp for our need
 */
public class TimeStamp {
	//time 
	protected long time;
	//user uri of timestamper
	protected String timestamper;

	//Constructeur était en protected 
	protected TimeStamp(long time, String timestamper) {
		this.time = time;
		this.timestamper = timestamper;
	}
	/**
	 * @return	- True if timestamper is not null
	 */
	public boolean isInitialised() {
		return timestamper != null;
	}

	// this should not exist

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}
	/**
	 * @return	- Uri of timestamper
	 */
	public String getTimestamper() {
		return timestamper;
	}
	
	public void setTimestamper(String timestamper) {
		this.timestamper = timestamper;
	}
}
