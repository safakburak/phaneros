package actionsim.chord;

import actionsim.chord.internal.AbstractChordMessage;
import actionsim.log.Logger;

public class DefaultChordApplication implements ChordApplication {

	private ChordId id; 
	
	public DefaultChordApplication(ChordId id) {
		
		this.id = id;
	}
	
	public static float totalHops = 0;
	public static float totalMessages = 0;
	
	@Override
	public void onChordMessage(AbstractChordMessage message) {

		Logger.log(id + " received: \n" + message, Logger.TRACE);
		
		totalHops += message.getHopCount();
		totalMessages++;
	}
	
	@Override
	public boolean beforeForward(AbstractChordMessage message, ChordId to) {
		
		return true;
	}
}
