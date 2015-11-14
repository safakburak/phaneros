package actionsim.core;

public class DefaultApplication implements Application {

	@Override
	public boolean canConnectTo(Node node) {
		
		return true;
	}

	@Override
	public boolean canConnectFrom(Node node) {
		
		return true;
	}

	@Override
	public void onConnect(Node node) {
		
	}

	@Override
	public void onDisconnect(Node node) {
		
	}

	@Override
	public void onMessage(Message message) {
		
//		System.out.println(message);
	}

	@Override
	public void onStep(Action[] completedActions, float deltaTime) {
		
	}
}
