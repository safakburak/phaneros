package p2p.common.messages;

import actionsim.core.Node;
import actionsim.core.Payload;
import p2p.map.Region;

public class TileRequest implements Payload {

	private Node node;
	private Region region;

	public TileRequest(Node node, Region region) {

		this.node = node;
		this.region = region;
	}

	public Node getNode() {

		return node;
	};

	public Region getRegion() {

		return region;
	}

	@Override
	public float getSize() {

		return 1;
	}
}
