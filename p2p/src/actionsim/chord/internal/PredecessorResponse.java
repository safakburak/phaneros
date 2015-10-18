package actionsim.chord.internal;

import actionsim.chord.ChordId;

public class PredecessorResponse extends BaseMessage {

	private ChordId predecessor;
	
	
	public PredecessorResponse(ChordId source, ChordId destination, ChordId predecessor) {
		
		super(source, destination);
		
		this.predecessor = predecessor;
	}

	public ChordId getPredecessor() {
		
		return predecessor;
	}
	
}
