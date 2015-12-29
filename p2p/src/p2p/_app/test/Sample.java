package p2p._app.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import actionsim.core.Simulation;
import actionsim.log.Logger;
import p2p._app.agent.Agent;
import p2p._app.agent.KeyManager;
import p2p._app.agent.Renderer;
import p2p._app.agent.Server;
import p2p._app.common.Persist;
import p2p._app.map.World;

public class Sample {

	public static void main(String[] args) throws IOException {
		
		ArrayList<Agent> agents = new ArrayList<Agent>();
		
		Random random = new Random();
		
		Logger.init(Logger.TRACE);
		
		World world = (World) Persist.load("data/world/random_fixed_range.world");
		
		Simulation simulation = new Simulation();
		
		Server server = new Server(simulation.createNode("server"), world.getMap(), world.getVisibility().getCellSize());
		agents.add(new Agent(simulation.createNode("agent"), world.getVisibility(), 10, server.getNode()));
		
		agents.get(0).setKeepOthers(true);
		Renderer renderer = new Renderer(agents.get(0));
		KeyManager keyManager = new KeyManager(simulation, renderer);
		
		int agentCount = 500;
		
		while(agentCount-- > 0) {
			
			Agent agent = new Agent(simulation.createNode(), world.getVisibility(), 10, server.getNode());
			
			int x;
			int y;
			
			do {
				
				x = random.nextInt(world.getMap().getWidth());
				y = random.nextInt(world.getMap().getHeight());
				
			} while(world.getMap().getHeightAtAbs(x, y) != 0);
			
			agent.setPosition(x, y);
			agents.add(agent);
		}
		
		server.getChordNode().createNetwork();

		for(Agent agent : agents) {
			
			agent.getChordNode().joinNetwork(server.getChordNode().getId());
			
			simulation.iterate(10);
		}
		
		//stabilize network
		simulation.iterate(300);
		
		for(Agent agent : agents) {
			
			agent.start();
		}
		
		long renderTime = 0;
		
		while(true) {
			
			simulation.iterate(10);
			
			long time = System.currentTimeMillis();
			
			if(time - renderTime > 100) {
				
				renderTime = time;
				renderer.paint(renderer.getGraphics());
			}
			
			try {
				
				Thread.sleep(1);
				
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
	}
}
