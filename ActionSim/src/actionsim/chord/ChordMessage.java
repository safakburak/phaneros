package actionsim.chord;

import actionsim.chord.internal.AbstractMessage;

public class ChordMessage extends AbstractMessage {

	private ChordId to;
	
	public ChordMessage(ChordId to) {
		
		this.to = to;
	}
	
	public ChordId getTo() {
		
		return to;
	}
}
