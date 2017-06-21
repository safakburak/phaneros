package actionsim.core;

public class Message {

	private Node from;

	private Node to;

	private Payload payload;

	public int retries = 0;

	public Message(Node from, Node to, Payload payload) {

		this.from = from;
		this.to = to;
		this.payload = payload;
	}

	public Node getFrom() {

		return from;
	}

	public Node getTo() {

		return to;
	}

	public Object getPayload() {

		return payload;
	}

	public float getSize() {

		return payload.getSize() + 0.04f;
	}

	@Override
	public String toString() {

		return "message from " + from + " to " + to;
	}
}
