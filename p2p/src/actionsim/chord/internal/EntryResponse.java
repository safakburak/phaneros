package actionsim.chord.internal;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;

public class EntryResponse extends ChordMessage {
	
	private ChordId key;
	
	private Object value;
	
	public EntryResponse(ChordId origin, ChordId target, ChordId key, Object value) {
		
		super(origin, target);
		
		this.key = key;
		this.value = value;
	}

	public ChordId getKey() {
		
		return key;
	}
	
	public Object getValue() {
		
		return value;
	}
}
