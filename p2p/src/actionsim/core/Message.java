package actionsim.core;

import java.util.ArrayList;

public class Message {

	private Node nextNode;
	
	private Node sender;
	
	private Node receiver;
	
	private float size; // kilobytes
	
	private ArrayList<Node> routeHistory = new ArrayList<Node>();
	
	public Message(Node sender, Node receiver, float size) {
		
		this.sender = sender;
		
		this.receiver = receiver;
		
		this.nextNode = receiver;
		
		this.size = size;
	}
	
	public float getSize() {
		
		return size;
	}
	
	public Node getSender() {
		
		return sender;
	}
	
	public Node getReceiver() {
		
		return receiver;
	}
	
	Node getNextNode() {
		
		return nextNode;
	}
	
	void setNextNode(Node node) {
		
		if(node != receiver) {
			
			routeHistory.add(receiver);
		}
		
		nextNode = node;
	}
	
	public Message copy() {
		
		Message message = new Message(sender, receiver, size);
		
		return message;
	}
}
