package actionsim.core;

import java.util.ArrayList;
import java.util.List;

public class Simulation {

	private List<Node> nodes = new ArrayList<Node>();

	private float stepLength = Configuration.simulationStepLength;
	
	private long currentStep = 0;
	
	public Node createNode (Class<? extends Node> clazz) {
		
		Node result = null;
		
		try {
			
			result = clazz.getConstructor(long.class).newInstance(nodes.size());
		} 
		catch (Exception e) {
			
			e.printStackTrace();
		}

		nodes.add(result);

		
		return result;
	}

	public void iterate(float duration) {
		
		int iterations = (int) (duration / stepLength);
		
		while(iterations-- > 0) {

			step();
		}
	}

	public void iterate(int iterations) {
		
		while(iterations-- > 0) {

			step();
		}
	}
	
	private void step() {
		
		synchronized (nodes) {
			
			for(Node node : nodes) {
				
				node.processMessages(stepLength);
			}
			
			for(Node node : nodes) {
				
				node.processActions(stepLength);
			}
			
			for(Node node : nodes) {
				
				node.deliverMessages(stepLength);
			}
			
		}
		
		currentStep++;
	}
	
	public void setLength(long length) {
		
		this.stepLength = length;
	}

	public long getCurrentStep() {
		
		return currentStep;
	}
	
	public float getCurrentTime() {
		
		return currentStep * stepLength;
	}
	
	public int getNodeCount() {
		
		return nodes.size();
	}
	
	public <T> T getNode(int index) {
		
		Node node = nodes.get(index);
		
		return (T) node;
	}
}
