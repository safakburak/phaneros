package actionsim.chord.internal;

import actionsim.chord.ChordId;

public class SuccessorQuery extends BaseMessage {

	private ChordId key;
	
	
	public SuccessorQuery(ChordId source, ChordId destination, ChordId key) {
		
		super(source, destination);
		
		this.key = key;
	}
	
	public ChordId getKey() {
		
		return key;
	}
}
