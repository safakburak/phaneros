package actionsim;

import actionsim.core.Action;
import actionsim.core.Message;
import actionsim.core.Node;
import actionsim.core.NodeListener;

public class AbstractNodeListener implements NodeListener {

	@Override
	public void onConnect(Node node) {

	}

	@Override
	public void onDisconnect(Node node) {

	}

	@Override
	public void onMessage(Message message) {

	}

	@Override
	public void onStep(Action[] completedActions, float deltaTime) {

	}
}
