package actionsim.core;

public interface NodeListener {

	public void onConnect(Node node);
	
	public void onDisconnect(Node node);
	
	public void onMessage(Message message);
	
	public void onStep(Action[] completedActions, float deltaTime);
}
