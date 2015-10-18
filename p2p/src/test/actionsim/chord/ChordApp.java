package test.actionsim.chord;

import java.util.ArrayList;

import actionsim.chord.ChordMessage;
import actionsim.chord.ChordNode;
import actionsim.core.Simulation;

public class ChordApp {

	public static void main(String[] args) {

		Simulation simulation = new Simulation();
		
		ArrayList<ChordNode> nodes = new ArrayList<ChordNode>();
		
		for(int i = 0; i < 1000; i++) {
			
			ChordNode node = (ChordNode) simulation.createNode(ChordNode.class);
			node.joinNetwork();
			
			nodes.add(node);
		}
		
		simulation.iterate(5000);
		
		
//		System.out.println("*******************");
//		node.listRecursive();
		
		ChordNode nodeA = nodes.get(50);
		ChordNode nodeB = nodes.get(750);
		
		System.out.println(nodeA.getChordId());
		System.out.println(nodeB.getChordId());
		
		ChordMessage message = new ChordMessage(nodeA.getChordId(), nodeB.getChordId(), null);
		nodeA.send(message);
		
		simulation.iterate(100);
		
	}
}
