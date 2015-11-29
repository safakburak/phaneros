package actionsim.chord;

import actionsim.chord.internal.AbstractMessage;

public interface ChordApplication {

	public boolean beforeForward(AbstractMessage message, ChordId to);
	
	public void onChordMessage(AbstractMessage message);
}
