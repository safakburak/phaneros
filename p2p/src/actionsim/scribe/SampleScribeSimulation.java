package actionsim.scribe;

import java.util.ArrayList;

import actionsim.core.Node;
import actionsim.core.Simulation;
import actionsim.logger.Logger;

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
		
		
	}
}
