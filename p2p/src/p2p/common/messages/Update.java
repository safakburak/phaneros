package p2p.common.messages;

import actionsim.core.Node;

public class Update {

	private Node node;
	private int x;
	private int y;
	
	public Update(Node node, int x, int y) {
		
		this.node = node;
		this.x = x;
		this.y = y;
	}

	public Node getNode() {
		
		return node;
	}
	
	public int getX() {
		
		return x;
	}
	
	public int getY() {
		
		return y;
	}
}
