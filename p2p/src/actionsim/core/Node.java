package actionsim.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Node {

	private final float NEAR_ZERO = 0.0000001f;

	private long serial;

	private Application application;

	private List<Node> connections = new LinkedList<Node>();
	
	private Queue<Message> inbox = new LinkedList<Message>();
	
	private Queue<Message> outbox = new LinkedList<Message>();
	
	
	private Queue<Action> actions = new LinkedList<Action>();
	
	private Queue<Action> completedActions = new LinkedList<Action>();
	
	
	private float bandwidth = 0; // kilobytes per second
	
	private float remainingKilobytes = 0;
	
	private float cpuBudgetPerStep = 0; // milliseconds
	
	private float remainingCpuBudget = 0;
	
	
	protected Node(long serial) {
		
		this.serial = serial;
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

				application.onMessage(message);
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
					
					completedActions.add(action);
				}
				else {
					
					break;
				}
			}
		
			completedActionsArr = new Action[completedActions.size()];
		
			completedActionsArr = completedActions.toArray(completedActionsArr);
		}
		
		application.onStep(completedActionsArr, deltaTime);
	}
	
	private boolean canConnectTo(Node node) {
		
		if(application == null) {
			
			return true;
		}
		else {
			
			return application.canConnectTo(node);
		}
	}
	
	private boolean canConnectFrom(Node node) {
		
		if(application == null) {
			
			return true;
		}
		else {
			
			return application.canConnectFrom(node);
		}
	}
	
	// public interface

	public Application getApplication() {
		
		return application;
	}
	
	public void setApplication(Application application) {
		
		this.application = application;
	}
	
	public final long getSerial() {
		
		return serial;
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
				application.onConnect(node);
				
				node.connect(this);
			}
		}
	}
	
	public final void disconnect(Node node) {
		
		if(connections.contains(node)) {
			
			connections.remove(node);
			application.onDisconnect(node);
			
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
		
		synchronized (actions) {
			
			actions.add(action);
		}
	}
}
