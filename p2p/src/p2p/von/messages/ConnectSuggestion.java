package p2p.von.messages;

import java.util.List;

import actionsim.core.Payload;
import p2p.von.VonAgent;

public class ConnectSuggestion implements Payload {

	private List<VonAgent> agents;

	public ConnectSuggestion(List<VonAgent> agents) {

		this.agents = agents;
	}

	public List<VonAgent> getAgents() {

		return agents;
	}

	@Override
	public float getSize() {

		return 1 * agents.size();
	}
}
