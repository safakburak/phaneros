package p2p.von.messages;

import java.util.List;

import p2p.von.VonAgent;

public class ConnectSuggestion {

	private List<VonAgent> agents;

	public ConnectSuggestion(List<VonAgent> agents) {

		this.agents = agents;
	}

	public List<VonAgent> getAgents() {

		return agents;
	}
}
