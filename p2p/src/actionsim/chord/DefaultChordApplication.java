package actionsim.chord;

import actionsim.chord.internal.AbstractMessage;
import p2p.log.Logger;

public class DefaultChordApplication implements ChordApplication {

	private ChordId id; 
	
	public DefaultChordApplication(ChordId id) {
		
		this.id = id;
	}
	
	public static float totalHops = 0;
	public static float totalMessages = 0;
	
	@Override
	public void onChordMessage(AbstractMessage message) {

		Logger.log(id + " received: \n" + message, Logger.TRACE);
		
		totalHops += message.getHopCount();
		totalMessages++;
	}
	
	@Override
	public boolean beforeForward(AbstractMessage message, ChordId to) {
		
		return true;
	}
	
	@Override
	public void onEntryValue(ChordId key, Object value) {
		
	}
}
