package actionsim.chord;

import actionsim.core.Message;
import actionsim.logger.Logger;

public class DefaultChordApplication implements ChordApplication {

	private ChordId id; 
	
	public DefaultChordApplication(ChordId id) {
		
		this.id = id;
	}
	
	@Override
	public void onMessage(ChordMessage message) {

		Logger.log(id + " received: \n\t" + message);
	}
	
	@Override
	public void onMessage(Message message) {
		
	}
	
	@Override
	public boolean onForward(ChordMessage message, ChordId to) {
		
		return true;
	}
	
	@Override
	public void onEntryValue(ChordId key, Object value) {
		
	}
}
