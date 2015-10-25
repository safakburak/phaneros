package actionsim.chord;

import actionsim.core.Message;

public class ChordEnvelope extends Message {

	private ChordId from;
	
	private ChordId to;
	
	private ChordMessage payload;
	
	public ChordEnvelope(ChordId from, ChordId to, ChordMessage payload) {
		
		super(ChordNode.node(from), ChordNode.node(to));
		
		this.from = from;
		
		this.to = to;
		
		this.payload = payload;
	}
	
	public ChordId getFrom() {
		
		return from;
	}
	
	public ChordId getTo() {
		
		return to;
	}
	
	public ChordMessage getPayload() {
		
		return payload;
	}
}
