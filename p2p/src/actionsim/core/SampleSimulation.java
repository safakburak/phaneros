package actionsim.core;

import java.util.ArrayList;

public class SampleSimulation {

	public static void main(String[] args) {
		
		Simulation simulation = new Simulation();
		
		ArrayList<Node> nodes = new ArrayList<Node>();
		
		for(int i = 0; i < 1000; i++) {
			
			nodes.add(simulation.createNode());
		}
		
		long start = System.currentTimeMillis();
		
		int messageCount = 10;
		
		while(messageCount-- > 0) {
			
			Node nodeA = nodes.get((int) (Math.random() * nodes.size()));
			Node nodeB = nodes.get((int) (Math.random() * nodes.size()));
			
			nodeA.send(new Message(nodeA, nodeB, null));
			
			simulation.iterate(5);
		}
		
		System.out.println((System.currentTimeMillis() - start));
	}
}
