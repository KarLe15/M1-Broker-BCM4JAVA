package publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.tests.multiThread;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import publisher_subscriber_bcm4java.fr.sorbonne_u.beans.MessageFactory;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.Annuaire;
import publisher_subscriber_bcm4java.fr.sorbonne_u.components.broker.Preference;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessage;
import publisher_subscriber_bcm4java.fr.sorbonne_u.interfaces.IMessageFactory;

public class AnnuaireMultiThreadTest {
	// TODO :: ajouter des tests pour l'interblocage et la famine
	IMessageFactory factory = new MessageFactory();

	private Annuaire annuaire;
	private final List<String> uris = Arrays.asList(
			"uri1", "uri2", "uri3", "uri4"
	);
	private final List<String> topics = Arrays.asList(
			"topic1", "topic2", "topic3", "topic4"
	);
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


	@Test
	public void testAddMultipleMessageOneTopic() {
		int threads = 10;
		int NUMBER_OF_MESSAGES = threads * 30;
		ExecutorService service = Executors.newFixedThreadPool(threads);
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
		for(IMessage msg:messages) {
			service.execute(new Runnable() {
				@Override
				public void run() {
					annuaire.addMessage(msg, "One Topic");

				}
			});
		}
		service.shutdown();
		while (!service.isTerminated())
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		Assert.assertEquals(NUMBER_OF_MESSAGES, annuaire.getMessagesOfTopic("One Topic").size());
		for (int i = 0; i < NUMBER_OF_MESSAGES; i++) {
			Assert.assertEquals(1, annuaire.getTopicsOfMessage(messages.get(i)).size());
		}
		Assert.assertEquals(0, annuaire.getUsers().length);
		Assert.assertEquals(1,annuaire.getTopics().length);
	}

	@Test
	public void addSubscriptionTest() {
		int nbTopics = 10;
		int threads = 7;
		int NUMBER_OF_MESSAGES = 10;
		int NB_SUBSCRIBER = 1;
		ExecutorService service = Executors.newFixedThreadPool(threads);
		String top = "MyTopic";
		String [] topics = new String[nbTopics];
		for(int i=0; i<nbTopics; i++)
		{
			topics[i] = top+i;
		}
		for(String topic : topics) {
			service.execute(new Runnable() {
				@Override
				public void run() {
					annuaire.addSubscription("user", topic);
				}
			});
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}


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

		for(IMessage msg:messages) {
			service.execute(new Runnable() {
				@Override
				public void run() {
					annuaire.addMessage(msg, "One Topic");
				}
			});
		}
		service.shutdown();

		while(!service.isTerminated())
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		Assert.assertEquals(NB_SUBSCRIBER,annuaire.getUsers().length);
		Assert.assertEquals(NUMBER_OF_MESSAGES, annuaire.getMessagesOfTopic("One Topic").size());
		Assert.assertEquals(1, annuaire.getURIsOfTopic("MyTopic1").size());
		Preference p = new Preference("MyTopic0");
	}


	@Test
	public void testEverything() {
		int NB_TOPIC = 10;
		int NB_SUBSCRIBERS = 6;
		int NB_PUBLISHERS = 600;
		final int NB_MESSAGE_PER_PUBLISHER = 4;
		int NB_THREADS = 3;
		List<String> publishers = new Vector<>();
		for (int i = 0; i < NB_PUBLISHERS; i++) {
			publishers.add("publisher " + (i + 1));
		}
		List<String> subscribers = new Vector<>();
		for (int i = 0; i < NB_SUBSCRIBERS; i++) {
			subscribers.add("subscriber " + (i + 1));
		}
		List<String> topics = new Vector<>();
		for (int i = 0; i < NB_TOPIC; i++) {
			topics.add("topic " + (i + 1));
		}
		Map<String, List<IMessage>> messagesRecieves = new ConcurrentHashMap<>();
		for (int i = 0; i < NB_SUBSCRIBERS; i++) {
			messagesRecieves.put(subscribers.get(i), new Vector<>());
		}
		// Create subscriptions
		for (int i = 0; i < NB_SUBSCRIBERS; i++) {
			for (int j = 0; j < NB_TOPIC; j++) {
				if (j % NB_SUBSCRIBERS == i) {
					annuaire.addSubscription(subscribers.get(i), topics.get(j));
				}
			}
		}
		ExecutorService executor = Executors.newFixedThreadPool(NB_THREADS);
		AtomicInteger cptMessage = new AtomicInteger(0);
		for (String publisher : publishers) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					for (int i = 0; i < NB_MESSAGE_PER_PUBLISHER; i++) {
						for (int j = 0; j < NB_TOPIC; j++) {
							IMessage m = factory.newMessage(
									"message " + cptMessage.incrementAndGet(),
									publisher
							);
							annuaire.addMessage(m, topics.get(j));
							try {
								Thread.sleep(((int) Math.random()) * 50);
							} catch (InterruptedException e) {
								e.printStackTrace();
								Assert.fail("probleme de sleep");
							}
							// ICI le broker va envoyer le message aux subscribers
							List<String> toSend = annuaire.getURIsToMessage(m);
							for (String subscriber : toSend) {
								messagesRecieves.get(subscriber).add(m);
							}
							// ICI on supprime le message une fois que tous les messages sont envoyés
							annuaire.destroyMessage(m);
						}
					}
				}
			});
		}
		executor.shutdown();
		while (!executor.isTerminated()) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				Assert.fail("Error while sleep");
			}
		}
		// Tout le monde a reçu les messages
		for (int i = 0; i < subscribers.size(); i++) {
			String subscriber = subscribers.get(i);
			int nbTopic;
			if (i >  3) {
				nbTopic = 1;
			} else {
				nbTopic = 2;
			}
			int expected = NB_PUBLISHERS * NB_MESSAGE_PER_PUBLISHER * nbTopic;
			Assert.assertEquals(expected, messagesRecieves.get(subscriber).size());
		}
		Assert.assertEquals(NB_TOPIC, annuaire.getTopics().length);
		for (String topic : topics) {
			Assert.assertEquals(0, annuaire.getMessagesOfTopic(topic).size());
//			Assert.assertNull(annuaire.getMessagesOfTopic(topic));
		}
		int nb_messages = NB_PUBLISHERS * NB_MESSAGE_PER_PUBLISHER * NB_TOPIC;
		System.out.printf("Send : %d messages\nPublishers : %d\nSubscribers : %d\nWell send\n",
				nb_messages,
				NB_PUBLISHERS,
				NB_SUBSCRIBERS
		);
	}


	@Test
	public void testEverythingFamine(){
		final int NB_TEST = 1000;
		System.out.printf("Testing annuaire %d times\n", NB_TEST);
		for (int i = 0; i < NB_TEST; i++) {
			try {
				testEverything();
			} catch (AssertionError e){
				System.out.println("iterations : " + i);
				System.out.println(e.getMessage());
			}
		}
	}

}
