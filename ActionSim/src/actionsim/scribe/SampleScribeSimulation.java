package actionsim.scribe;

import java.util.ArrayList;

import actionsim.chord.ChordId;
import actionsim.core.Payload;
import actionsim.core.Simulation;
import actionsim.log.Logger;

public class SampleScribeSimulation {

	public static void main(String[] args) {

		// Logger.init(System.out, Logger.INFO);
		Logger.init(Logger.INFO);

		Simulation simulation = new Simulation();

		ArrayList<ScribeNode> nodes = new ArrayList<ScribeNode>();

		for (int i = 0; i < 1000; i++) {

			ScribeNode scribeNode = new ScribeNode(simulation.createNode());

			if (i == 0) {

				scribeNode.getChordNode().createNetwork();
			} else {

				scribeNode.getChordNode().joinNetwork(nodes.get(0).getChordNode().getId());
			}

			nodes.add(scribeNode);

			simulation.iterate(10);
		}

		simulation.iterate(50);

		Logger.setLevel(Logger.TRACE);

		// nodes.get(0).getChordNode().report(nodes.size());

		ChordId topic = new ChordId("topic");

		nodes.get((int) (Math.random() * nodes.size())).subscribe(topic);
		nodes.get((int) (Math.random() * nodes.size())).subscribe(topic);
		nodes.get((int) (Math.random() * nodes.size())).subscribe(topic);
		nodes.get((int) (Math.random() * nodes.size())).subscribe(topic);
		nodes.get((int) (Math.random() * nodes.size())).subscribe(topic);
		nodes.get((int) (Math.random() * nodes.size())).subscribe(topic);
		nodes.get((int) (Math.random() * nodes.size())).subscribe(topic);

		simulation.iterate(10);

		ScribeNode publisher = nodes.get((int) (Math.random() * nodes.size()));
		publisher.subscribe(topic);
		publisher.publish(topic, new StringPayload("Hello World! I'm Alive!"));

		simulation.iterate(20);
	}

	private static class StringPayload implements Payload {

		private String string;

		public StringPayload(String string) {

			this.string = string;
		}

		@Override
		public float getSize() {

			return string.length();
		}

		@Override
		public String toString() {

			return string;
		}
	}
}
