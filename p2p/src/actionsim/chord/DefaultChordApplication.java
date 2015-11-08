package actionsim.chord;

import p2p.log.Logger;

public class DefaultChordApplication implements ChordApplication {

	private ChordId id; 
	
	public DefaultChordApplication(ChordId id) {
		
		this.id = id;
	}
	
	public static float totalHops = 0;
	public static float totalMessages = 0;
	
	@Override
	public void onChordMessage(ChordMessage message) {

		Logger.log(id + " received: \n\t" + message);
		
		totalHops += message.getHopCount();
		totalMessages++;
	}
	
	@Override
	public boolean onForward(ChordMessage message, ChordId to) {
		
		return true;
	}
	
	@Override
	public void onEntryValue(ChordId key, Object value) {
		
	}
}
