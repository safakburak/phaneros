package actionsim.core;

public class ConnectionAction extends Action {

	private Node from;
	private Node to;
	private boolean result;
	
	public ConnectionAction(Node from, Node to) {
		
		super(SimulationConfiguration.connectionCost);
		
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

	public boolean isResult() {
		return result;
	}

	public void setResult(boolean result) {
		this.result = result;
	}
}
