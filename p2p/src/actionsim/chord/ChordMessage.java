package actionsim.chord;

import java.util.ArrayList;

public class ChordMessage {

	private ChordId origin;
	
	private ChordId target;
	
	private ArrayList<ChordId> hopHistory = new ArrayList<ChordId>();
	
	public ChordMessage(ChordId origin, ChordId target) {

		this.origin = origin;
		setTarget(target);
	}
	
	public ChordId getOrigin() {
		
		return origin;
	}
	
	public ChordId getTarget() {
		
		return target;
	}
	
	public void hop(ChordId id) {
		
		hopHistory.add(id);
	}
	
	public int getHopCount() {
		
		return hopHistory.size();
	}
	
	public void setTarget(ChordId target) {
		
		this.target = target;
	}
	
	public void setOrigin(ChordId origin) {
		
		this.origin = origin;
	}
}
