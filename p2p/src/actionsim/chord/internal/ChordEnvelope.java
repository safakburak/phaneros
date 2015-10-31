package actionsim.chord.internal;

import actionsim.chord.ChordMessage;
import actionsim.core.Message;
import actionsim.core.Node;

public class ChordEnvelope extends Message {

	private Node source;
	
	private Node target;
	
	private ChordMessage payload;
	
	public ChordEnvelope(Node from, Node to, ChordMessage payload) {
		
		super(from, to);
		
		this.source = from;
		
		this.target = to;
		
		this.payload = payload;
	}
	
	public Node getSourceNode() {
		
		return source;
	}
	
	public Node getTargetNode() {
		
		return target;
	}
	
	public ChordMessage getPayload() {
		
		return payload;
	}
	
	@Override
	public float getSize() {
		
		return super.getSize() + 0 + payload.getSize();
	}
}
