package p2p.phaneros.messages;

import actionsim.core.Payload;
import p2p.phaneros.PhanerosAgent;
import p2p.visibility.VisibilityCell;

public class CellExit implements Payload {

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

	@Override
	public float getSize() {

		return 0.125f;
	}
}
