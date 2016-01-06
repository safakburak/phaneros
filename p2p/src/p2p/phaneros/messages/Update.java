package p2p.phaneros.messages;

import p2p.phaneros.PhanerosAgent;

public class Update {

	private PhanerosAgent agent;
	private int x;
	private int y;

	public Update(PhanerosAgent agent, int x, int y) {

		this.agent = agent;
		this.x = x;
		this.y = y;
	}

	public PhanerosAgent getAgent() {

		return agent;
	}

	public int getX() {

		return x;
	}

	public int getY() {

		return y;
	}
}
