package p2p.von.messages;

import actionsim.core.Payload;
import p2p.von.VonAgent;

public class Update implements Payload {

	private VonAgent agent;
	private float x;
	private float y;
	private boolean isFinal;

	public Update(VonAgent agent, float x, float y, boolean isFinal) {

		this.agent = agent;
		this.x = x;
		this.y = y;
		this.isFinal = isFinal;
	}

	public VonAgent getAgent() {

		return agent;
	}

	public float getX() {

		return x;
	}

	public float getY() {

		return y;
	}

	public boolean isFinal() {

		return isFinal;
	}

	@Override
	public float getSize() {

		return 1;
	}
}
