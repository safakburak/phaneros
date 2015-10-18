package actionsim.chord;

import actionsim.chord.internal.BaseMessage;

public class ChordMessage extends BaseMessage {

	private Object payload;
	
	public ChordMessage(ChordId source, ChordId destination, Object payload) {
		
		super(source, destination);
	}
	
	public Object getPayload() {
		
		return payload;
	}
}
