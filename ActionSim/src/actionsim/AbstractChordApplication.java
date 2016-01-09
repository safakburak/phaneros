package actionsim;

import actionsim.chord.ChordApplication;
import actionsim.chord.ChordId;
import actionsim.chord.internal.AbstractChordMessage;

public class AbstractChordApplication implements ChordApplication {

	@Override
	public boolean beforeForward(AbstractChordMessage message, ChordId to) {
		
		return true;
	}

	@Override
	public void onChordMessage(AbstractChordMessage message) {

	}
}
