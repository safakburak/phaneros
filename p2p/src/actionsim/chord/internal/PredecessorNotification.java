package actionsim.chord.internal;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;

public class PredecessorNotification extends ChordMessage {

	public PredecessorNotification(ChordId origin, ChordId target) {

		super(origin, target);
	}
}
