package actionsim.chord.internal;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;

public class EntryQuery extends ChordMessage {

	private ChordId key;
	
	public EntryQuery(ChordId origin, ChordId target, ChordId key) {
		
		super(origin, target);
		
		this.key = key;
	}
	
	public ChordId getKey() {
		
		return key;
	}
}
