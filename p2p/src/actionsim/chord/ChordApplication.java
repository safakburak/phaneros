package actionsim.chord;

import actionsim.core.Message;

public interface ChordApplication {

	public boolean onForward(ChordMessage message, ChordId to);
	
	public void onMessage(ChordMessage message);
	
	public void onMessage(Message message);
	
	public void onEntryValue(ChordId key, Object value);
	
}
