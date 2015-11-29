package actionsim.chord;

import java.util.ArrayList;

import actionsim.core.Simulation;
import actionsim.log.Logger;

public class SampleChordSimulation {

	public static void main(String[] args) {

		Logger.init(System.out, Logger.INFO);
//		Logger.init(Logger.TRACE);
		
		Simulation simulation = new Simulation();
		
		ArrayList<ChordNode> nodes = new ArrayList<ChordNode>();
		
		for(int i = 0; i < 1000; i++) {
			
			ChordNode node = new ChordNode(simulation.createNode());
			
			if(i == 0) {
				
				node.createNetwork();
			}
			else {
				
				node.joinNetwork(nodes.get(0).getId());
			}
			
			nodes.add(node);
			
			simulation.iterate(10);
		}
		
		simulation.iterate(50);
		
		
		int messageCount = 1000;
		
		long start = System.currentTimeMillis();
		
		while(messageCount-- > 0) {
			
			ChordNode nodeA = (ChordNode) nodes.get((int) (Math.random() * nodes.size()));
			ChordNode nodeB = (ChordNode) nodes.get((int) (Math.random() * nodes.size()));
			
			ChordMessage message = new ChordMessage(nodeB.getId());
			nodeA.send(message);
			
			simulation.iterate(10);
		}
		
		System.out.println("messages: " + DefaultChordApplication.totalMessages);
		System.out.println("avg hops: " + DefaultChordApplication.totalHops / DefaultChordApplication.totalMessages);
		System.out.println("sum time: " + (System.currentTimeMillis() - start));
		System.out.println("avg time: " + (System.currentTimeMillis() - start) / DefaultChordApplication.totalMessages);
		
		
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
