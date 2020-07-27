package publisher_subscriber_bcm4java.fr.sorbonne_u.beans;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Properties implements Serializable {
	private Map<Object, Object> properties = new HashMap<>();
	
	public void 	putProp(String name, boolean v) {
		properties.put(name, v);
	}
	public void 	putProp			(String name, byte v) {
		properties.put(name, v);
	}
	public void 	putProp			(String name, char v) {
		properties.put(name, v);
	}
	public void 	putProp			(String name, double v) {
		properties.put(name, v);
	}
	public void 	putProp			(String name, float v) {
		properties.put(name, v);
	}
	public void 	putProp			(String name, int v) {
		properties.put(name, v);
	}
	public void 	putProp			(String name, long v) {
		properties.put(name, v);
	}
	public void 	putProp			(String name, short v) {
		properties.put(name, v);
	}
	public void 	putProp			(String name, String v) {
		properties.put(name, v);
	}
	public boolean 	getBooleanProp	(String name) {
		return (boolean) properties.get(name);
	}
	public byte 	getByteProp		(String name) {
		return (byte) properties.get(name);
	}
	public char 	getCharProp		(String name) {
		return (char) properties.get(name);
	}
	public double 	getDoubleProp	(String name) {
		return (double) properties.get(name);
	}
	public float 	getFloatProp	(String name) {
		return (float) properties.get(name);
	}
	public int 		getIntProp		(String name) {
		return (int) properties.get(name);
	}
	public long 	getLongProp		(String name) {
		return (long) properties.get(name);
	}
	public short 	getShortProp	(String name) {
		return (short) properties.get(name);
	}
	public String 	getStringProp	(String name) {
		return (String) properties.get(name);
	}

	public boolean 	contains		(Object key, Object value) {
		return 	this.properties.containsKey(key) &&
				this.properties.get(key).equals(value);
	}
}
