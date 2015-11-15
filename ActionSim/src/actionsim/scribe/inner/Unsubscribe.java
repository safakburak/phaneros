package actionsim.scribe.inner;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;

public class Unsubscribe extends ChordMessage {

	private ChordId topic;
	
	public Unsubscribe(ChordId topic) {
		
		super(topic);
		
		this.topic = topic;
	}
	
	public ChordId getTopic() {
		
		return topic;
	}
}
