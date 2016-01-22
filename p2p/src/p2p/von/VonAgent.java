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
import p2p.common.messages.TileAvailable;
import p2p.common.messages.TileEnvelope;
import p2p.common.messages.TileQuery;
import p2p.common.messages.TileRequest;
import p2p.map.Region;
import p2p.map.Tile;
import p2p.phaneros.PvsCheckAction;
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

	private Node mapServer;

	public VonAgent(Node node, Visibility visibility, int cacheSize, Node mapServer, int worldWidth, int worldHeight,
			MapServer server) {

		super(node, visibility, cacheSize, server);

		this.mapServer = mapServer;

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

					TileEnvelope envelope = (TileEnvelope) payload;

					if (envelope.getTile() == null) {

						node.send(new Message(node, mapServer, new TileRequest(node, envelope.getRegion())));

					} else {

						cache.addTile(((TileEnvelope) payload).getTile());
					}

					Stats.fetchDelay.sampleTime(VonAgent.this);

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
				} else if (payload instanceof TileQuery) {

					TileQuery query = (TileQuery) payload;

					if (cache.getTile(query.getRegion()) != null) {

						node.send(new Message(node, query.getNode(), new TileAvailable(node, query.getRegion())));
					}
				} else if (payload instanceof TileAvailable) {

					TileAvailable available = (TileAvailable) payload;

					if (cache.contains(available.getRegion()) == false) {

						node.send(new Message(node, available.getNode(), new TileRequest(node, available.getRegion())));
					}

				} else if (payload instanceof TileRequest) {

					TileRequest request = (TileRequest) payload;

					Tile tile = cache.getTile(request.getRegion());

					node.send(new Message(node, request.getNode(), new TileEnvelope(tile, request.getRegion())));

					Stats.tilesFromAgents.sample();
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

		emitUpdates();

		VisibilityCell newCell = visibility.getCellForPos(x, y);
		VisibilityCell oldCell = currentCell;

		if (oldCell == null || oldCell != newCell) {

			currentCell = newCell;

			requestPvs(newCell, false);

			timer.delay(new PvsCheckAction(this, newCell), 1000);

			Stats.pvsSize.sample(newCell.getPvs().size());

			if (oldCell != null) {

				Stats.cellStay.sampleTime(VonAgent.this);
			}

			Stats.cellStay.markTime(VonAgent.this);

			Stats.cellChange.sample();
		}

		Stats.aoiNeighbors.sample(aoiAgents.size());
	}

	private void emitUpdates() {

		int connectionCount = 0;

		for (VonAgent agent : aoiAgents) {

			node.send(new Message(node, agent.node, new Update(this, x, y)));

			Stats.updatesSend.sample();
			connectionCount++;
		}

		for (VonAgent agent : enclosingAgents) {

			if (aoiAgents.contains(agent) == false) {

				node.send(new Message(node, agent.node, new Update(this, x, y)));

				Stats.updatesSend.sample();
				connectionCount++;
			}
		}

		Stats.simultaneousConnections.sample(connectionCount);
	}

	@Override
	public void requestPvs(VisibilityCell visibilityCell, boolean fromServer) {

		if (visibilityCell == currentCell) {

			int missingCount = 0;

			for (VisibilityCell cell : visibilityCell.getPvs()) {

				if (cache.getTile(cell.getRegion()) == null) {

					missingCount++;

					if (fromServer) {

						node.send(new Message(node, mapServer, new TileRequest(node, cell.getRegion())));

					} else {

						for (VonAgent agent : aoiAgents) {

							node.send(new Message(node, agent.node, new TileRequest(node, cell.getRegion())));
						}

						for (VonAgent agent : enclosingAgents) {

							if (aoiAgents.contains(agent) == false) {

								node.send(new Message(node, agent.node, new TileRequest(node, cell.getRegion())));
							}
						}

						Stats.fetchDelay.markTime(VonAgent.this);
					}
				}
			}

			if (fromServer) {

				Stats.tilesMissingAfterSecond.sample(missingCount);

			} else {

				Stats.deltaPvs.sample(missingCount);
			}
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

		int cellSize = visibility.getCellSize();
		int col = x / cellSize * cellSize;
		int row = y / cellSize * cellSize;

		Region region = new Region(col, row, visibility.getCellSize());

		node.send(new Message(node, mapServer, new TileRequest(node, region)));
	}
}
