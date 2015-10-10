package actionsim.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Node {

	private final float NEAR_ZERO = 0.0000001f;
	
	private int nodeId;
	
	private Queue<Message> inbox = new LinkedList<Message>();
	
	private Queue<Message> outbox = new LinkedList<Message>();
	
	private Queue<Action> actions = new LinkedList<Action>();
	
	private Queue<Action> completedActions = new LinkedList<Action>();
	
	private List<Node> connections = new LinkedList<Node>();
	
	private float bandwidth = 0; // kilobytes per second
	
	private float remainingKilobytes = 0;
	
	private float cpuBudgetPerStep = 0; // milliseconds
	
	private float remainingCpuBudget = 0;
	
	
	public Node(int nodeId) {
		
		this.nodeId = nodeId;
	}
	
	final void deliverMessages(float deltaTime) {
		
		if(bandwidth > 0) {
			
			remainingKilobytes += (bandwidth / 1000.0) * deltaTime;
		}
		
		while(outbox.isEmpty() == false) {
			
			if(bandwidth <= NEAR_ZERO || outbox.peek().getSize() <= remainingKilobytes) {
				
				Message message = outbox.poll();
				
				remainingKilobytes -= message.getSize();
				
				Node nextNode = message.getNextNode();
				
				message.setNextNode(message.getReceiver());
				
				nextNode.outbox.add(message);
			}
			else {
				
				break;
			}
		}
	}
	
	final void processMessages(float deltaTime) {
		
		while(inbox.isEmpty() == false) {
			
			Message message = inbox.poll();
			
			if(message.getReceiver() == this) {
				
				MessageReceiveAction messageReceiveAction = new MessageReceiveAction(message);
				doAction(messageReceiveAction);
			}
			else {
				
				Node[] nextNodes = calculateNextNodes(message);
				
				if(nextNodes != null) {
					
					for(Node nextNode : nextNodes) {

						Message copy = message.copy();
						
						copy.setNextNode(nextNode);
						
						outbox.add(copy);
					}
				}
			}
		}
	}
	
	final void processActions(float deltaTime) {
		
		completedActions.clear();
		
		Action[] completedActionsArr;
		
		if(cpuBudgetPerStep > 0) {
			
			remainingCpuBudget += cpuBudgetPerStep;
		}

		synchronized (actions) {
		
			while(actions.isEmpty() == false) {
				
				if(cpuBudgetPerStep <= NEAR_ZERO || actions.peek().getCpuCost() <= remainingCpuBudget) {
					
					Action action = actions.poll();
					
					remainingCpuBudget -= action.getCpuCost();
					
					action.run();
					
					if(action instanceof ConnectionAction) {
						
						handleConnect((ConnectionAction) action);
					}
					else if (action instanceof DisconnectionAction) {
						
						handleDisconnect((DisconnectionAction) action);
					}
						
					completedActions.add(action);
				}
				else {
					
					break;
				}
			}
		
			completedActionsArr = new Action[completedActions.size()];
		
			completedActionsArr = completedActions.toArray(completedActionsArr);
		}
		
		process(completedActionsArr, deltaTime);
	}
	
	private void handleConnect(ConnectionAction connectionAction) {
		
		if(canConnectTo(connectionAction.getTo()) 
			&& connectionAction.getTo().canConnectFrom(this)) {
			
			connections.add(connectionAction.getTo());
			connectionAction.getTo().connections.add(this);
			connectionAction.setResult(true);
			
		}
		else {
			
			connectionAction.setResult(false);
		}
	}
	
	private void handleDisconnect(DisconnectionAction disconnectionAction) {
		
		connections.remove(disconnectionAction.getTo());
		
		disconnectionAction.getTo().connections.remove(this);
	}
	
	
	// public interface
	
	public final int getNodeId() {
		
		return nodeId;
	}
	
	public final void send(Message message) {
		
		outbox.add(message);
	}
	
	public final boolean isConnectedTo(Node node) {
		
		return connections.contains(node);
	}
	
	public final void requestConnectionTo(Node node) {

		if(connections.contains(node) == false) {
			
			ConnectionAction request = new ConnectionAction(this, node);
			
			doAction(request);
		}
	}
	
	public final void requestDisconnectionFrom(Node node) {
		
		if(connections.contains(node)) {
			
			DisconnectionAction request = new DisconnectionAction(this, node);
			
			doAction(request);
		}
	}

	public final float getBandwidth() {
		
		return bandwidth;
	}

	public final void setBandwidth(float bandwidth) {
		
		this.bandwidth = bandwidth;
	}
	
	public final float getCpuBudget() {
		
		return cpuBudgetPerStep;
	}

	public final void setCpuBudget(float cpuBudget) {
		
		this.cpuBudgetPerStep = cpuBudget;
	}
	
	public final void doAction(Action action) {
		
		synchronized (actions) {
			
			actions.add(action);
		}
	}

	
	// expected to be overridden by implementers
	
	public boolean canConnectTo(Node node) {
		
		return true;
	}
	
	public boolean canConnectFrom(Node node) {
		
		return true;
	}
	
	public boolean canDeliverTo(Node node, Message message) {
		
		return true;
	}
	
	public boolean canDeliverFrom(Node node, Message message) {
		
		return true;
	}
	
	public void process(Action[] completedActions, float deltaTime) {
		
	}
	
	protected Node[] calculateNextNodes(Message message) {
		
		return null;
	}
	
	@Override
	public String toString() {
		
		String result = new String();
		result += "Node: " + nodeId + "\n"; 
		result += "Connections: " + connections.size() + "\n"; 
		
		return result;
	}
}
