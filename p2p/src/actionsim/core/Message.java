package actionsim.core;

public class Message {

	private Node sender;
	
	private Node receiver;
	
	
	public Message(Node sender, Node receiver) {
		
		this.sender = sender;
		
		this.receiver = receiver;
	}
	
	public Node getSender() {
		
		return sender;
	}
	
	public Node getReceiver() {
		
		return receiver;
	}
	
	public float getSize() {
		
		return 0;
	}
}
