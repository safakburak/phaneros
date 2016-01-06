package p2p.phaneros.messages;

import p2p.phaneros.PhanerosAgent;
import p2p.visibility.VisibilityCell;

public class CellEnter {

	private int x;
	private int y;
	private VisibilityCell cell;
	private PhanerosAgent agent;

	public CellEnter(VisibilityCell cell, PhanerosAgent agent, int x, int y) {

		this.cell = cell;
		this.agent = agent;
		this.x = x;
		this.y = y;
	}

	public VisibilityCell getCell() {
		return cell;
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
