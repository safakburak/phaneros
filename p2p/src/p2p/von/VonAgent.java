package p2p.von;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import actionsim.AbstractNodeListener;
import actionsim.core.Message;
import actionsim.core.Node;
import p2p.common.AbstractAgent;
import p2p.common.RandomWalker;
import p2p.map.Map;
import p2p.phaneros.messages.PatchRequest;
import p2p.timer.TimedAction;
import p2p.visibility.Visibility;

public class VonAgent extends AbstractAgent {

	private RandomWalker walker;
	
	private Node mapServer;
	
	private VonGraph graph = new VonGraph(this);
	
	private Set<VonAgent> enclosingAgents = new HashSet<VonAgent>();
	private Set<VonAgent> aoiAgents = new HashSet<VonAgent>();

	
	public VonAgent(Node node, Visibility visibility, int cacheSize, Node mapServer, int worldWidth, int worldHeight, Map worldMap) {
		
		super(node, visibility, cacheSize, worldMap);
		
		this.mapServer = mapServer;
		
		walker = new RandomWalker(this, worldWidth, worldHeight);
	}

	
	@Override
	public void start() {
	
		node.addNodeListener(new AbstractNodeListener() {
			
			@Override
			public void onMessage(Message message) {
				
				Object payload = message.getPayload();
				
				if(payload instanceof Map) {
					
					cache.addPatch((Map) payload);
				}
			}
		});
		
		timer.repeat(new TimedAction() {
			
			@Override
			public void act(float time) {
				
				walker.walk();
			}
		}, 500);
	}
	
	@Override
	public void onCacheMissAt(int x, int y) {
	
		mapServer.send(new Message(node, mapServer, new PatchRequest(x, y)));
	}

	@Override
	public void onPositionChange() {
		
	}
	
	public void init(ArrayList<AbstractAgent> agents, VonGraph graph) {

		int range = visibility.getMaxRange();
		
		for(AbstractAgent agent : agents) {

			double dX = agent.getX() - x; 
			double dY = agent.getY() - y;
			
			if( dX * dX + dY * dY < range * range) {
			
				aoiAgents.add((VonAgent) agent);
			}
		}
		
		enclosingAgents = graph.getEnclosing(this);
	}
}
