package actionsim.chord.internal;

import actionsim.chord.ChordId;
import actionsim.chord.ChordNode;
import actionsim.core.Message;

public class BaseMessage extends Message{

	private ChordId source;
	
	private ChordId destination;
	
	public BaseMessage(ChordId source, ChordId destination) {
		
		super(ChordNode.node(source), ChordNode.node(destination));
		
		this.source = source;
		
		this.destination = destination;
	}
	
	public ChordId getSource() {
		
		return source;
	}
	
	public ChordId getDestination() {
		
		return destination;
	}
}
