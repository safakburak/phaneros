package actionsim.scribe.inner;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;

public class Subscribe extends ChordMessage {

	private ChordId topic;
	
	public Subscribe(ChordId topic) {
		
		super(topic);
		
		this.topic = topic;
	}
	
	public ChordId getTopic() {
		
		return topic;
	}
}
