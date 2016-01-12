package actionsim.scribe;

import actionsim.chord.ChordId;

public interface ScribeListener {

	public void onScribeMessage(ChordId topic, Object message, int hopCount);

}
