package p2p.von;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import actionsim.AbstractNodeListener;
import actionsim.core.Message;
import actionsim.core.Node;
import p2p.common.AbstractAgent;
import p2p.common.RandomWalker;
import p2p.common.messages.PatchRequest;
import p2p.geometry.primitives.Point;
import p2p.map.Map;
import p2p.timer.TimedAction;
import p2p.visibility.Visibility;
import p2p.von.messages.ConnectSuggestion;
import p2p.von.messages.Update;

public class VonAgent extends AbstractAgent<VonAgent> {

	private RandomWalker walker;

	private Node mapServer;

	private VonGraph graph = new VonGraph(this);

	private Set<VonAgent> enclosingAgents = new HashSet<VonAgent>();
	private Set<VonAgent> aoiAgents = new HashSet<VonAgent>();

	public VonAgent(Node node, Visibility visibility, int cacheSize, Node mapServer, int worldWidth, int worldHeight,
			Map worldMap) {

		super(node, visibility, cacheSize, worldMap);

		this.mapServer = mapServer;

		walker = new RandomWalker(this, worldWidth, worldHeight);
	}

	@Override
	public void start() {

		node.addNodeListener(new AbstractNodeListener() {

			@Override
			public void onMessage(Message message) {

				Object payload = message.getPayload();

				if (payload instanceof Map) {

					cache.addPatch((Map) payload);

				} else if (payload instanceof Update) {

					Update update = (Update) payload;

					if (update.getAgent() != VonAgent.this) {

						knownAgents.put(update.getAgent(), new Point(update.getX(), update.getY()));

						boolean isBoundary = isBoundaryFor(update.getAgent());

						graph.update(knownAgents);

						enclosingAgents = graph.getEnclosing();

						int range = visibility.getMaxRange();

						if (distSquare(x, y, update.getX(), update.getY()) > range * range) {

							aoiAgents.remove(update.getAgent());

							if (enclosingAgents.contains(update.getAgent()) == false) {

								knownAgents.remove(update.getAgent());

								node.send(new Message(node, update.getAgent().node, new Update(VonAgent.this, x, y)));
							}

						} else {

							aoiAgents.add(update.getAgent());
						}

						if (isBoundary) {

							ArrayList<VonAgent> suggestionList = new ArrayList<VonAgent>();

							Set<VonAgent> enclosingOfUpdater = graph.getEnclosing(update.getAgent());

							for (VonAgent agent : enclosingAgents) {

								Point p = knownAgents.get(agent);

								if (p != null) {

									if (distSquare(update.getX(), update.getY(), p.getX(), p.getY()) <= range * range
											|| enclosingOfUpdater.contains(agent)) {

										suggestionList.add(agent);
									}
								}
							}

							if (suggestionList.size() > 0) {

								node.send(new Message(node, update.getAgent().node,
										new ConnectSuggestion(suggestionList)));
							}
						}

						// System.out.println(node.getId() + " kwn : " +
						// knownAgents.size());
						// System.out.println(node.getId() + " enc : " +
						// enclosingAgents.size());
						// System.out.println(node.getId() + " aoi : " +
						// aoiAgents.size());
					}

				} else if (payload instanceof ConnectSuggestion) {

					ConnectSuggestion suggestion = (ConnectSuggestion) payload;

					int range = visibility.getMaxRange();

					for (VonAgent agent : suggestion.getAgents()) {

						if (agent != VonAgent.this) {

							knownAgents.put(agent, new Point(agent.getX(), agent.getY()));

							graph.update(knownAgents);

							enclosingAgents = graph.getEnclosing();

							if (distSquare(x, y, agent.getX(), agent.getY()) <= range * range) {

								aoiAgents.add(agent);
							}
						}
					}
				}
			}
		});

		timer.repeat(new TimedAction() {

			@Override
			public void act(float time) {

				walker.walk();
			}
		}, 500);
	}

	@Override
	public void onCacheMissAt(int x, int y) {

		mapServer.send(new Message(node, mapServer, new PatchRequest(x, y)));
	}

	@Override
	public void onPositionChange() {

		for (VonAgent agent : aoiAgents) {

			node.send(new Message(node, agent.node, new Update(this, x, y)));
		}

		for (VonAgent agent : enclosingAgents) {

			if (aoiAgents.contains(agent) == false) {

				node.send(new Message(node, agent.node, new Update(this, x, y)));
			}
		}
	}

	public void init(ArrayList<AbstractAgent> agents, VonGraph graph) {

		int range = visibility.getMaxRange();

		for (AbstractAgent agent : agents) {

			if (distSquare(x, y, agent.getX(), agent.getY()) <= range * range) {

				aoiAgents.add((VonAgent) agent);
			}
		}

		enclosingAgents = graph.getEnclosing(this);
	}

	private double distSquare(double x1, double y1, double x2, double y2) {

		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}

	private boolean isBoundaryFor(VonAgent agent) {

		int range = visibility.getMaxRange();

		for (VonAgent enclosing : enclosingAgents) {

			if (distSquare(enclosing.x, enclosing.y, agent.x, agent.y) > range * range) {

				return true;
			}
		}

		return false;
	}
}
