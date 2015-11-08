package p2p.network.actionsim;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;
import p2p.network.IMessage;

public class ActionSimEnvelope extends ChordMessage {

	private IMessage payload;
	
	public ActionSimEnvelope(ChordId from, ChordId to, IMessage payload) {
		
		super(from, to);
		
		this.payload = payload;
	}
	
	public IMessage getPayload() {
		
		return payload;
	}
}
