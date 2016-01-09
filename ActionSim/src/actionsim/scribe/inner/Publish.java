package actionsim.scribe.inner;

import java.util.List;

import actionsim.chord.ChordId;
import actionsim.chord.ChordMessage;
import actionsim.core.Payload;

public class Publish extends ChordMessage {

	private ChordId topic;
	private Payload payload;

	public Publish(ChordId topic, Payload payload) {

		this(topic, topic, payload, null);
	}

	public Publish(ChordId to, ChordId topic, Payload payload, List<ChordId> existingHistory) {

		super(to);

		this.topic = topic;
		this.payload = payload;

		if (existingHistory != null) {

			addHops(existingHistory);
		}
	}

	public ChordId getTopic() {

		return topic;
	}

	public Payload getValue() {

		return payload;
	}

	@Override
	public float getSize() {

		return payload.getSize();
	}
}
