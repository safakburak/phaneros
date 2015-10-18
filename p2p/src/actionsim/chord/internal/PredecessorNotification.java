package actionsim.chord.internal;

import actionsim.chord.ChordId;

public class PredecessorNotification extends BaseMessage {

	public PredecessorNotification(ChordId source, ChordId destination) {

		super(source, destination);
	}
}
