package p2p.visibility.nuv.agent;

import java.util.TreeSet;

import actionsim.chord.ChordNode;
import actionsim.core.Node;
import actionsim.scribe.ScribeNode;
import p2p.visibility.nuv.map.Map;

public class Agent {

	private TreeSet<Map> cache;
	private ChordNode chordNode;
	private ScribeNode scribeNode;
	
	public Agent(Node node) {

		this.scribeNode = new ScribeNode(node);
		this.chordNode = scribeNode.getChordNode();
	}
	
	public void start() {
		
		
	}
}
