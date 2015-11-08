package actionsim.chord.internal;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;

public class PredecessorQuery extends ChordMessage implements InternalMessage {

	public PredecessorQuery(ChordId origin, ChordId target) {
		
		super(origin, target);
	}
}
