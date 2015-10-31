package actionsim.scribe;

import java.util.ArrayList;
import java.util.HashMap;

import actionsim.chord.ChordApplication;
import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;
import actionsim.chord.ChordNode;
import actionsim.core.Node;

public class ScribeNode implements ChordApplication {

	private ChordNode chordNode;
	
	private HashMap<ChordId, ArrayList<ChordId>> routes = new HashMap<ChordId, ArrayList<ChordId>>();
	
	public ScribeNode(Node node) {
		
		chordNode = new ChordNode(node);
		
	}
	
	@Override
	public void onMessage(ChordMessage message) {
		
	}

	@Override
	public boolean onForward(ChordMessage message, ChordId to) {
		
		return true;
	}
	
	@Override
	public void onEntryValue(ChordId key, Object value) {
		
	}

	public ChordNode getChordNode() {
		
		return chordNode;
	}
	
	public void subscribe(ChordId topic) {
		
	}
	
	public void unsubscrive(ChordId topic) {
		
	}
	
	public void publish(ChordId topic, Object value) {
		
	}
}
