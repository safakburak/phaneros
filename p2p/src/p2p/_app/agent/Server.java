package p2p._app.agent;

import actionsim.AbstractNodeListener;
import actionsim.chord.ChordNode;
import actionsim.core.Message;
import actionsim.core.Node;
import actionsim.scribe.ScribeNode;
import p2p._app.map.Map;

public class Server {

	private Node node;
	private ScribeNode scribeNode;
	private ChordNode chordNode;
	private Map[][] patches;
	
	public Server(Node node, Map map, int patchSize) {
		
		this.node = node;
		this.scribeNode = new ScribeNode(node);
		this.chordNode = scribeNode.getChordNode();

		initialize(map, patchSize);
	}
	
	private void initialize(Map map, final int patchSize) {

		int colNum = map.getWidth() / patchSize;
		int rowNum = map.getHeight() / patchSize;
		
		patches = new Map[colNum][rowNum];
		
		for(int col = 0; col < colNum; col++) {
			for(int row = 0; row < rowNum; row++) {
				
				patches[col][row] = map.getMapPart(col * patchSize, row * patchSize, patchSize, patchSize);
			}	
		}
		
		node.addNodeListener(new AbstractNodeListener() {
			
			@Override
			public void onMessage(Message message) {
				
				Object payload = message.getPayload();
				
				if(payload instanceof PatchRequest) {
					
					PatchRequest request = (PatchRequest) payload;
					node.send(new Message(message.getTo(), message.getFrom(), patches[request.getX() / patchSize][request.getY() / patchSize]));
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
