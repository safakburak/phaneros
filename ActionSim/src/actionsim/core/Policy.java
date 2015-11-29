package actionsim.core;

public interface Application {

	public boolean canConnectTo(Node node);
	
	public boolean canConnectFrom(Node node);
		
//	public boolean canDeliverTo(Node node, Message message);
//
//	public boolean canDeliverFrom(Node node, Message message);
	
	public void onConnect(Node node);
	
	public void onDisconnect(Node node);
	
	public void onMessage(Message message);
	
	public void onStep(Action[] completedActions, float deltaTime);
	
}
