package actionsim.scribe.inner;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;

public class Subscribe extends ChordMessage {

	private ChordId subscriber;
	
	private ChordId topic;
	
	public Subscribe(ChordId subscriber, ChordId topic) {
		
		super(subscriber, topic);
		
		this.subscriber = subscriber;
		this.topic = topic;
	}
	
	public ChordId getSubscriber() {
		
		return subscriber;
	}
	
	public ChordId getTopic() {
		
		return topic;
	}
}
