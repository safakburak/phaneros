package actionsim.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Node {

	private final float NEAR_ZERO = 0.0000001f;

	private String id;

	private ArrayList<Policy> policies = new ArrayList<>();
	
	private ArrayList<NodeListener> listeners = new ArrayList<>();
	
	private List<Node> connections = new LinkedList<Node>();
	
	private Queue<Message> inbox = new LinkedList<Message>();
	
	private Queue<Message> outbox = new LinkedList<Message>();
	
	
	private Queue<Action> actions = new LinkedList<Action>();
	
	private Queue<Action> completedActions = new LinkedList<Action>();
	
	
	private float bandwidth; // kilobytes per second
	
	private float remainingKilobytes = 0;
	
	private float cpuBudgetPerStep; // milliseconds
	
	private float remainingCpuBudget = 0;
	
	
	Node(String id, Configuration configuration) {
		
		this.id = id;
		this.bandwidth = configuration.getDefaultBandwidth();
		this.cpuBudgetPerStep = configuration.getDefaultCpuBudget();
	}
	
	final void deliverMessages(float deltaTime) {
		
		if(bandwidth > 0) {
			
			remainingKilobytes += (bandwidth / 1000.0) * deltaTime;
		}
		
		while(outbox.isEmpty() == false) {
			
			if(bandwidth <= NEAR_ZERO || outbox.peek().getSize() <= remainingKilobytes) {
				
				Message message = outbox.poll();
				
				remainingKilobytes -= message.getSize();
				
				Node receiver = message.getTo();
				
				receiver.inbox.add(message);
			}
			else {
				
				break;
			}
		}
	}
	
	final void processMessages(float deltaTime) {
		
		while(inbox.isEmpty() == false) {
			
			Message message = inbox.poll();
			
			if(message.getTo() == this) {

				for(NodeListener listener : listeners) {
					
					listener.onMessage(message);
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
		
		while(actions.isEmpty() == false) {
			
			if(cpuBudgetPerStep <= NEAR_ZERO || actions.peek().getCpuCost() <= remainingCpuBudget) {
				
				Action action = actions.poll();
				
				remainingCpuBudget -= action.getCpuCost();
				
				action.run();
				
				completedActions.add(action);
			}
			else {
				
				break;
			}
		}
	
		completedActionsArr = new Action[completedActions.size()];
	
		completedActionsArr = completedActions.toArray(completedActionsArr);
		
		for(NodeListener listener : listeners) {
			
			listener.onStep(completedActionsArr, deltaTime);
		}
	}
	
	private boolean canConnectTo(Node node) {
		
		boolean result = true;
		
		for(Policy policy : policies) {
			
			if(policy.canConnectTo(node) == false) {
				
				result = false;
			}
		}
		
		return result;
	}
	
	private boolean canConnectFrom(Node node) {
		
		boolean result = true;
		
		for(Policy policy : policies) {
			
			if(policy.canConnectFrom(node) == false) {
				
				result = false;
			}
		}
		
		return result;
	}
	
	// public interface

	public void addPolicy(Policy policy) {

		if(policies.contains(policy) == false) {
			
			policies.add(policy);
		}
	}
	
	public void addNodeListener(NodeListener listener) {

		if(listeners.contains(listener) == false) {
			
			listeners.add(listener);
		}
	}
	
	public final String getId() {
		
		return id;
	}
	
	public final void send(Message message) {
		
		outbox.add(message);
	}
	
	public final boolean isConnectedTo(Node node) {
		
		return connections.contains(node);
	}
	
	public final void connect(Node node) {

		if(connections.contains(node) == false) {
			
			if(canConnectTo(node) && node.canConnectFrom(this)) {
				
				connections.add(node);
				
				for(NodeListener listener : listeners) {
					
					listener.onConnect(node);
				}
				
				node.connect(this);
			}
		}
	}
	
	public final void disconnect(Node node) {
		
		if(connections.contains(node)) {
			
			connections.remove(node);
			
			for(NodeListener listener : listeners) {
				
				listener.onDisconnect(node);
			}
			
			node.disconnect(this);
		}
	}

	public final void setBandwidth(float bandwidth) {
		
		this.bandwidth = bandwidth;
	}
	
	public final void setCpuBudget(float cpuBudget) {
		
		this.cpuBudgetPerStep = cpuBudget;
	}
	
	public final void act(Action action) {
		
		actions.add(action);
	}
	
	@Override
	public String toString() {
		
		return id;
	}
}
