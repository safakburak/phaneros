package actionsim.chord.internal;

import actionsim.chord.ChordId;

public class SuccessorResponse extends AbstractChordMessage implements InternalMessage {
	
	private ChordId key;
	private ChordId successor;

	public SuccessorResponse(ChordId key, ChordId successor) {
		
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
