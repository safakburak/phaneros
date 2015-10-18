package actionsim.chord.internal;

import actionsim.chord.ChordId;

public class PredecessorQuery extends BaseMessage {

	public PredecessorQuery(ChordId source, ChordId destination) {
		
		super(source, destination);
	}
}
