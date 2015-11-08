package p2p.network.actionsim;

import java.util.ArrayList;

import actionsim.core.Simulation;
import actionsim.scribe.ScribeNode;
import p2p.application.loop.ILoopListener;
import p2p.application.loop.Loop;
import p2p.log.Logger;

public class ActionSimNetwork {

	public static Simulation simulation;
	
	private static ArrayList<ActionSimEndPoint> endPoints = new ArrayList<ActionSimEndPoint>();
	
	
	public static void initialize() {

		simulation = new Simulation();
		
		Loop.setTimer(new ILoopListener() {
			@Override
			public void loopCallback(long deltaTime)
			{
				simulation.iterate(deltaTime);
			}
		});
	}
	
	public static ActionSimEndPoint getEndPoint(String id) {
		
		ActionSimEndPoint endPoint = new ActionSimEndPoint(new ScribeNode(simulation.createNode(id)));
		
		endPoints.add(endPoint);
		
		if(endPoints.size() == 1) {
			
			endPoint.getScribeNode().getChordNode().createNetwork();
		}
		else {
			
			endPoint.getScribeNode().getChordNode().joinNetwork(endPoints.get(0).getScribeNode().getChordNode().getId());
		}
		
		simulation.iterate(5);
		
		return endPoint;
	} 
	
}
