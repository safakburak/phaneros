package p2p.stats;

import java.lang.reflect.Field;

import actionsim.core.Simulation;

public class Stats {

	public static Metric tilesFromServer = new Metric("Tiles from server");
	public static Metric tilesFromAgents = new Metric("Tiles from agents");

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
}
