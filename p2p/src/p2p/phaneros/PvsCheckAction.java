package p2p.phaneros;

import p2p.common.AbstractAgent;
import p2p.timer.TimedAction;
import p2p.visibility.VisibilityCell;

public class PvsCheckAction implements TimedAction {

	private VisibilityCell forCell;
	private AbstractAgent agent;

	public PvsCheckAction(AbstractAgent agent, VisibilityCell forCell) {

		this.agent = agent;
		this.forCell = forCell;
	}

	@Override
	public void act(float time) {

		agent.requestPvs(forCell, true);
	}
}
