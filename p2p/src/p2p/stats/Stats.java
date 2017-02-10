package p2p.stats;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import actionsim.core.Simulation;

public class Stats {

	// CHORD
	public static Metric internalMessagesReceived = new Metric("CHORD Internal messages received");

	// SCRIBE
	public static Metric scribeAllMessagesReceived = new Metric("SCRIBE all messages received");
	public static Metric scribeTileMessagesReceived = new Metric("SCRIBE tile messages received");
	public static Metric scribeCellMessagesReceived = new Metric("SCRIBE cell messages received");
	public static Metric scribeSubscriptionMessagesReceived = new Metric("SCRIBE subscription messages received");

	// common
	public static Metric pvsSize = new Metric("PVS Size");
	public static Metric deltaPvs = new Metric("Delta PVS");
	public static Metric serverFetchesOfUrgent = new Metric("Server Fetch Because Of Urgent");
	public static Metric serverFetchesOfNullEnvelope = new Metric("Server Fetch Because Of NULL Envelope");
	public static Metric serverFetchesOfTimeout = new Metric("Server Fetch Because Of Timeout");

	// content streaming
	public static Metric tilesFromServer = new Metric("Tiles From Server");
	public static Metric tilesFromAgents = new Metric("Tiles From Agents");
	public static Metric queryHops = new Metric("Query Hops");
	public static Metric missingTilesAfterSecond = new Metric("Missing Tiles After Second");
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

		double timePassed = (simulation.getCurrentTime() - startOffset) / 1000.0;

		if (reportRequested || timePassed == 300) {

			System.out.println("Simulation time: " + timePassed);

			Field[] fields = Stats.class.getDeclaredFields();
			List<Field> sortedFields = Arrays.asList(fields);

			Collections.sort(sortedFields, new Comparator<Field>() {

				@Override
				public int compare(Field o1, Field o2) {

					return o1.getName().compareTo(o2.getName());
				}
			});

			for (Field field : sortedFields) {

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
