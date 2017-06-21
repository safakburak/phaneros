package p2p.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import actionsim.core.DefaultConfiguration;
import actionsim.core.Node;
import actionsim.core.Simulation;
import actionsim.logging.Logger;
import p2p.common.AbstractAgent;
import p2p.common.MapServer;
import p2p.map.World;
import p2p.phaneros.PhanerosAgent;
import p2p.renderer.Renderer;
import p2p.stats.Stats;
import p2p.util.Persist;

public class PhanerosSample {

	private static MapServer server;

	private static int numberOfAgents = 1000;
	private static float uploadBandwidth = 2048;
	private static float downloadBandwidth = 8192;

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws IOException {

		ArrayList<AbstractAgent> agents = new ArrayList<AbstractAgent>();

		Random random = new Random();

		Logger.init(System.out, Logger.INFO);

		World world = (World) Persist.load("data/world/random_pvs.world");

		Simulation simulation = new Simulation(new DefaultConfiguration() {

			@Override
			public Float getUploadBandwidth(Node node) {

				if (node.getId().equals("server")) {

					return null;

				} else {

					return uploadBandwidth;
				}
			}

			@Override
			public Float getDownloadBandwidth(Node node) {

				if (node.getId().equals("server")) {

					return null;

				} else {

					return downloadBandwidth;
				}
			}
		});

		server = new MapServer(simulation.createNode("server"), world.getAtlas(), world.getVisibility().getCellSize());

		int agentCount = numberOfAgents;

		while (agentCount-- > 0) {

			PhanerosAgent agent = new PhanerosAgent(simulation.createNode(), world.getVisibility(), 60,
					server.getNode(), world.getWidth(), world.getHeight(), null);

			int x;
			int y;

			do {

				x = random.nextInt(world.getAtlas().getWidth());
				y = random.nextInt(world.getAtlas().getHeight());

			} while (world.getAtlas().get(x, y) != 0);

			agent.setPosition(x, y);
			agents.add(agent);

			if (agents.size() > 1) {

				agent.fillCache(server);
			}
		}

		agents.get(0).setPosition(502, 502);
		agents.get(0).fillCache(server);

		new Renderer(world, simulation, agents.get(0), agents, world.getAtlas());

		server.getChordNode().createNetwork();

		for (AbstractAgent agent : agents) {

			PhanerosAgent phanerosAgent = (PhanerosAgent) agent;
			phanerosAgent.getChordNode().joinNetwork(server.getChordNode().getId());

			simulation.iterate(10);
		}

		// stabilize network
		simulation.iterate(300);

		Stats.init(simulation);

		for (AbstractAgent agent : agents) {

			agent.start();
		}

		while (true) {

			simulation.iterate(10);

			Stats.report();

			try {

				Thread.sleep(1);

			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
	}
}
