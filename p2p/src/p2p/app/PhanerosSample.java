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
import p2p.phaneros.PhanerosAgent;
import p2p.phaneros.MapServer;
import p2p.renderer.Renderer;
import p2p.util.Persist;

public class PhanerosSample {

	public static void main(String[] args) throws IOException {

		ArrayList<AbstractAgent> agents = new ArrayList<AbstractAgent>();

		Random random = new Random();

		Logger.init(System.out, Logger.INFO);

		World world = (World) Persist.load("data/world/random_fixed_range.world");

		Cache cache = new FullCache(world.getMap(), world.getVisibility().getCellSize());

		Simulation simulation = new Simulation();

		MapServer server = new MapServer(simulation.createNode("server"), world.getMap(),
				world.getVisibility().getCellSize());
		agents.add(new PhanerosAgent(simulation.createNode("agent"), world.getVisibility(), 10, server.getNode(),
				world.getWidth(), world.getHeight(), cache));

		agents.get(0).setKeepOthers(true);

		new Renderer(world, simulation, agents.get(0), agents);

		int agentCount = 1000;

		while (agentCount-- > 0) {

			PhanerosAgent agent = new PhanerosAgent(simulation.createNode(), world.getVisibility(), 10,
					server.getNode(), world.getWidth(), world.getHeight(), cache);

			int x;
			int y;

			do {

				x = random.nextInt(world.getMap().getWidth());
				y = random.nextInt(world.getMap().getHeight());

			} while (world.getMap().getHeightAtAbs(x, y) != 0);

			agent.setPosition(x, y);
			agents.add(agent);
		}

		server.getChordNode().createNetwork();

		for (AbstractAgent agent : agents) {

			PhanerosAgent phanerosAgent = (PhanerosAgent) agent;
			phanerosAgent.getChordNode().joinNetwork(server.getChordNode().getId());

			simulation.iterate(10);
		}

		// stabilize network
		simulation.iterate(300);

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
