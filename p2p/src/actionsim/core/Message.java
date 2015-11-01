package actionsim.core;

public class Message {

	private Node from;
	
	private Node to;
	
	
	public Message(Node from, Node to) {
		
		this.from = from;
		
		this.to = to;
	}
	
	public Node getFrom() {
		
		return from;
	}
	
	public Node getTo() {
		
		return to;
	}
	
	public float getSize() {
		
		return 0;
	}
}
