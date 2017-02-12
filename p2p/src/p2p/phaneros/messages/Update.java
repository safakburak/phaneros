package p2p.phaneros.messages;

import actionsim.core.Payload;
import p2p.phaneros.PhanerosAgent;

public class Update implements Payload {

	private PhanerosAgent agent;
	private float x;
	private float y;

	public Update(PhanerosAgent agent, float x, float y) {

		this.agent = agent;
		this.x = x;
		this.y = y;
	}

	public PhanerosAgent getAgent() {

		return agent;
	}

	public float getX() {

		return x;
	}

	public float getY() {

		return y;
	}

	@Override
	public float getSize() {

		return 1;
	}
}
