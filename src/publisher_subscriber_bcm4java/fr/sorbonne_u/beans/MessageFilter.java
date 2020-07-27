package publisher_subscriber_bcm4java.fr.sorbonne_u.beans;

import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.MessageFilterI;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MessageFilter implements MessageFilterI {
    Map<Object, Object> properties;

    public MessageFilter(Map<Object, Object> properties) {
        this.properties = properties;
    }

    public MessageFilter() {
        this.properties = new HashMap<>();
    }


    public void addProperty(Object name, Object value){
        this.properties.put(name, value);
    }
    @Override
    public boolean filter(IMessage m) {
        boolean res = true;
        Collection<Object> keys = this.properties.keySet();
        for (Object k : keys) {
            res = this.messageContainsProperty(m, k, this.properties.get(k) );
            if(! res) {
                return false;
            }
        }
        return true;
    }

    public boolean messageContainsProperty(IMessage m, Object key, Object value) {
        return m.getProperties().contains(key, value);
    }

}
