package actionsim.core;

public interface NodeContoller {

	public boolean canConnectTo(Node node);
	
	public boolean canConnectFrom(Node node);
	
	public boolean canDeliverTo(Node node, Message message);
	
	public boolean canDeliverFrom(Node node, Message message);
	
	public void process(Action[] completedActions, float deltaTime);
}
