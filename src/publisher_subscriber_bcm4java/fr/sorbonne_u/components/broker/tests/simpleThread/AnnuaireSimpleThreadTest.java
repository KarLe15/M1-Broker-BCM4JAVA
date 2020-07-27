package publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.tests.simpleThread;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import publisher_subscriber_bcm4java.fr.sorbonne_u.beans.MessageFactory;
import publisher_subscriber_bcm4java.fr.sorbonne_u.beans.MessageFilter;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.Annuaire;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.Preference;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessageFactory;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.MessageFilterI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnuaireSimpleThreadTest {

    IMessageFactory factory = new MessageFactory();

    private Annuaire annuaire;
    private final List<String> uris = Arrays.asList(
        "uri1", "uri2", "uri3", "uri4"
    );
    private final List<String> topics = Arrays.asList(
        "topic1", "topic2", "topic3", "topic4"
    );
    // ==========================================================================
    //                      		Initialisation
    // ==========================================================================

    @Before
    public void initiateData() {
        annuaire = new Annuaire();
    }

    private void initiateUsers(){
        annuaire.createUser("uri0");
    }
    private void initiateTopics(){
        annuaire.createTopic("topic0");
    }

    // ==========================================================================
    //                      		    UTILITIES
    // ==========================================================================

    private boolean isInArray(Object[] array, Object element){
        boolean res = false;
        for (int i = 0; !res && i < array.length; i++) {
            res = element.equals(array[i]);
        }
        return res;
    }

    private boolean containsPreference(List<Preference> prefs, String topic) {
        return prefs.
            stream().
            filter((p) -> p.getTopic().equals(topic)).
            count() == 1;
    }

    private boolean containsPreference(List<Preference> prefs, String topic, MessageFilterI filter) {
        return prefs.
            stream().
            filter((p) -> p.getTopic().equals(topic) && p.getFilters().equals(filter)).
            count() == 1;
    }

    private boolean containsURI(List<String> uris, String uri){
        return uris.stream().
            filter((u) -> u.equals(uri)).
            count() == 1;
    }

    public boolean containsAllPreference(List<Preference> prefs, String [] topics){
        return prefs.stream().
            filter(p -> isInArray(topics,p.getTopic())).
            count() == topics.length;
    }



    // ==========================================================================
    //                      		    Users
    // ==========================================================================

    @Test
    public void testAdd1UserToEmpty() {
        Assert.assertEquals(0,annuaire.getUsers().length);
        annuaire.createUser("uri1");
        Assert.assertEquals(1, annuaire.getUsers().length);
    }

    @Test
    public void testAdd1UserToNonEmpty() {
        initiateUsers(); // this function initiate annuaire with only one user
        Assert.assertEquals(1,annuaire.getUsers().length);
        annuaire.createUser("uri1");
        Assert.assertEquals(2,annuaire.getUsers().length);
    }

    @Test
    public void testAddUsersToEmpty(){
        Assert.assertEquals(0,annuaire.getUsers().length);
        String[] urisToAdd = uris.toArray(new String[uris.size()]);
        annuaire.createUsers(urisToAdd);
        Assert.assertEquals(urisToAdd.length, annuaire.getUsers().length);
    }

    @Test
    public void testIsUserWork(){
        initiateUsers();
        Assert.assertTrue(annuaire.isUser("uri0"));
    }

    @Test
    public void testIsUserNotWork(){
        initiateUsers();
        Assert.assertFalse(annuaire.isUser("azerty"));
    }

    // ==========================================================================
    //                      		    Topics
    // ==========================================================================

    @Test
    public void testCreate1TopicToEmpty(){
        Assert.assertEquals(0,annuaire.getTopics().length);
        annuaire.createTopic("topic1");
        Assert.assertEquals(1,annuaire.getTopics().length);
    }

    @Test
    public void testCreate1TopicToNotEmpty(){
        initiateTopics();
        Assert.assertEquals(1,annuaire.getTopics().length);
        annuaire.createTopic("topic1");
        Assert.assertEquals(2,annuaire.getTopics().length);
    }

    @Test
    public void testCreateTopicsToEmpty(){
        Assert.assertEquals(0,annuaire.getTopics().length);
        String[] topicsToAdd = topics.toArray(new String[topics.size()]);
        annuaire.createTopics(topicsToAdd);
        Assert.assertEquals(topics.size(), annuaire.getTopics().length);
    }

    @Test
    public void testIsTopicWork(){
        initiateTopics();
        Assert.assertTrue(annuaire.isTopic("topic0"));
    }

    @Test
    public void testIsTopicNotWork(){
        initiateTopics();
        Assert.assertFalse(annuaire.isTopic("azerty"));
    }

    @Test
    public void testDestroyTopic(){
        initiateTopics();
        Assert.assertEquals(1,annuaire.getTopics().length);
        annuaire.destroyTopic("topic0");
        Assert.assertEquals(0,annuaire.getTopics().length);
    }


    // ==========================================================================
    //                      		SUBSCRIPTION
    // ==========================================================================

    @Test
    public void testAddSubscription1TopicNewUserNewTopic(){
        String uri = "uri1";
        String topic = "topic1";
        Assert.assertEquals(0,annuaire.getUsers().length);
        Assert.assertEquals(0,annuaire.getTopics().length);
        annuaire.addSubscription(uri,topic);
        Assert.assertEquals(1,annuaire.getUsers().length);
        Assert.assertEquals(1,annuaire.getTopics().length);
        Assert.assertTrue(
            containsPreference(
                annuaire.getPreferenceOfURI(uri), topic
            )
        );
        Assert.assertTrue(
            containsURI(
                annuaire.getURIsOfTopic(topic), uri
            )
        );
    }

    @Test
    public void testAddSubscription1TopicNewUserOldTopic(){
        initiateTopics(); // initiate with "topic0"
        String uri = "uri1";
        String topic = "topic0";
        Assert.assertEquals(0,annuaire.getUsers().length);
        Assert.assertEquals(1,annuaire.getTopics().length);
        annuaire.addSubscription(uri,topic);
        Assert.assertEquals(1,annuaire.getUsers().length);
        Assert.assertEquals(1,annuaire.getTopics().length);
        Assert.assertTrue(
            containsPreference(
                annuaire.getPreferenceOfURI(uri), topic
            )
        );
        Assert.assertTrue(
            containsURI(
                annuaire.getURIsOfTopic(topic), uri
            )
        );
    }

    @Test
    public void testAddSubscription1TopicOldUserNewTopic(){
        initiateUsers(); // initiate with "uri0"
        String uri = "uri0";
        String topic = "topic1";
        Assert.assertEquals(1,annuaire.getUsers().length);
        Assert.assertEquals(0,annuaire.getTopics().length);
        annuaire.addSubscription(uri,topic);
        Assert.assertEquals(1,annuaire.getUsers().length);
        Assert.assertEquals(1,annuaire.getTopics().length);
        Assert.assertTrue(
            containsPreference(
                annuaire.getPreferenceOfURI(uri), topic
            )
        );
        Assert.assertTrue(
            containsURI(
                annuaire.getURIsOfTopic(topic), uri
            )
        );
    }

    @Test
    public void testAddSubscription1TopicOldUserOldTopic(){
        initiateUsers(); // initiate with "uri0"
        initiateTopics(); // initiate with "topic0"
        String uri = "uri0";
        String topic = "topic0";
        Assert.assertEquals(1,annuaire.getUsers().length);
        Assert.assertEquals(1,annuaire.getTopics().length);
        annuaire.addSubscription(uri,topic);
        Assert.assertEquals(1,annuaire.getUsers().length);
        Assert.assertEquals(1,annuaire.getTopics().length);
        Assert.assertTrue(
            containsPreference(
                annuaire.getPreferenceOfURI(uri), topic
            )
        );
        Assert.assertTrue(
            containsURI(
                annuaire.getURIsOfTopic(topic), uri
            )
        );
    }

    @Test
    public void testAddSubscriptionTopics(){
        initiateUsers(); // initiate with "uri0"
        initiateTopics(); // initiate with "topic0"
        String uri = "uri0";
        String[] topic = new String[]{ "topic1", "topic2", "topic3"};
        Assert.assertEquals(1,annuaire.getUsers().length);
        Assert.assertEquals(1,annuaire.getTopics().length);
        annuaire.addSubscription(uri,topic);
        Assert.assertEquals(1,annuaire.getUsers().length);
        Assert.assertEquals(topic.length + 1 ,annuaire.getTopics().length);
        Assert.assertTrue(
            containsAllPreference(
                annuaire.getPreferenceOfURI(uri), topic
            )
        );
        for (int i = 0; i < topic.length; i++) {
            Assert.assertTrue(
                containsURI(
                    annuaire.getURIsOfTopic(topic[i]), uri
                )
            );
        }

    }

    @Test
    public void testAddSubscriptionTopicsWithOneExists(){
        initiateUsers(); // initiate with "uri0"
        initiateTopics(); // initiate with "topic0"
        String uri = "uri0";
        String[] topic = new String[]{ "topic0", "topic1", "topic2", "topic3"};
        Assert.assertEquals(1,annuaire.getUsers().length);
        Assert.assertEquals(1,annuaire.getTopics().length);
        annuaire.addSubscription(uri,topic);
        Assert.assertEquals(1, annuaire.getUsers().length);
        Assert.assertEquals(topic.length, annuaire.getTopics().length);
        Assert.assertTrue(
            containsAllPreference(
                annuaire.getPreferenceOfURI(uri), topic
            )
        );
        for (String s : topic) {
            Assert.assertTrue(
                    containsURI(
                            annuaire.getURIsOfTopic(s), uri
                    )
            );
        }

    }

    @Test
    public void testAddSubscriptionTopicFilter(){
        String uri = "uri0";
        String topic = "topic0";
        MessageFilterI filter = new MessageFilter();
        Assert.assertEquals(0,annuaire.getUsers().length);
        Assert.assertEquals(0,annuaire.getTopics().length);
        annuaire.addSubscription(uri,topic, filter);
        Assert.assertEquals(1, annuaire.getUsers().length);
        Assert.assertEquals(1, annuaire.getTopics().length);
        Assert.assertTrue(
                containsPreference(
                    annuaire.getPreferenceOfURI(uri),
                    topic,
                    filter
                )
        );
//        Assert.fail("Not implemented MessageFilterI");
    }

    @Test
    public void testModifyFilter(){
        String subscriber = "subscriber";

        MessageFilter oldMF = new MessageFilter();
        oldMF.addProperty("PC3R", "Go");

        MessageFilter newMF = new MessageFilter();
        newMF.addProperty("CPS", "Java");

        annuaire.addSubscription(subscriber, "UE", oldMF);
        Assert.assertEquals(1, annuaire.getPreferenceOfURI(subscriber).size());
        Assert.assertEquals(oldMF, annuaire.getPreferenceOfURI(subscriber).get(0).getFilters());

        annuaire.modifyFilter("subscriber", "UE", newMF);
        Assert.assertEquals(1, annuaire.getPreferenceOfURI(subscriber).size());
        Assert.assertEquals(newMF, annuaire.getPreferenceOfURI(subscriber).get(0).getFilters());
    }

    @Test
    public void testRemoveSubscription(){
        String subscriber = "subscriber";
        String topic = "UE";
        MessageFilter MF = new MessageFilter();
        MF.addProperty("PC3R", "Go");

        annuaire.addSubscription(subscriber, topic, MF);
        Assert.assertEquals(1, annuaire.getPreferenceOfURI(subscriber).size());

        annuaire.removeSubscription(subscriber, topic);
        Assert.assertEquals(0, annuaire.getPreferenceOfURI(subscriber).size());
    }

    // ==========================================================================
    //                      		MESSAGES
    // ==========================================================================

    @Test
    public void testAddMessage() {
    	String myMessage = "Hello World" ;
    	String uri = "uriM";
    	IMessage m = factory.newMessage(myMessage, uri);
    	annuaire.addMessage(m, "Topicparticulier");
    	Assert.assertEquals(1, annuaire.getMessagesOfTopic("Topicparticulier").size());
    	Assert.assertEquals(1, annuaire.getUsers().length);
    }

    @Test
    public void testAddMultipleMessageOneTopic() {
    	int NUMBER_OF_MESSAGES = 10;
    	List<IMessage> messages = new ArrayList<>();
    	for(int i=0; i<NUMBER_OF_MESSAGES;i++) {
    		if(i==8) {
    			messages.add(
    			    factory.newMessage("myMessage"+i, "uri"+7 )
                );
    			continue;
    		}
    		messages.add(
                factory.newMessage("myMessage"+i, "uri"+i )
            );
    	}
    	annuaire.addMessage(messages.stream().toArray(IMessage[]::new), "One Topic");
    	Assert.assertEquals(NUMBER_OF_MESSAGES, annuaire.getMessagesOfTopic("One Topic").size());
        for (int i = 0; i < NUMBER_OF_MESSAGES; i++) {
            Assert.assertEquals(1, annuaire.getTopicsOfMessage(messages.get(i)).size());
        }
    	Assert.assertEquals(NUMBER_OF_MESSAGES-1, annuaire.getUsers().length);
    }

    @Test
    public void testAddOneMessageMultipleTopics() {
    	int NUMBER_OF_TOPICS = 10;
    	List<String> topics = new ArrayList<>();
    	IMessage  m = factory.newMessage("myMessage", "uri" );
    	for(int i=0; i<NUMBER_OF_TOPICS; i++) {
    		topics.add("Topics"+i);
    	}
    	annuaire.addMessage(m, topics.stream().toArray(String[]::new) );
    	for (String topic : topics){
            Assert.assertEquals(1,annuaire.getMessagesOfTopic(topic).size());
        }
    	Assert.assertEquals(NUMBER_OF_TOPICS,annuaire.getTopics().length);
    }

    @Test
    public void testAddMultipleMessagesMultipleTopics() {
    	int NUMBER_OF_MESSAGES = 13;
    	int NUMBER_OF_TOPICS = 10;
    	List<String> topics = new ArrayList<>();
    	List<IMessage> messages = new ArrayList<>();
    	for(int i = 0; i < NUMBER_OF_MESSAGES; i++) {
    		messages.add(
    		    factory.newMessage("myMessage"+i, "uri"+i)
            );
    	}
    	for(int i = 0; i < NUMBER_OF_TOPICS; i++) {
    		if(i > NUMBER_OF_TOPICS-4) {
    			topics.add("myTopic"+7);
    			continue;
    		}
    		topics.add("myTopic"+i);
    	}
    	topics = topics.stream().distinct().collect(Collectors.toList());
    	annuaire.addMessage(
    	    messages.stream().toArray(IMessage[]::new),
            topics.stream().toArray(String[]::new)
        );
    	Assert.assertEquals(NUMBER_OF_MESSAGES, annuaire.getUsers().length);
    	Assert.assertEquals(NUMBER_OF_TOPICS-2, annuaire.getTopics().length);
    	Assert.assertEquals(NUMBER_OF_MESSAGES, annuaire.getMessagesOfTopic("myTopic7").size());
    }

    @Test
    public void testRemoveMessage() {
        String publisher = "publisher";
        String topic = "topic";
        IMessage m = factory.newMessage("message 1", publisher);

        annuaire.addMessage(m, topic);
        Assert.assertEquals(1, annuaire.getMessagesOfTopic(topic).size());

        annuaire.destroyMessage(m);
        Assert.assertEquals(0, annuaire.getMessagesOfTopic(topic).size());
    }

    @Test
    public void testGetURIofMessageWithoutFilter() {
        List<String> subscribers = new ArrayList<>();
        List<IMessage> messages = new ArrayList<>();
        int NUMBER_OF_TOPICS = 15;

        for (int i = 0; i < 5; i++) {
            subscribers.add("uri "+(i+1));
        }

        for (int i = 0; i < 5; i++) {
            int topicID = i*3;
            annuaire.addSubscription(subscribers.get(i), "topic "+topicID);
        }
        List<String> topics = new ArrayList<>(30);
        for (int i = 0; i < NUMBER_OF_TOPICS; i++) {
            topics.add("topic "+i);
            for (int j = 0; j < 4; j++) {
                int indexMessage = i*j +j;
                IMessage m = factory.newMessage("message"+indexMessage,"pubisher");
                messages.add(m);
                annuaire.addMessage(m, topics.stream().toArray(String[]::new));
            }
        }
        for (int i = 0; i < messages.size(); i++) {
            IMessage m = messages.get(i);
            if (i == 60) {
                Assert.assertEquals(5, annuaire.getURIsToMessage(m));
            }else{
                Assert.assertEquals( (int)Math.ceil( (i+1) / 12.0) ,annuaire.getURIsToMessage(m).size());
            }
        }
    }

}
