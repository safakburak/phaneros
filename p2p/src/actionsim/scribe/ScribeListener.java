package actionsim.scribe;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;

public interface ScribeListener {

	public void onScribeMessage(ChordId topic, ChordId origin, Object message);
	
	public void onChordMessage(ChordMessage message);
	
}
