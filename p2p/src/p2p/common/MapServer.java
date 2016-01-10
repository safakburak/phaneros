package p2p.common;

import actionsim.AbstractNodeListener;
import actionsim.chord.ChordNode;
import actionsim.core.Message;
import actionsim.core.Node;
import actionsim.scribe.ScribeNode;
import p2p.common.messages.TileAvailable;
import p2p.common.messages.TileEnvelope;
import p2p.common.messages.TileQuery;
import p2p.common.messages.TileRequest;
import p2p.map.Atlas;
import p2p.map.Tile;

public class MapServer {

	private Node node;
	private ScribeNode scribeNode;
	private ChordNode chordNode;
	private Tile[][] tiles;
	private int tileSize;

	public MapServer(Node node, Atlas atlas, int tileSize) {

		this.node = node;
		this.scribeNode = new ScribeNode(node);
		this.chordNode = scribeNode.getChordNode();
		this.tileSize = tileSize;

		initialize(atlas);
	}

	private void initialize(Atlas atlas) {

		int colNum = atlas.getWidth() / tileSize;
		int rowNum = atlas.getHeight() / tileSize;

		tiles = new Tile[colNum][rowNum];

		for (int col = 0; col < colNum; col++) {
			for (int row = 0; row < rowNum; row++) {

				tiles[col][row] = atlas.getTile(col * tileSize, row * tileSize, tileSize);
			}
		}

		node.addNodeListener(new AbstractNodeListener() {

			@Override
			public void onMessage(Message message) {

				Object payload = message.getPayload();

				if (payload instanceof TileQuery) {

					TileQuery query = (TileQuery) payload;
					node.send(new Message(node, query.getNode(), new TileAvailable(node, query.getRegion())));

				} else if (payload instanceof TileRequest && false) {

					TileRequest request = (TileRequest) payload;
					node.send(new Message(message.getTo(), message.getFrom(), new TileEnvelope(
							tiles[request.getRegion().getX() / tileSize][request.getRegion().getY() / tileSize])));
				}
			}
		});
	}

	public Tile getTile(int x, int y) {

		return tiles[x / tileSize][y / tileSize];
	}

	public Node getNode() {
		return node;
	}

	public ChordNode getChordNode() {
		return chordNode;
	}
}
