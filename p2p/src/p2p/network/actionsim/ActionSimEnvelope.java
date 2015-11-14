package p2p.network.actionsim;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;
import p2p.network.IMessage;

public class ActionSimEnvelope extends ChordMessage {

	private IMessage payload;
	private ChordId from;
	
	public ActionSimEnvelope(ChordId from, ChordId to, IMessage payload) {
		
		super(to);
		
		this.from = from;
		this.payload = payload;
	}
	
	public IMessage getPayload() {
		
		return payload;
	}
	
	public ChordId getFrom() {
		
		return from;
	}
}
