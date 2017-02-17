package actionsim.chord;

import actionsim.chord.internal.InternalMessage;

public interface ChordLowLevelListener {

	void onInternalMessage(InternalMessage message);

	void onExternalMessage(ChordMessage message);

	void onExternalMessageArrive(ChordMessage message);
}
