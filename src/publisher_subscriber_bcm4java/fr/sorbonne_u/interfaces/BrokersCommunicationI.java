package publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces;


import fr.sorbonne_u.components.interfaces.OfferedI;
import fr.sorbonne_u.components.interfaces.RequiredI;

public interface BrokersCommunicationI extends RequiredI, OfferedI {
    void publish(IMessage m, String topic, String brokerURI) throws Exception;
    void publish(IMessage m, String[] topics, String brokerURI)  throws Exception;
    void publish(IMessage[] ms, String topic, String brokerURI) throws Exception;
    void publish(IMessage[] ms, String[] topics, String brokerURI) throws Exception;

    void createTopic(String topic, String brokerURI) throws Exception;
    void createTopics(String[] topics, String brokerURI) throws Exception;
    void destroyTopic(String topic, String brokerURI) throws Exception;
}
