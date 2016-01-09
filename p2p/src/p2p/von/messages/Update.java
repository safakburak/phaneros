package p2p.von.messages;

import actionsim.core.Payload;
import p2p.von.VonAgent;

public class Update implements Payload {

	private VonAgent agent;
	private int x;
	private int y;

	public Update(VonAgent agent, int x, int y) {

		this.agent = agent;
		this.x = x;
		this.y = y;
	}

	public VonAgent getAgent() {

		return agent;
	}

	public int getX() {

		return x;
	}

	public int getY() {

		return y;
	}

	@Override
	public float getSize() {

		return 0.125f;
	}
}
