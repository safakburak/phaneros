package p2p.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import actionsim.core.Simulation;
import actionsim.log.Logger;
import p2p.common.AbstractAgent;
import p2p.common.Cache;
import p2p.common.FullCache;
import p2p.map.World;
import p2p.phaneros.MapServer;
import p2p.renderer.Renderer;
import p2p.util.Persist;
import p2p.von.VonAgent;
import p2p.von.VonGraph;

public class VonSample {

	public static void main(String[] args) throws IOException {

		Logger.init(System.out, Logger.INFO);
		Random random = new Random();

		ArrayList<AbstractAgent> agents = new ArrayList<AbstractAgent>();

		World world = (World) Persist.load("data/world/random_fixed_range.world");

		Cache cache = new FullCache(world.getMap(), world.getVisibility().getCellSize());
		
		Simulation simulation = new Simulation();

		MapServer server = new MapServer(simulation.createNode("server"), world.getMap(),
				world.getVisibility().getCellSize());

		VonAgent agentOne = new VonAgent(simulation.createNode("agent"), world.getVisibility(), 10, server.getNode(),
				world.getWidth(), world.getHeight(), cache);

		agentOne.setPosition(502, 502);
		
		agents.add(agentOne);

		agentOne.setKeepOthers(true);
		new Renderer(world, simulation, agentOne, agents);

		int agentCount = 1000;

		while (agentCount-- > 0) {

			VonAgent agent = new VonAgent(simulation.createNode(), world.getVisibility(), 10, server.getNode(),
					world.getWidth(), world.getHeight(), cache);

			int x;
			int y;

			do {

				x = random.nextInt(world.getMap().getWidth());
				y = random.nextInt(world.getMap().getHeight());

			} while (world.getMap().getHeightAtAbs(x, y) != 0);

			agent.setPosition(x, y);
			agents.add(agent);
		}

		VonGraph graph = new VonGraph(agents);
		
		for (AbstractAgent agent : agents) {

			((VonAgent)agent).init(agents, graph);
		}

		for (AbstractAgent agent : agents) {

			agent.start();
		}

		while (true) {

			simulation.iterate(10);

			try {

				Thread.sleep(1);

			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
	}
}
