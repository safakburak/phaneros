package actionsim.chord.internal;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;

public class SuccessorQuery extends ChordMessage implements InternalMessage {

	private ChordId key;
	
	
	public SuccessorQuery(ChordId origin, ChordId target, ChordId key) {
		
		super(origin, target);
		
		this.key = key;
	}
	
	public ChordId getKey() {
		
		return key;
	}
}
