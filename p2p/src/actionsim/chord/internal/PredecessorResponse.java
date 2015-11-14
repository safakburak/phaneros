package actionsim.chord.internal;

import actionsim.chord.ChordId;

public class PredecessorResponse extends AbstractMessage implements InternalMessage {

	private ChordId predecessor;
	
	
	public PredecessorResponse(ChordId predecessor) {
		
		this.predecessor = predecessor;
	}

	public ChordId getPredecessor() {
		
		return predecessor;
	}
	
}
