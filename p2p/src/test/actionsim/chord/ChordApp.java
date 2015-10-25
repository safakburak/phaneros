package test.actionsim.chord;

import actionsim.chord.ChordMessage;
import actionsim.chord.ChordNode;
import actionsim.core.Simulation;
import actionsim.logger.Logger;

public class ChordApp {

	public static void main(String[] args) {

		Logger.init(System.out);
		
		Simulation simulation = new Simulation();
		
		ChordNode seed = (ChordNode) simulation.createNode(ChordNode.class);
		seed.createNetwork();
		
		for(int i = 0; i < 1000; i++) {
			
			ChordNode node = (ChordNode) simulation.createNode(ChordNode.class);
			node.joinNetwork(seed.getChordId());
			simulation.iterate(5);
		}
		
		simulation.iterate(300);

//		System.out.println(seed.gather().size());
		

		int messageCount = 10000;
		
		while(messageCount-- > 0) {
			
			ChordNode nodeA = simulation.getNode((int) (Math.random() * simulation.getNodeCount()));
			ChordNode nodeB = simulation.getNode((int) (Math.random() * simulation.getNodeCount()));
			
			ChordMessage message = new ChordMessage(nodeA.getChordId(), nodeB.getChordId());
			nodeA.sendChordMessage(message);
			
			simulation.iterate(10);
		}
		
		simulation.iterate(100);
		
		System.out.println(ChordNode.totalHops / 10000.0);
	}
}
