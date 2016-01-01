package p2p.phaneros.messages;

import actionsim.core.Node;
import p2p.visibility.VisibilityCell;

public class CellExit {

	private VisibilityCell cell;
	private Node node;
	
	public CellExit(VisibilityCell cell, Node node) {
		
		this.cell = cell;
		this.node = node;
	}
	
	public VisibilityCell getCell() {
		return cell;
	}
	
	public Node getNode() {
		return node;
	}
}
