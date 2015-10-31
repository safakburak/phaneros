package actionsim.chord;

public class DefaultChordApplication implements ChordApplication {

	@Override
	public void onMessage(ChordMessage message) {

		System.out.println(message);
	}
	
	@Override
	public void onEntryValue(ChordId key, Object value) {
		
	}
}
