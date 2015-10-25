package actionsim.chord.internal;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;

public class PredecessorResponse extends ChordMessage {

	private ChordId predecessor;
	
	
	public PredecessorResponse(ChordId origin, ChordId target, ChordId predecessor) {
		
		super(origin, target);
		
		this.predecessor = predecessor;
	}

	public ChordId getPredecessor() {
		
		return predecessor;
	}
	
}
