package actionsim.chord;

public interface ChordApplication {

	public boolean onForward(ChordMessage message, ChordId to);
	
	public void onChordMessage(ChordMessage message);
	
	public void onEntryValue(ChordId key, Object value);
	
}
