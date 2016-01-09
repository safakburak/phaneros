package p2p.common.messages;

import actionsim.core.Payload;
import p2p.map.Tile;

public class TileEnvelope implements Payload {

	private Tile tile;

	public TileEnvelope(Tile tile) {

		this.tile = tile;
	}

	public Tile getTile() {

		return tile;
	}

	@Override
	public float getSize() {

		return 64;
	}
}
