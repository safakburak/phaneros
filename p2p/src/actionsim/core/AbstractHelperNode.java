package actionsim.core;

public abstract class AbstractHelperNode extends Node {

	public AbstractHelperNode(long serial) {
		
		super(serial);
	}
	
	@Override
	public void process(Action[] completedActions, float deltaTime) {

		processActions(completedActions);
		
		update(deltaTime);
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

				Message message = ((MessageReceiveAction)action).getMessage();
				
				onMessage(message);
			}
			else {
				
				onActionComplete(action);
			}
		}
	}
	
	protected void onConnect(Node node) {};
	
	protected void onDisconnect(Node node) {};
	
	protected void onMessage(Object message) {};
	
	protected void onActionComplete(Action action) {};
	
	protected void update(float deltaTime) {};
	
}
