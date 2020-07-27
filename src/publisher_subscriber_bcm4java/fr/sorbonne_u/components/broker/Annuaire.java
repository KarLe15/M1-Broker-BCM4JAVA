package publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.MessageFilterI;
/**
 * This class contains all methods and aspects relative to the management of our databases 
 * including protection for multi-threads
 *
 */
public class 		Annuaire {
	
	private 		ReentrantLock 					topicLock;
	//Condition preventing topic removing while all messages have not been dispatched/removed
	private 		Condition 						stillContainsMessages;	
	//Mutex guaranteeing atomicity between the handling of topicToUsers and topicToMessages
	private final 	Object 							mutexTopicUserMessage;
	//Mutex guaranteeing atomicity between the handling of topicToUsers and uriToPrefs 
	private final 	Object 							mutexPrefUsers;
	//Mutex guaranteeing atomicity between the handling of messageToTopics and topicToMessages
	private final 	Object 							mutexMessageTopic;
	//Mutex guaranteeing coherence in users map
	private final 	Object 							mutexURIS;
	//Map linking a message and a list of topics relative to this message
	private 		Map<IMessage, List<String>>     messageToTopics;
	//Map linking a topic to a list of messages
	private 		Map<String, List<IMessage>>     topicToMessages;
	//Map linking an URI of a user and a list of preferences (subscriptions or filter)
	private 		Map<String, List<Preference>>   uriToPrefs;
	//Map linking a topic with a list of users subscribed to this topic
	private 		Map<String, List<String>>       topicToUsers;



	public Annuaire() {
		this.topicToMessages = new ConcurrentHashMap<>();
		this.messageToTopics = new ConcurrentHashMap<>();
		this.uriToPrefs = new ConcurrentHashMap<>();
		this.topicToUsers = new ConcurrentHashMap<>();


		this.topicLock = new ReentrantLock();
		this.stillContainsMessages = topicLock.newCondition();

		this.mutexTopicUserMessage = new Object();
		this.mutexPrefUsers = new Object();
		this.mutexURIS = new Object();
		this.mutexMessageTopic = new Object();
	}

	// ==========================================================================
	//                      		TOPICS
	// ==========================================================================
	/**
	 * This method updates topicToUsers and topicToMessages
	 * @param topic	- Name of the topic
	 */
	public void createTopic(String topic) {
		synchronized (mutexTopicUserMessage) {
			this.topicToUsers.put(topic, new ArrayList<>());
			this.topicToMessages.put(topic, new ArrayList<>());
		}
	}
	/**
	 * This method creates each topic using previous method
	 * @param topic	- A List of topics
	 */
	public void createTopics(String[] topic) {
		for(String t : topic) {
			createTopic(t);
		}
	}
	/**
	 * This method destroys the message on the condition the topic empty 
	 * That is to say all messages have been dispatched
	 * @param topic - Name of the topic
	 */
	public void destroyTopic(String topic) {
		// IF TOPIC NOT EMPTY(MESSAGES) WAIT
		this.topicLock.lock();
		if (! isTopic(topic)) {
			this.topicLock.unlock();
			return;
		}
		while (!topicToMessages.get(topic).isEmpty()) {
			// S’il y a toujours des messages non envoyés
			try {
				this.stillContainsMessages.await();
			} catch (InterruptedException ignored) {}
		}
		this.topicToMessages.remove(topic);
		this.topicToUsers.remove(topic);
		this.topicLock.unlock();
	}
	/**
	 * @param	topic	- Name of the topic 
	 * @return			- True if our database contains this topic
	 */
	public boolean isTopic(String topic) {
		this.topicLock.lock();
		boolean res = this.topicToUsers.containsKey(topic);
		this.topicLock.unlock();
		return res;
	}
	/**
	 * @return			- A list of all topics contained in our database
	 */
	public String[] getTopics() {
		this.topicLock.lock();
		Object [] res = this.topicToUsers.keySet().toArray();
		this.topicLock.unlock();
		return Arrays.copyOf(res, res.length, String[].class);
	}


	// ==========================================================================
	//                      		URIS
	// ==========================================================================
	/**
	 * @param URI		- URI of the user we want to create
	 */
	public void createUser(String URI) {
		synchronized (mutexURIS) {
			this.uriToPrefs.put(URI, new ArrayList<>());
		}
	}
	/**
	 * @param URIS		- A list of all users we want to be created
	 */
	public void createUsers(String [] URIS) {
		for(String URI : URIS) {
			createUser(URI);
		}
	}
	/**
	 * @param URI		- URI of the user we look up
	 * @return			- Return true if database contains the user
	 */
	public boolean isUser(String URI) {
		synchronized (mutexURIS) {
			return this.uriToPrefs.containsKey(URI);
		}
	}
	/**
	 * @return			- Return all users contained in our databases
	 */
	public String[] getUsers() {
		synchronized (mutexURIS) {
			Object [] res = this.uriToPrefs.keySet().toArray();
			return Arrays.copyOf(res, res.length, String[].class);
		}
	}

