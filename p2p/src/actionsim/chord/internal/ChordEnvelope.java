package actionsim.chord.internal;

import actionsim.chord.ChordMessage;
import actionsim.core.Message;
import actionsim.core.Node;

public class ChordEnvelope extends Message {

	private ChordMessage payload;
	
	public ChordEnvelope(Node from, Node to, ChordMessage payload) {
		
		super(from, to);
		
		this.payload = payload;
	}
	
	public ChordMessage getPayload() {
		
		return payload;
	}
	
	@Override
	public float getSize() {
		
		return super.getSize() + 0 + payload.getSize();
	}
}
