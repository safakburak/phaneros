package p2p.von;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import actionsim.AbstractNodeListener;
import actionsim.core.Message;
import actionsim.core.Node;
import p2p.common.AbstractAgent;
import p2p.common.MapServer;
import p2p.common.RandomWalker;
import p2p.common.messages.TileEnvelope;
import p2p.stats.Stats;
import p2p.timer.TimedAction;
import p2p.visibility.Visibility;
import p2p.visibility.VisibilityCell;
import p2p.von.messages.ConnectSuggestion;
import p2p.von.messages.Update;

public class VonAgent extends AbstractAgent<VonAgent> {

	private RandomWalker walker;

	private VonGraph graph = new VonGraph(this);

	private Set<VonAgent> enclosingAgents = new HashSet<VonAgent>();
	private Set<VonAgent> aoiAgents = new HashSet<VonAgent>();

	private int extendedrange;

	public VonAgent(Node node, Visibility visibility, int cacheSize, int worldWidth, int worldHeight,
			MapServer server) {

		super(node, visibility, cacheSize, server);

		walker = new RandomWalker(this, worldWidth, worldHeight);

		extendedrange = visibility.getMaxRange() + visibility.getCellSize();
	}

	@Override
	public void start() {

		node.addNodeListener(new AbstractNodeListener() {

			@Override
			public void onMessage(Message message) {

				Object payload = message.getPayload();

				if (payload instanceof TileEnvelope) {

					cache.addTile(((TileEnvelope) payload).getTile());

				} else if (payload instanceof Update) {

					Update update = (Update) payload;

					if (update.getAgent() != VonAgent.this) {

						knownAgents.put(update.getAgent(), new Point(update.getX(), update.getY()));

						boolean isBoundary = isBoundaryFor(update.getAgent());

						graph.update(knownAgents);

						enclosingAgents = graph.getEnclosing();

						if (distSquare(x, y, update.getX(), update.getY()) > extendedrange * extendedrange) {

							aoiAgents.remove(update.getAgent());

							if (enclosingAgents.contains(update.getAgent()) == false) {

								knownAgents.remove(update.getAgent());

								node.send(new Message(node, update.getAgent().node, new Update(VonAgent.this, x, y)));

								Stats.updatesSend.sample();
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

									if (distSquare(update.getX(), update.getY(), p.getX(), p.getY()) <= extendedrange
											* extendedrange || enclosingOfUpdater.contains(agent)) {

										suggestionList.add(agent);
									}
								}
							}

							if (suggestionList.size() > 0) {

								node.send(new Message(node, update.getAgent().node,
										new ConnectSuggestion(suggestionList)));

								Stats.suggestions.sample(suggestionList.size());
							}
						}
					}

				} else if (payload instanceof ConnectSuggestion) {

					ConnectSuggestion suggestion = (ConnectSuggestion) payload;

					for (VonAgent agent : suggestion.getAgents()) {

						if (agent != VonAgent.this) {

							knownAgents.put(agent, new Point(agent.getX(), agent.getY()));

							graph.update(knownAgents);

							enclosingAgents = graph.getEnclosing();

							if (distSquare(x, y, agent.getX(), agent.getY()) <= extendedrange * extendedrange) {

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
	public void onPositionChange() {

		int updatesSend = 0;

		for (VonAgent agent : aoiAgents) {

			node.send(new Message(node, agent.node, new Update(this, x, y)));

			Stats.updatesSend.sample();

			updatesSend++;
		}

		for (VonAgent agent : enclosingAgents) {

			if (aoiAgents.contains(agent) == false) {

				node.send(new Message(node, agent.node, new Update(this, x, y)));

				Stats.updatesSend.sample();

				updatesSend++;
			}
		}

		Stats.aoiNeighbors.sample(aoiAgents.size());
		Stats.simultaneousConnections.sample(updatesSend);

		VisibilityCell newCell = visibility.getCellForPos(x, y);
		VisibilityCell oldCell = currentCell;

		if (oldCell == null || oldCell != newCell) {

			currentCell = newCell;

			for (VisibilityCell cell : newCell.getPvs()) {

				cache.getTile(cell.getRegion());
			}

			Stats.pvsSize.sample(newCell.getPvs().size());

			if (oldCell != null) {

				Stats.cellStay.sampleTime(VonAgent.this);
			}

			Stats.cellStay.markTime(VonAgent.this);

			Stats.cellChange.sample();
		}
	}

	@SuppressWarnings("rawtypes")
	public void init(ArrayList<AbstractAgent> agents, VonGraph graph) {

		for (AbstractAgent agent : agents) {

			if (distSquare(x, y, agent.getX(), agent.getY()) <= extendedrange * extendedrange) {

				aoiAgents.add((VonAgent) agent);
			}
		}

		enclosingAgents = graph.getEnclosing(this);
	}

	private double distSquare(double x1, double y1, double x2, double y2) {

		return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
	}

	private boolean isBoundaryFor(VonAgent agent) {

		for (VonAgent enclosing : enclosingAgents) {

			if (distSquare(enclosing.x, enclosing.y, agent.x, agent.y) > extendedrange * extendedrange) {

				return true;
			}
		}

		return false;
	}

	@Override
	public void onUrgentTileNeed(int x, int y) {

	}
}