	// ==========================================================================
	//                      		SUBSCRIPTION
	// ==========================================================================
	/**
	 * 					- Update database with a new subscription
	 * @param URI		- URI of the user subscribing
	 * @param topic		- Names of the topics the user wants to subscribe
	 */
	public void addSubscription(String URI, String topic) {
		addSubscription(URI,topic, null);
	}
	/**
	 * 					- Update database with this list of subscription
	 * @param URI		- URI of the user user subscribing
	 * @param topics	- List of topics
	 */
	public void addSubscription(String URI, String[] topics) {
		for (String topic : topics) {
			if(! isTopic(topic)) {
				createTopic(topic);
			}
		}
		if (! isUser(URI)) {
			createUser(URI);
		}
		List<Preference> ps = new ArrayList<>();
		for (String topic : topics) {
			Preference p = new Preference(topic);
			ps.add(p);
		}
		synchronized (mutexPrefUsers) {
			List<Preference> preferences = this.uriToPrefs.get(URI);
			preferences.addAll(ps);
			for (String topic : topics) {
				List<String> users = this.topicToUsers.get(topic);
				users.add(URI);
			}
		}
	}
	/**
	 * 					- Update database with a subscription and a Message filter
	 * @param URI		- URI of the user subscribing
	 * @param topic		- Topic user wants to subscribe
	 * @param filter	- Filter to the message
	 */
	public void addSubscription(String URI, String topic, MessageFilterI filter) {
		if (! isTopic(topic)) {
			createTopic(topic);
		}
		if (! isUser(URI)) {
			createUser(URI);
		}
		synchronized (mutexPrefUsers) {
			Preference p = new Preference(topic, filter);
			// check if it doesn't exist already
			List<Preference> preferences = this.uriToPrefs.get(URI);
			preferences.add(p);

			List<String> topics = this.topicToUsers.get(topic);
			topics.add(URI);
		}
	}
	/**
	 * 					- Update the filter of a user relative to a topic with a new one
	 * @param URI		- URI of the user we want to modify its messageFilter
	 * @param topic		- Name of the topic 
	 * @param newFilter	- The new Filter
	 */
	public void modifyFilter(String URI, String topic, MessageFilterI newFilter) {
		if (! isTopic(topic)) {
			return ;
		}
		if (! isUser(URI)) {
			return ;
		}
		synchronized (mutexPrefUsers) {
			List<Preference> preferences = this.uriToPrefs.get(URI);
			List<Preference> prefOfTopic = preferences.stream().
					filter((p) -> p.getTopic().equals(topic)).
					collect(Collectors.toList());
			if(prefOfTopic.isEmpty()){
				return;
			}
			assert prefOfTopic.size() == 1;
			Preference pref = prefOfTopic.get(0);
			pref.setFilters(newFilter);
		}
	}
	/**
	 * 					- Update database removing the subscription of a user
	 * @param URI		- URI of the user removing subscription
	 * @param topic		- Name of the Topic
	 */
	public void removeSubscription(String URI, String topic) {
		if (! isTopic(topic)) {
			return ;
		}
		if (! isUser(URI)) {
			return ;
		}
		synchronized (mutexPrefUsers) {
			List<Preference> preferences = this.uriToPrefs.get(URI);
			List<Preference> prefOfTopic = preferences.stream().
					filter((p) -> p.getTopic().equals(topic))
					.collect(Collectors.toList());
			if(prefOfTopic.isEmpty()){
				return;
			}
			assert prefOfTopic.size() == 1;
			Preference pref = prefOfTopic.get(0);
			preferences.remove(pref);
		}
	}


