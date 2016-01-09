package p2p.common.messages;

import p2p.common.AbstractAgent;
import p2p.map.Region;

public class TileAvailable {

	@SuppressWarnings("rawtypes")
	private AbstractAgent agent;
	private Region region;

	@SuppressWarnings("rawtypes")
	public TileAvailable(AbstractAgent agent, Region region) {

		this.agent = agent;
		this.region = region;
	}

	@SuppressWarnings("rawtypes")
	public AbstractAgent getAgent() {
		return agent;
	}

	public Region getRegion() {

		return region;
	}
}
