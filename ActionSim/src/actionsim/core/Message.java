package actionsim.core;

public class Message {

	private Node from;
	
	private Node to;
	
	private Object payload;
	
	public Message(Node from, Node to, Object payload) {
		
		this.from = from;
		this.to = to;
		this.payload = payload;
	}
	
	public Node getFrom() {
		
		return from;
	}
	
	public Node getTo() {
		
		return to;
	}
	
	public Object getPayload() {
		
		return payload;
	}
	
	public float getSize() {
		
		return 0;
	}
	
	@Override
	public String toString() {
		
		return "message from " + from + " to " + to;
	}
}
