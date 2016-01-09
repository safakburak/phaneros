package actionsim.chord;

import actionsim.chord.internal.AbstractChordMessage;

public interface ChordApplication {

	public boolean beforeForward(AbstractChordMessage message, ChordId to);
	
	public void onChordMessage(AbstractChordMessage message);
}
