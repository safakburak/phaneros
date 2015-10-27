
package actionsim.chord.internal;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;

public class EntryUpdate extends ChordMessage {
	
	private ChordId key;
	
	private Object value;
	
	public EntryUpdate(ChordId origin, ChordId target, ChordId key, Object value) {
		
		super(origin, target);
		
		this.key = key;
		this.value = value;
	}
	
	public ChordId getKey() {
		
		return key;
	}
	
	public Object getValue() {
		
		return value;
	}
}
