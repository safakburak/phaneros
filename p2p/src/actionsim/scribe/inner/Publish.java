package actionsim.scribe.inner;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;

public class Publish extends ChordMessage {

	private ChordId topic;
	
	private Object value;
	
	public Publish(ChordId from, ChordId to, ChordId topic, Object value) {
		
		super(from, to);
		
		this.topic = topic;
		this.value = value;
	}
	
	public ChordId getTopic() {
		
		return topic;
	}
	
	public Object getValue() {
		
		return value;
	}
}