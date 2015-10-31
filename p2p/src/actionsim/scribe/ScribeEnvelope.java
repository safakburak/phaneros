package actionsim.scribe;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;

public class ScribeEnvelope extends ChordMessage {

	
	
	public ScribeEnvelope(ChordId origin, ChordId target) {
		
		super(origin, target);
	}
}
