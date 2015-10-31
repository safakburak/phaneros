package actionsim.chord;

public interface ChordApplication {

	public void onMessage(ChordMessage message);
	
	public void onEntryValue(ChordId key, Object value);
}
