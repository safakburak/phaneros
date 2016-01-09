package p2p.common.messages;

import actionsim.core.Payload;

public class TileRequest implements Payload {

	private int x;
	private int y;

	public TileRequest(int x, int y) {

		this.x = x;
		this.y = y;
	}

	public int getX() {

		return x;
	}

	public int getY() {

		return y;
	}

	@Override
	public float getSize() {

		return 1;
	}
}
