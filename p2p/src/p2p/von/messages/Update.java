package p2p.von.messages;

import p2p.von.VonAgent;

public class Update {

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
}
