package actionsim;

import actionsim.chord.ChordApplication;
import actionsim.chord.ChordId;
import actionsim.chord.internal.AbstractMessage;

public class AbstractChordApplication implements ChordApplication {

	@Override
	public boolean beforeForward(AbstractMessage message, ChordId to) {
		
		return true;
	}

	@Override
	public void onChordMessage(AbstractMessage message) {

	}
}
