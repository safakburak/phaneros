package actionsim.chord.internal;

import actionsim.chord.ChordId;

public class SuccessorQuery extends AbstractChordMessage implements InternalMessage {

	private ChordId key;

	public SuccessorQuery(ChordId key) {

		this.key = key;
	}

	public ChordId getKey() {

		return key;
	}
}
