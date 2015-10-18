package actionsim.chord.internal;

import actionsim.chord.ChordId;

public class SuccessorResponse extends BaseMessage {
	
	private ChordId key;
	private ChordId successor;

	
	public SuccessorResponse(ChordId source, ChordId destination, ChordId key, ChordId successor) {
		
		super(source, destination);
		
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
