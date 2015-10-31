package actionsim.scribe.inner;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;

public class Publish extends ChordMessage {

	private ChordId publisher;
	
	private ChordId topic;
	
	private Object value;
	
	public Publish(ChordId publisher, ChordId key, Object value) {
		
		super(publisher, key);
		
		this.publisher = publisher;
		this.topic = key;
		this.value = value;
	}
	
	public ChordId getPublisher() {
		
		return publisher;
	}
	
	public ChordId getKey() {
		
		return topic;
	}
	
	public Object getValue() {
		
		return value;
	}
}
