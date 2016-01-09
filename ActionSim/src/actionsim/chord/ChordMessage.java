package actionsim.chord;

import actionsim.chord.internal.AbstractChordMessage;

public class ChordMessage extends AbstractChordMessage {

	private ChordId to;
	
	public ChordMessage(ChordId to) {
		
		this.to = to;
	}
	
	public ChordId getTo() {
		
		return to;
	}
}
