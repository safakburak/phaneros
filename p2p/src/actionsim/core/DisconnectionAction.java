package actionsim.core;

public class DisconnectionAction extends Action {

	private Node from;
	private Node to;
	
	public DisconnectionAction(Node from, Node to) {
		
		super(SimulationConfiguration.disconnectionCost);
		
		this.from = from;
		this.to = to;
	}
	
	@Override
	protected void run() {
		
	}

	public Node getFrom() {
		return from;
	}

	public Node getTo() {
		return to;
	}
	
}
