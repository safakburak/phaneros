package p2p._app.test;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import actionsim.core.Node;
import actionsim.core.Simulation;
import p2p._app.agent.Agent;
import p2p._app.agent.KeyManager;
import p2p._app.agent.Renderer;
import p2p._app.agent.Server;
import p2p._app.common.Persist;
import p2p._app.map.Map;
import p2p._app.map.World;
import p2p._app.visibility.Visibility;

public class Sample {

	public static void main(String[] args) throws IOException {
		
		World world = (World) Persist.load("data/world/random_fixed_range.world");
		
		Simulation simulation = new Simulation();
		
		Server server = new Server(simulation.createNode("server"), world.getMap(), world.getVisibility().getCellSize());
		Agent agent = new Agent(simulation.createNode("agent"), world.getVisibility(), 10, server.getNode());
		
		Renderer renderer = new Renderer(agent);
		
		KeyManager keyManager = new KeyManager(simulation, renderer);
		
		long renderTime = 0;
		
		while(true) {
			
			simulation.iterate(10);
			
			long time = System.currentTimeMillis();
			
			if(time - renderTime > 100) {
				
				renderTime = time;
				renderer.repaint();
			}
			
			try {
				
				Thread.sleep(1);
				
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
		}
	}
}
