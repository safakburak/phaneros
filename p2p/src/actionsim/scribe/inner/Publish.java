package actionsim.scribe.inner;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;

public class Publish extends ChordMessage {

	private ChordId origin;
	
	private ChordId topic;
	
	private Object value;
	
	public Publish(ChordId origin, ChordId from, ChordId to, ChordId topic, Object value) {
		
		super(from, to);
		
		this.topic = topic;
		this.value = value;
		this.origin = origin;
	}
	
	public ChordId getTopic() {
		
		return topic;
	}
	
	public Object getValue() {
		
		return value;
	}
	
	public ChordId getOrigin() {
		
		return origin;
	}
	
	@Override
	public String toString() {
		
		return "Publish on " + topic + " from " + origin;
	}
}
