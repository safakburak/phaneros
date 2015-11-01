package actionsim.chord;

import java.util.ArrayList;

public class ChordMessage {

	private ChordId from;
	
	private ChordId to;
	
	private ArrayList<ChordId> hopHistory = new ArrayList<ChordId>();
	
	public ChordMessage(ChordId from, ChordId to) {

		this.from = from;
		setTo(to);
	}
	
	public ChordId getFrom() {
		
		return from;
	}
	
	public ChordId getTo() {
		
		return to;
	}
	
	public void hop(ChordId id) {
		
		hopHistory.add(id);
	}
	
	public int getHopCount() {
		
		return hopHistory.size();
	}
	
	public void setTo(ChordId target) {
		
		this.to = target;
	}
	
	public void setFrom(ChordId origin) {
		
		this.from = origin;
	}
	
	@Override
	public String toString() {
		
		return getClass().getSimpleName() + " from: " + getFrom() + " to: " + getTo() + " hops: " + getHopCount();
	}
	
	public float getSize() {
		
		return 1;
	}
}
