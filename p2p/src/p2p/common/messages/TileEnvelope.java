package p2p.common.messages;

import actionsim.core.Payload;
import p2p.map.Region;
import p2p.map.Tile;

public class TileEnvelope implements Payload {

	private Region region;
	private Tile tile;

	public TileEnvelope(Tile tile, Region region) {

		this.tile = tile;
		this.region = region;
	}

	public Tile getTile() {

		return tile;
	}

	public Region getRegion() {
		return region;
	}

	@Override
	public float getSize() {

		return 512;
	}
}
