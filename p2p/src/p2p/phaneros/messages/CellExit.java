package p2p.phaneros.messages;

import p2p.phaneros.PhanerosAgent;
import p2p.visibility.VisibilityCell;

public class CellExit {

	private VisibilityCell cell;
	private PhanerosAgent agent;

	public CellExit(VisibilityCell cell, PhanerosAgent agent) {

		this.cell = cell;
		this.agent = agent;
	}

	public VisibilityCell getCell() {
		return cell;
	}

	public PhanerosAgent getAgent() {
		return agent;
	}
}
