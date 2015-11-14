package p2p.network.actionsim;

import java.util.ArrayList;
import java.util.HashMap;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;
import actionsim.chord.internal.AbstractMessage;
import actionsim.scribe.ScribeListener;
import actionsim.scribe.ScribeNode;
import p2p.network.IEndPoint;
import p2p.network.IForwardListener;
import p2p.network.IMessage;
import p2p.network.IMessageListener;

public class ActionSimEndPoint implements IEndPoint, ScribeListener {

	private ScribeNode scribeNode;
	
	private ChordId nodeId;
	
	@SuppressWarnings("rawtypes")
	private HashMap<Class<?>, ArrayList<IMessageListener>> listenersMap = new HashMap<Class<?>, ArrayList<IMessageListener>>();
	
	
	public ActionSimEndPoint(ScribeNode scribeNode) {
		
		this.scribeNode = scribeNode;
		scribeNode.setListener(this);
		
		nodeId = scribeNode.getChordNode().getId();
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void addMessageListener(Class<?> messageClass, IMessageListener listener) {
		
		ArrayList<IMessageListener> listeners = listenersMap.get(messageClass);
		
		if(listeners == null) {
			
			listeners = new ArrayList<IMessageListener>();
			listenersMap.put(messageClass, listeners);
		} 
		
		if(listeners.contains(listener) == false) {
			
			listeners.add(listener);
		}
	}

	@Override
	public void subscribe(String topic) {

		scribeNode.subscribe(new ChordId(topic));
	}

	@Override
	public void unsubscribe(String topic) {
		
		scribeNode.unsubscribe(new ChordId(topic));
	}

	
	@Override
	public void castMessage(String topic, IMessage message) {

		scribeNode.publish(new ChordId(topic), message);
	}

	
	@Override
	public void sendMessage(String dest, IMessage message) {

		scribeNode.getChordNode().send(new ActionSimEnvelope(nodeId, new ChordId(dest), message));
	}

	@Override
	public void setForwardDecider(IForwardListener decider) {

		//killed
	}

	
	@Override
	public void onScribeMessage(ChordId topic, Object message) {

		if(message instanceof IMessage) {
			
			distributeToListeners(null, (IMessage) message);
		}
	}
	
	@Override
	public void onChordMessage(ChordMessage message) {

		if(message instanceof ActionSimEnvelope /*&& message.getFrom().equals(nodeId) == false*/) {
			
			ActionSimEnvelope envelope = (ActionSimEnvelope) message;
			IMessage payload = envelope.getPayload();
			distributeToListeners(envelope.getFrom().getKeyText(), payload);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void distributeToListeners(String from, IMessage message) {
		
		ArrayList<IMessageListener> listeners = listenersMap.get(message.getClass());
		
		if(listeners != null) {
			
			for(IMessageListener listener : listeners) {
				
				listener.onMessage(message, from);
			}
		}
	}

	public ScribeNode getScribeNode() {
		
		return scribeNode;
	}
}
