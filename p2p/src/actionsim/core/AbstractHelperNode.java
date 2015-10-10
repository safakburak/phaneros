package actionsim.core;

public abstract class AbstractHelperNode extends Node {

	public AbstractHelperNode(int nodeId) {
		
		super(nodeId);
	}
	
	@Override
	protected Node[] calculateNextNodes(Message message) {
		
		return super.calculateNextNodes(message);
	}

	@Override
	public void process(Action[] completedActions, float deltaTime) {

		processActions(completedActions);
	}
	
	private void processActions(Action[] actions) {
		
		for(Action action : actions) {

			if(action instanceof ConnectionAction) {
				
				onDisconnect(((ConnectionAction)action).getTo());
			}
			else if(action instanceof DisconnectionAction) {
				
				onDisconnect(((DisconnectionAction)action).getTo());
			}
			else if(action instanceof MessageReceiveAction) {

				onMessage(((MessageReceiveAction)action).getMessage());
			}
			else {
				
				onActionComplete(action);
			}
		}
	}
	
	protected abstract void onConnect(Node node);
	
	protected abstract void onDisconnect(Node node);
	
	protected abstract void onMessage(Message message);
	
	protected abstract void onActionComplete(Action action);
	
	protected abstract void update(float deltaTime);
	
}
