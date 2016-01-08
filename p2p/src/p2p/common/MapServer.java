package p2p.common;

import actionsim.AbstractNodeListener;
import actionsim.chord.ChordNode;
import actionsim.core.Message;
import actionsim.core.Node;
import actionsim.scribe.ScribeNode;
import p2p.common.messages.TileRequest;
import p2p.map.Atlas;
import p2p.map.Tile;

public class MapServer {

	private Node node;
	private ScribeNode scribeNode;
	private ChordNode chordNode;
	private Tile[][] tiles;

	public MapServer(Node node, Atlas atlas, int tileSize) {

		this.node = node;
		this.scribeNode = new ScribeNode(node);
		this.chordNode = scribeNode.getChordNode();

		initialize(atlas, tileSize);
	}

	private void initialize(Atlas atlas, final int tileSize) {

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

				if (payload instanceof TileRequest) {

					TileRequest request = (TileRequest) payload;
					node.send(new Message(message.getTo(), message.getFrom(),
							tiles[request.getX() / tileSize][request.getY() / tileSize]));
				}
			}
		});
	}

	public Node getNode() {
		return node;
	}

	public ChordNode getChordNode() {
		return chordNode;
	}
}
