package actionsim.chord.internal;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;

public class SuccessorResponse extends ChordMessage implements InternalMessage {
	
	private ChordId key;
	private ChordId successor;

	public SuccessorResponse(ChordId origin, ChordId target, ChordId key, ChordId successor) {
		
		super(origin, target);
		
		this.key = key;
		this.successor = successor;
	}
	
	public ChordId getKey() {
		
		return key;
	}
	
	public ChordId getSuccessor() {
		
		return successor;
	}
	
}
