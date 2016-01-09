package actionsim.scribe.inner;

import java.util.List;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;

public class Publish extends ChordMessage {

	private ChordId topic;
	private Object value;

	public Publish(ChordId topic, Object value) {

		this(topic, topic, value, null);
	}

	public Publish(ChordId to, ChordId topic, Object value, List<ChordId> existingHistory) {

		super(to);

		this.topic = topic;
		this.value = value;

		if (existingHistory != null) {

			addHops(existingHistory);
		}
	}

	public ChordId getTopic() {

		return topic;
	}

	public Object getValue() {

		return value;
	}
}
