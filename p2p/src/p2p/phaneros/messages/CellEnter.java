package p2p.phaneros.messages;

import actionsim.core.Node;
import p2p.visibility.VisibilityCell;

public class CellEnter {

	private int x;
	private int y;
	private VisibilityCell cell;
	private Node node;
	
	public CellEnter(VisibilityCell cell, Node node, int x, int y) {
		
		this.cell = cell;
		this.node = node;
		this.x = x;
		this.y = y;
	}
	
	public VisibilityCell getCell() {
		return cell;
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
