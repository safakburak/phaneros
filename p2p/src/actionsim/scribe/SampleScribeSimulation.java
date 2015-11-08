package actionsim.scribe;

import java.util.ArrayList;

import actionsim.chord.ChordId;
import actionsim.core.Node;
import actionsim.core.Simulation;
import p2p.log.Logger;

public class SampleScribeSimulation {

	public static void main(String[] args) {
		
		Logger.init(System.out);
		
		Simulation simulation = new Simulation();
		
		ArrayList<ScribeNode> nodes = new ArrayList<ScribeNode>();
		
		for(int i = 0; i < 1000; i++) {
			
			Node node = simulation.createNode();
			
			ScribeNode scribeNode = new ScribeNode(node);
			
			nodes.add(scribeNode);
			
			if(i == 0) {
				
				scribeNode.getChordNode().createNetwork();
			}
			else {
				
				scribeNode.getChordNode().joinNetwork(nodes.get(0).getChordNode().getId());
			}
			
			//stabilize network
			simulation.iterate(5);
		}
		
		simulation.iterate(300);
		
//		nodes.get(0).getChordNode().report(nodes.size());
		
		ScribeNode nodeA = nodes.get((int) (Math.random() * nodes.size()));
		ScribeNode nodeB = nodes.get((int) (Math.random() * nodes.size()));
		ScribeNode nodeC = nodes.get((int) (Math.random() * nodes.size()));
		ScribeNode nodeD = nodes.get((int) (Math.random() * nodes.size()));
		
		ChordId topic = new ChordId("topic");
		
		nodeA.subscribe(topic);
		nodeB.subscribe(topic);
		nodeC.subscribe(topic);
		nodeD.subscribe(topic);
		
		simulation.iterate(10);
		
		nodeD.publish(topic, "Hello World! I'm Alive!");
		
		simulation.iterate(20);
	}
}
