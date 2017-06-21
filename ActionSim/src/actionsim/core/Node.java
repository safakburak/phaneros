package actionsim.core;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Node {

	private String id;

	private ArrayList<Policy> policies = new ArrayList<>();

	private ArrayList<NodeListener> listeners = new ArrayList<>();

	private List<Node> connections = new LinkedList<Node>();

	private Queue<Message> inbox = new LinkedList<Message>();

	private LinkedList<Message> outbox = new LinkedList<Message>();

	private Queue<Action> actions = new LinkedList<Action>();

	private Queue<Action> completedActions = new LinkedList<Action>();

	private Float uploadBandwidth = null; // kilobytes per second

	private float remainingUploadKilobytes = 0;

	private Float downloadBandwidth = null; // kilobytes per second

	private float remainingDownloadKilobytes = 0;

	private Float cpuBudget = null; // milliseconds

	private float remainingCpuBudget = 0;

	Node(String id) {

		this.id = id;
	}

	final void refreshBudgets(float deltaTime) {

		if (uploadBandwidth != null && remainingUploadKilobytes <= 0) {

			remainingUploadKilobytes += (uploadBandwidth / 1000.0) * deltaTime;
		}

		if (downloadBandwidth != null && remainingDownloadKilobytes <= 0) {

			remainingDownloadKilobytes += (downloadBandwidth / 1000.0) * deltaTime;
		}
	}

	final void deliverMessages(float deltaTime) {

		for (int i = 0; i < outbox.size(); i++) {

			Message message = outbox.get(i);

			message.retries++;

			if (message.retries == 10) {

				outbox.remove(message);
				i--;

			} else {

				if (remainingUploadKilobytes > 0 || uploadBandwidth == null) {

					Node receiver = message.getTo();

					if (receiver.remainingDownloadKilobytes > 0 || receiver.downloadBandwidth == null) {

						remainingUploadKilobytes -= message.getSize();
						receiver.remainingDownloadKilobytes -= message.getSize();

						outbox.remove(i);
						receiver.inbox.add(message);

						i--;

						message.retries = 0;
					}

				} else {

					break;
				}
			}
		}
	}

	final void processMessages(float deltaTime) {

		while (inbox.isEmpty() == false) {

			Message message = inbox.poll();

			if (message.getTo() == this) {

				for (NodeListener listener : listeners) {

					listener.onMessage(message);
				}
			}
		}
	}

	final void processActions(float deltaTime) {

		completedActions.clear();

		Action[] completedActionsArr;

		if (cpuBudget != null) {

			remainingCpuBudget += cpuBudget;
		}

		while (actions.isEmpty() == false) {

			if (remainingCpuBudget >= 0 || cpuBudget == null) {

				Action action = actions.poll();

				remainingCpuBudget -= action.getCpuCost();

				action.run();

				completedActions.add(action);

			} else {

				break;
			}
		}

		completedActionsArr = new Action[completedActions.size()];

		completedActionsArr = completedActions.toArray(completedActionsArr);

		for (NodeListener listener : listeners) {

			listener.onStep(completedActionsArr, deltaTime);
		}
	}

	private boolean canConnectTo(Node node) {

		boolean result = true;

		for (Policy policy : policies) {

			if (policy.canConnectTo(node) == false) {

				result = false;
			}
		}

		return result;
	}

	private boolean canConnectFrom(Node node) {

		boolean result = true;

		for (Policy policy : policies) {

			if (policy.canConnectFrom(node) == false) {

				result = false;
			}
		}

		return result;
	}

	// public interface

	public void addPolicy(Policy policy) {

		if (policies.contains(policy) == false) {

			policies.add(policy);
		}
	}

	public void addNodeListener(NodeListener listener) {

		if (listeners.contains(listener) == false) {

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

		if (connections.contains(node) == false) {

			if (canConnectTo(node) && node.canConnectFrom(this)) {

				connections.add(node);

				for (NodeListener listener : listeners) {

					listener.onConnect(node);
				}

				node.connect(this);
			}
		}
	}

	public final void disconnect(Node node) {

		if (connections.contains(node)) {

			connections.remove(node);

			for (NodeListener listener : listeners) {

				listener.onDisconnect(node);
			}

			node.disconnect(this);
		}
	}

	public final void setUploadBandwidth(Float bandwidth) {

		this.uploadBandwidth = bandwidth;
	}

	public void setDownloadBandwidth(Float downloadBandwidth) {

		this.downloadBandwidth = downloadBandwidth;
	}

	public final void setCpuBudget(Float cpuBudget) {

		this.cpuBudget = cpuBudget;
	}

	public final void act(Action action) {

		actions.add(action);
	}

	@Override
	public String toString() {

		return id;
	}
}
