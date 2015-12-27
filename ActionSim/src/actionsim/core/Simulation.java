package actionsim.core;

import java.util.ArrayList;
import java.util.List;

public class Simulation {

	private Configuration configuration = new DefaultConfiguration();
	
	private List<Node> nodes = new ArrayList<Node>();
	
	private long currentStep = 0;
	
	private boolean isPlay = true;
	
	public Simulation() {
		
	}
	
	public Simulation(Configuration configuration) {
		
		this.configuration = configuration;
	}
	
	public Node createNode() {
		
		String serial = nodes.size() + "";
		
		while(serial.length() < 4) {
			
			serial = "0" + serial;
		}
		
		return createNode("N" + serial);
	}
	
	public Node createNode (String id) {
		
		Node result = new Node(id);
		result.setBandwidth(configuration.getBandwidth(result));
		result.setCpuBudget(configuration.getCpuBudget(result));
		nodes.add(result);
		
		return result;
	}

	public void iterate(float duration) {
		
		int iterations = (int) (duration / configuration.getStepLength());
		
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
		
		if(isPlay) {
			
			synchronized (nodes) {
				
				for(Node node : nodes) {
					
					node.processMessages(configuration.getStepLength());
				}
				
				for(Node node : nodes) {
					
					node.processActions(configuration.getStepLength());
				}
				
				for(Node node : nodes) {
					
					node.deliverMessages(configuration.getStepLength());
				}
				
			}
			
			currentStep++;
		}
	}
	
	public long getCurrentStep() {
		
		return currentStep;
	}
	
	public float getCurrentTime() {
		
		return currentStep * configuration.getStepLength();
	}
	
	public int getNodeCount() {
		
		return nodes.size();
	}
	
	public Node getNode(int index) {
		
		return nodes.get(index);
	}
	
	public void setConfiguration(Configuration configuration) {
		
		this.configuration = configuration;
	}
	
	public void togglePlay() {
		
		isPlay = !isPlay;
	}
}
