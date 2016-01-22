package p2p.phaneros;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import actionsim.AbstractNodeListener;
import actionsim.chord.ChordId;
import actionsim.chord.ChordNode;
import actionsim.core.Message;
import actionsim.core.Node;
import actionsim.scribe.ScribeListener;
import actionsim.scribe.ScribeNode;
import p2p.common.AbstractAgent;
import p2p.common.MapServer;
import p2p.common.RandomWalker;
import p2p.common.messages.TileAvailable;
import p2p.common.messages.TileEnvelope;
import p2p.common.messages.TileQuery;
import p2p.common.messages.TileRequest;
import p2p.map.Region;
import p2p.map.Tile;
import p2p.phaneros.messages.CellEnter;
import p2p.phaneros.messages.CellExit;
import p2p.phaneros.messages.Update;
import p2p.stats.Stats;
import p2p.timer.TimedAction;
import p2p.visibility.Visibility;
import p2p.visibility.VisibilityCell;

public class PhanerosAgent extends AbstractAgent<PhanerosAgent> {

	private ScribeNode scribeNode;
	private ChordNode chordNode;
	private Node mapServer;
	private RandomWalker walker;

	private Multimap<VisibilityCell, PhanerosAgent> connections = HashMultimap.create();

	private List<VisibilityCell> subscriptions = Collections.synchronizedList(new ArrayList<VisibilityCell>());

	public PhanerosAgent(Node node, Visibility visibility, int cacheSize, Node mapServer, int worldWidth,
			int worldHeight, MapServer server) {

		super(node, visibility, cacheSize, server);

		this.scribeNode = new ScribeNode(node);
		this.chordNode = scribeNode.getChordNode();
		this.mapServer = mapServer;

		walker = new RandomWalker(this, worldWidth, worldHeight);
	}

	public void start() {

		timer.repeat(new TimedAction() {

			@Override
			public void act(float time) {

				walker.walk();
			}
		}, 500, (float) (Math.random() * 500));

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

					Stats.fetchDelay.sampleTime(PhanerosAgent.this);

				} else if (payload instanceof Update) {

					Update update = (Update) payload;

					if (update.getAgent() != PhanerosAgent.this) {

						knownAgents.put(update.getAgent(), new Point(update.getX(), update.getY()));
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

		scribeNode.addScribeListener(new ScribeListener() {

			@Override
			public void onScribeMessage(ChordId topic, Object message, int hopCount) {

				if (message instanceof CellEnter) {

					CellEnter cellEnter = (CellEnter) message;

					if (currentCell.getPvs().contains(cellEnter.getCell())) {

						connections.put(cellEnter.getCell(), cellEnter.getAgent());
						knownAgents.put(cellEnter.getAgent(), new Point(cellEnter.getX(), cellEnter.getY()));
						node.send(new Message(node, cellEnter.getAgent().node,
								new CellEnter(currentCell, PhanerosAgent.this, x, y)));
					}

				} else if (message instanceof CellExit) {

					CellExit cellExit = (CellExit) message;

					connections.remove(cellExit.getCell(), cellExit.getAgent());
					knownAgents.remove(cellExit.getAgent());

				} else if (message instanceof TileQuery) {

					TileQuery query = (TileQuery) message;

					if (cache.getTile(query.getRegion()) != null) {

						node.send(new Message(node, query.getNode(), new TileAvailable(node, query.getRegion())));
					}

					Stats.queryHops.sample(hopCount);
				}
			}
		});

		onPositionChange();
	}

	public ChordNode getChordNode() {
		return chordNode;
	}

	@Override
	public void onPositionChange() {

		emitUpdates();

		VisibilityCell newCell = visibility.getCellForPos(x, y);
		VisibilityCell oldCell = currentCell;

		if (oldCell == null || oldCell != newCell) {

			currentCell = newCell;

			updateSubscriptions(newCell);

			emitEnterExit(oldCell, newCell);

			requestPvs(newCell, false);

			timer.delay(new PvsCheckAction(this, newCell), 2000);

			Stats.pvsSize.sample(newCell.getPvs().size());

			if (oldCell != null) {

				Stats.cellStay.sampleTime(PhanerosAgent.this);
			}

			Stats.cellStay.markTime(PhanerosAgent.this);

			Stats.cellChange.sample();
		}

		Stats.aoiNeighbors.sample(connections.size());
		Stats.simultaneousConnections.sample(connections.size());
	}

	public void requestPvs(VisibilityCell visibilityCell, boolean fromServer) {

		if (visibilityCell == currentCell) {

			int missingCount = 0;

			for (VisibilityCell cell : visibilityCell.getPvs()) {

				if (cache.getTile(cell.getRegion()) == null) {

					missingCount++;

					if (fromServer) {

						node.send(new Message(node, mapServer, new TileRequest(node, cell.getRegion())));

					} else {

						scribeNode.publish(new ChordId(cell.getRegion().toString()),
								new TileQuery(node, cell.getRegion()));

						Stats.fetchDelay.markTime(PhanerosAgent.this);
					}
				}
			}

			if (fromServer) {

				Stats.missingTilesAfterSecond.sample(missingCount);

			} else {

				Stats.deltaPvs.sample(missingCount);
			}
		}
	}

	private void emitEnterExit(VisibilityCell oldCell, VisibilityCell newCell) {

		if (oldCell != null) {

			scribeNode.publish(new ChordId(oldCell.getRegion().toString()), new CellExit(oldCell, this));
		}

		scribeNode.publish(new ChordId(newCell.getRegion().toString()), new CellEnter(newCell, this, x, y));
	}

	private void updateSubscriptions(VisibilityCell newCell) {

		Set<VisibilityCell> newPvs = newCell.getPvs();

		synchronized (subscriptions) {

			Iterator<VisibilityCell> itr = subscriptions.iterator();

			while (itr.hasNext()) {

				VisibilityCell cell = itr.next();

				if (newPvs.contains(cell) == false) {

					scribeNode.unsubscribe(new ChordId(cell.toString()));

					for (PhanerosAgent agent : connections.removeAll(cell)) {

						knownAgents.remove(agent);
					}

					itr.remove();
				}
			}
		}

		for (VisibilityCell cell : newPvs) {

			if (subscriptions.contains(cell) == false) {

				scribeNode.subscribe(new ChordId(cell.getRegion().toString()));
				subscriptions.add(cell);

				Stats.subscriptions.sample();
			}
		}
	}

	private void emitUpdates() {

		for (PhanerosAgent agent : connections.values().toArray(new PhanerosAgent[] {})) {

			node.send(new Message(node, agent.node, new Update(this, x, y)));

			Stats.updatesSend.sample();
		}
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
