package actionsim.scribe;

import actionsim.chord.ChordId;

public class DefaultScribeListener implements ScribeListener {

	private ChordId chordId;

	public DefaultScribeListener(ChordId chordId) {
		
		this.chordId = chordId;
	}
	
	@Override
	public void onScribeMessage(ChordId topic, Object message) {

		System.out.println(chordId);
		System.out.println(message);
	}
}
