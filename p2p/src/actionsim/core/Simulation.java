package actionsim.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Simulation {

	private List<Node> nodes = new ArrayList<Node>();

	private float simStepSize = SimulationConfiguration.simulationStepSize;
	
	private volatile boolean simStopFlag = false;
	
	private Thread simThread = null;
	
	
	@SuppressWarnings("unchecked")
	public <T extends Node> T createNode () {
		
		Method me;
		T result = null;
		
		try {
			me = getClass().getMethod("createNode");
			result = (T) me.getReturnType().getConstructor(int.class).newInstance(nodes.size());
			
			synchronized (nodes) {
				
				nodes.add(result);
			}
		} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return result;
	}

	public void iterate(int iterations) {
		
		while(iterations-- > 0) {

			step();
		}
	}
	
	public void start() {

		stop();
		
		simThread = new Thread(new Runnable() {
			
			public void run() {
				
				while(simStopFlag == false) {
					
					step();
				}
			}
		});
		
		simThread.start();
	}
	
	public void stop() {
		
		if(simThread != null) {
			
			simStopFlag = true;
			
			try {
				
				simThread.join();
			} 
			catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
	}
	
	private void step() {
		
		synchronized (nodes) {
			
			for(Node node : nodes) {
				
				node.processMessages(simStepSize);
			}
			
			for(Node node : nodes) {
				
				node.processActions(simStepSize);
			}
			
			for(Node node : nodes) {
				
				node.deliverMessages(simStepSize);
			}
			
		}
	}
}
