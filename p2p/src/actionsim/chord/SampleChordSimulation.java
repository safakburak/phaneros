package actionsim.chord;

import actionsim.core.Simulation;
import actionsim.logger.Logger;

public class SampleChordSimulation {

	public static void main(String[] args) {

		Logger.init(System.out);
		
		Simulation simulation = new Simulation();
		
		ChordNode seed = new ChordNode(simulation.createNode());
		seed.createNetwork();
		
		for(int i = 0; i < 1000; i++) {
			
			ChordNode node = new ChordNode(simulation.createNode());
			node.joinNetwork(seed.getId());
			simulation.iterate(5);
		}
		
		simulation.iterate(300);

		
		System.out.println(seed.gather().size());

		int messageCount = 10000;
		
		while(messageCount-- > 0) {
			
			ChordNode nodeA = (ChordNode) simulation.getNode((int) (Math.random() * simulation.getNodeCount())).getApplication();
			ChordNode nodeB = (ChordNode) simulation.getNode((int) (Math.random() * simulation.getNodeCount())).getApplication();
			
			ChordMessage message = new ChordMessage(nodeA.getId(), nodeB.getId());
			nodeA.sendMessage(message);
			
			simulation.iterate(10);
		}
		
		simulation.iterate(100);
		
//		ChordNode nodeA = (ChordNode) simulation.getNode((int) (Math.random() * simulation.getNodeCount())).getApplication();
//		ChordNode nodeB = (ChordNode) simulation.getNode((int) (Math.random() * simulation.getNodeCount())).getApplication();
//		ChordNode nodeC = (ChordNode) simulation.getNode((int) (Math.random() * simulation.getNodeCount())).getApplication();
//		
//		ChordId topic = new ChordId("topic");
//		
//		nodeA.setEntry(topic, 100);
//		
//		simulation.iterate(15);
//		
//		nodeC.setEntry(topic, 101);
//		
//		simulation.iterate(15);
//		
//		nodeB.requestEntry(topic);
//		
//		simulation.iterate(15);
	}
}
