package p2p.stats;

import java.lang.reflect.Field;

import actionsim.core.Simulation;

public class Stats {

	// common
	public static Metric pvsSize = new Metric("PVS Size");
	public static Metric deltaPvs = new Metric("Delta PVS");

	// content streaming
	public static Metric tilesFromServer = new Metric("Tiles From Server");
	public static Metric tilesFromAgents = new Metric("Tiles From Agents");
	public static Metric queryHops = new Metric("Query Hops");
	public static Metric tilesMissingAfterSecond = new Metric("Missing Tiles After Second");
	public static Metric fetchDelay = new Metric("Fetch Delay");

	// update dissemination
	public static Metric cellChange = new Metric("Cell Change");
	public static Metric cellStay = new Metric("Cell Stay");
	public static Metric updatesSend = new Metric("Update Send");
	public static Metric simultaneousConnections = new Metric("Simultaneous Connections");
	public static Metric aoiNeighbors = new Metric("AOI Neighbors");
	public static Metric suggestions = new Metric("Suggestions");
	public static Metric subscriptions = new Metric("Subscriptions");

	private static boolean reportRequested = false;
	private static Simulation simulation;

	private static long startOffset;

	public static synchronized void init(Simulation simulation) {

		Stats.simulation = simulation;
		startOffset = (long) simulation.getCurrentTime();
	}

	public static synchronized void requestReport() {

		reportRequested = true;
	}

	public static synchronized void report() {

		if (reportRequested) {

			System.out.println("Simulation time: " + (simulation.getCurrentTime() - startOffset) / 1000.0);

			for (Field field : Stats.class.getDeclaredFields()) {

				if (field.getType() == Metric.class) {

					try {

						Metric metric = (Metric) field.get(null);
						metric.report();

					} catch (Exception e) {

						e.printStackTrace();
					}
				}

			}

			reportRequested = false;
		}
	}

	public static long getTime() {

		return (long) simulation.getCurrentTime();
	}
}