	// ==========================================================================
	//                      		MESSAGES
	// ==========================================================================
	/**
	 * 					- Update database with a new message
	 * @param m			- Message sent
	 * @param topic		- Topic where the message has been sent
	 */
	public void addMessage(IMessage m, String topic) {
		this.topicLock.lock();
		if (!isTopic(topic)) {
			createTopic(topic);
		}
		synchronized (this.mutexMessageTopic) {
			List<String> topics = new ArrayList<>();
			topics.add(topic);
			this.messageToTopics.put(m, topics);
			this.topicToMessages.get(topic).add(m);
		}
		this.topicLock.unlock();
	}
	/**
	 * 					- Update database with a new message concerning multiple topics
	 * @param m			- Message sent
	 * @param topics	- List of topics relative to this topic
	 */
	public void addMessage(IMessage m, String[] topics){
		this.topicLock.lock();
		for (String topic : topics){
			if(! isTopic(topic) ) {
				createTopic(topic);
			}
		}
		synchronized (this.mutexMessageTopic){
			List<String> topicsToAdd = Arrays.asList(topics);
			this.messageToTopics.put(m, topicsToAdd);
			for (String topic : topics){
				this.topicToMessages.get(topic).add(m);
			}
//			this.stillContainsMessages.signalAll(); // This is necessary only for remove message
		}
		this.topicLock.unlock();
	}
	/**
	 *					- Update database with a list of messages relative to one topic 
	 * @param ms		- List of messages
	 * @param topic		- Name of topic
	 */
	public void addMessage(IMessage[] ms, String topic) {
		for(IMessage msg : ms) {
			//switching are tolerated between two messages
			this.addMessage(msg, topic);
		}

	}
	/**
	 * 					- Update database with a list of messages relative to multiple topics
	 * @param ms		- List of messages
	 * @param topics	- List of topics
	 */
	public void addMessage(IMessage[] ms, String[] topics) {
		for(IMessage msg: ms) {
			//switching are tolerated between two messages
			this.addMessage(msg, topics);
		}

	}
	/**
	 * 					- Update database removing one message (usually, a message is removed from the database
	 * 					  when it has been dispatched)
	 * @param m			- Message
	 */
	public void destroyMessage(IMessage m){
		this.topicLock.lock();
		synchronized (this.mutexMessageTopic){
			List<String> topics = this.messageToTopics.get(m);
			for (String topic : topics){
				this.topicToMessages.get(topic).remove(m);
			}
			this.messageToTopics.remove(m);

			this.stillContainsMessages.signalAll();
		}
		this.topicLock.unlock();
	}

	// ==========================================================================
	//                      		Getters
	// ==========================================================================
	/**
	 * @param URI		- URI of a user
	 * @return			- A List of preferences
	 */
	public List<Preference> getPreferenceOfURI(String URI) {
		return uriToPrefs.get(URI);
	}
	/**
	 * @param topic		- Topic 
	 * @return			- All users subscribed to this topic
	 */
	public List<String> getURIsOfTopic(String topic) {
		return topicToUsers.get(topic);
	}
	/**
	 * @param URI
	 * @return			- A list of preferences of a user
	 */
	public List<Preference> getPreferencesOfURI(String URI) {
		return this.uriToPrefs.get(URI);
	}
	/**
	 * @param topic		- Name of topic
	 * @return			- List of messages relative to a topic
	 */
	public List<IMessage> getMessagesOfTopic(String topic) {
		return this.topicToMessages.get(topic);
	}
	/**
	 * @param msg		- A message
	 * @return			- All topic relative to a message
	 */
	public List<String> getTopicsOfMessage(IMessage msg){
		return this.messageToTopics.get(msg);
	}
	/**
	 * @param m			- Message
	 * @return			- A list of uris of a message
	 */
	public List<String> getURIsToMessage(IMessage m){
		List<String> uris = new ArrayList<>();
		List<String> topics = this.messageToTopics.get(m);
		for (String topic : topics) {
			uris.addAll(this.topicToUsers.get(topic));
		}
		uris = uris.stream().
				distinct().
				filter((u) -> filterMessagePrefs(m, topics, u)).
				collect(Collectors.toList());

		return uris;
	}
	/**
	 * 
	 * @param m			- Message
	 * @param topics	- A topic
	 * @param u			- Content of a search
	 * @return			- True if a message contains the keyword
	 */
	private boolean filterMessagePrefs(IMessage m, List<String> topics, String u) {
		List<Preference> prefs = getPreferenceOfURI(u).stream().
				filter(p -> topics.contains(p.getTopic())).
				collect(Collectors.toList());
		boolean filter = false;
		for (Preference p : prefs) {
			if (p.getFilters() != null) {
				filter = filter || p.getFilters().filter(m);
			} else {
				filter = true;
			}
		}
		return filter;
	}


	// ==========================================================================
	//                      		UTILITIES
	// ==========================================================================
	/**
	 * @param prefs		- List of preferences
	 * @param pref		- A preference
	 * @return			- True if the list contains a pref
	 */
	public boolean containsPreference(List<Preference> prefs, Preference pref) {
		return prefs.
				stream().
				filter( (p) -> p.getTopic().equals(pref.getTopic()) ).
				count() > 1;
	}

}
