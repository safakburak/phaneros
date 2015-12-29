package actionsim.scribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import actionsim.chord.ChordApplication;
import actionsim.chord.ChordId;
import actionsim.chord.ChordNode;
import actionsim.chord.internal.AbstractMessage;
import actionsim.core.Node;
import actionsim.scribe.inner.Publish;
import actionsim.scribe.inner.Subscribe;
import actionsim.scribe.inner.Unsubscribe;

public class ScribeNode implements ChordApplication {

	private ChordNode chordNode;

	private HashMap<ChordId, ArrayList<ChordId>> routes = new HashMap<ChordId, ArrayList<ChordId>>();

	private ArrayList<ChordId> subscriptions = new ArrayList<ChordId>();

	private ArrayList<ScribeListener> listeners = new ArrayList<>();

	
	public ScribeNode(Node node) {

		chordNode = new ChordNode(node);

		chordNode.addApplication(this);
	}

	@Override
	public void onChordMessage(AbstractMessage message) {

		if (message instanceof Publish) {

			Publish publish = (Publish) message;

			if (subscriptions.contains(publish.getTopic())) {

				for(ScribeListener listener : listeners) {
					
					listener.onScribeMessage(publish.getTopic(), ((Publish) message).getValue());
				}
			}

			ArrayList<ChordId> children = routes.get(publish.getTopic());

			if (children != null) {

				for (ChordId child : children) {

					chordNode.send(child, new Publish(child, publish.getTopic(), publish.getValue()));
				}
			}
		} 
		else if (message instanceof Subscribe) {

			Subscribe subscribe = (Subscribe) message;

			ArrayList<ChordId> children = routes.get(subscribe.getTopic());

			if (children == null) {

				children = new ArrayList<ChordId>();
				routes.put(subscribe.getTopic(), children);
			}

			if(subscribe.getLastHop() == null) {
				//subscribing to topic owned by me
			}
			else {
				
				if (children.contains(subscribe.getLastHop()) == false) {
					
					children.add(subscribe.getLastHop());
				}
			}
			
		} 
		else if (message instanceof Unsubscribe) {

			Unsubscribe unsubscribe = (Unsubscribe) message;

			if (routes.containsKey(unsubscribe.getTopic())) {

				routes.get(unsubscribe.getTopic()).remove(unsubscribe.getLastHop());
			}
			
		} 
	}

	@Override
	public boolean beforeForward(AbstractMessage message, ChordId to) {

		if (message instanceof Publish) {

			return true;
			
		} else if (message instanceof Subscribe) {

			Subscribe subscribe = (Subscribe) message;

			
			ArrayList<ChordId> children = routes.get(subscribe.getTopic());
			
			if (children == null) {
				
				children = new ArrayList<ChordId>();
				routes.put(subscribe.getTopic(), children);
			}

			if (children.contains(subscribe.getLastHop()) == false) {

				children.add(subscribe.getLastHop());
			}

			chordNode.send(new Subscribe(subscribe.getTopic()));

			return false;
			
		} else if (message instanceof Unsubscribe) {

			Unsubscribe unsubscribe = (Unsubscribe) message;

			ArrayList<ChordId> children = routes.get(unsubscribe.getTopic());

			if (children != null) {

				children.remove(unsubscribe.getLastHop());

				if (children.size() == 0) {

					chordNode.send(new Unsubscribe(unsubscribe.getTopic()));
				}
			}

			return false;
		}

		return true;
	}

	public ChordNode getChordNode() {

		return chordNode;
	}

	public void subscribe(ChordId topic) {

		if (subscriptions.contains(topic) == false) {

			subscriptions.add(topic);
			chordNode.send(new Subscribe(topic));
		}
	}

	public void unsubscribe(ChordId topic) {

		subscriptions.remove(topic);

		chordNode.send(new Unsubscribe(topic));
	}

	public void publish(ChordId topic, Object value) {

		Publish publish = new Publish(topic, value);

		chordNode.send(publish);
	}

	public void addScribeListener(ScribeListener listener) {

		if(listeners.contains(listener) == false) {
			
			listeners.add(listener);
		}
	}
	
	public List<ChordId> getSubscriptions() {
		
		return subscriptions;
	}
}
