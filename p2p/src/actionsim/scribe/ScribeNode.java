package actionsim.scribe;

import java.util.ArrayList;
import java.util.HashMap;

import actionsim.chord.ChordApplication;
import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;
import actionsim.chord.ChordNode;
import actionsim.core.Node;
import actionsim.scribe.inner.Publish;
import actionsim.scribe.inner.Subscribe;
import actionsim.scribe.inner.Unsubscribe;
import p2p.log.Logger;

public class ScribeNode implements ChordApplication {

	private ChordNode chordNode;

	private HashMap<ChordId, ArrayList<ChordId>> routes = new HashMap<ChordId, ArrayList<ChordId>>();

	private ArrayList<ChordId> subscriptions = new ArrayList<ChordId>();

	private ScribeListener listener;

	public ScribeNode(Node node) {

		chordNode = new ChordNode(node);

		chordNode.setApplication(this);
	}

	@Override
	public void onChordMessage(ChordMessage message) {

		if (message instanceof Publish) {

			Publish publish = (Publish) message;

//			Logger.log(chordNode.getId() + " received: " + publish);

			if (subscriptions.contains(publish.getTopic())) {

				onScribeMessage(publish.getTopic(), publish.getOrigin(), ((Publish) message).getValue());
			}

			ArrayList<ChordId> children = routes.get(publish.getTopic());

			if (children != null) {

				for (ChordId child : children) {

					chordNode.send(child, new Publish(publish.getOrigin(), chordNode.getId(), child, publish.getTopic(),
							publish.getValue()));
				}
			}
		} else if (message instanceof Subscribe) {

			Subscribe subscribe = (Subscribe) message;

			ArrayList<ChordId> children = routes.get(subscribe.getTopic());

			if (children == null) {

				children = new ArrayList<ChordId>();
				routes.put(subscribe.getTopic(), children);
			}

			if (children.contains(subscribe.getFrom()) == false) {

				children.add(subscribe.getFrom());
			}
		} else if (message instanceof Unsubscribe) {

			Unsubscribe unsubscribe = (Unsubscribe) message;

			if (routes.containsKey(unsubscribe.getTopic())) {

				routes.get(unsubscribe.getTopic()).remove(unsubscribe.getSubscriber());
			}
		} else {

			listener.onChordMessage(message);
		}
	}

	@Override
	public boolean onForward(ChordMessage message, ChordId to) {

		if (message instanceof Publish) {

//			Logger.log(chordNode.getId() + " received: " + message);

			return true;
		} else if (message instanceof Subscribe) {

			Subscribe subscribe = (Subscribe) message;

			if (routes.containsKey(subscribe.getTopic()) == false) {

				routes.put(subscribe.getTopic(), new ArrayList<ChordId>());
			}

			ArrayList<ChordId> children = routes.get(subscribe.getTopic());

			if (children.contains(subscribe.getFrom()) == false) {

				children.add(subscribe.getFrom());
			}

			chordNode.send(new Subscribe(chordNode.getId(), subscribe.getTopic()));

			return false;
		} else if (message instanceof Unsubscribe) {

			Unsubscribe unsubscribe = (Unsubscribe) message;

			ArrayList<ChordId> children = routes.get(unsubscribe.getTopic());

			if (children != null) {

				children.remove(unsubscribe.getSubscriber());

				if (children.size() == 0) {

					chordNode.send(new Unsubscribe(chordNode.getId(), unsubscribe.getTopic()));
				}
			}

			return false;
		}

		return true;
	}

	@Override
	public void onEntryValue(ChordId key, Object value) {

	}

	public ChordNode getChordNode() {

		return chordNode;
	}

	public void subscribe(ChordId topic) {

		if (subscriptions.contains(topic) == false) {

			subscriptions.add(topic);
		}

		chordNode.send(new Subscribe(chordNode.getId(), topic));
	}

	public void unsubscribe(ChordId topic) {

		subscriptions.remove(topic);

		chordNode.send(new Unsubscribe(chordNode.getId(), topic));
	}

	public void publish(ChordId topic, Object value) {

		Publish publish = new Publish(chordNode.getId(), chordNode.getId(), topic, topic, value);

		chordNode.send(publish);

//		Logger.log(chordNode.getId() + " published: " + publish);
	}

	private void onScribeMessage(ChordId topic, ChordId origin, Object value) {

		if (listener != null) {

			listener.onScribeMessage(topic, origin, value);
		}
	}

	public void setListener(ScribeListener listener) {

		this.listener = listener;
	}
}
