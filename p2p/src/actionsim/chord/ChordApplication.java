package actionsim.chord;

public interface ChordApplication {

	public boolean onForward(ChordMessage message, ChordId to);
	
	public void onMessage(ChordMessage message);
	
	public void onEntryValue(ChordId key, Object value);
}
