package p2p.phaneros;

import p2p.timer.TimedAction;
import p2p.visibility.VisibilityCell;

public class PvsCheckAction implements TimedAction {

	private VisibilityCell forCell;
	private PhanerosAgent agent;

	public PvsCheckAction(PhanerosAgent agent, VisibilityCell forCell) {

		this.agent = agent;
		this.forCell = forCell;
	}

	@Override
	public void act(float time) {

		agent.requestPvs(forCell, true);
	}
}
