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
import p2p.common.RandomWalker;
import p2p.common.messages.PatchRequest;
import p2p.map.Map;
import p2p.phaneros.messages.CellEnter;
import p2p.phaneros.messages.CellExit;
import p2p.phaneros.messages.Update;
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
			int worldHeight, Map worldMap) {

		super(node, visibility, cacheSize, worldMap);

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
		}, 500);

		node.addNodeListener(new AbstractNodeListener() {

			@Override
			public void onMessage(Message message) {

				Object payload = message.getPayload();

				if (payload instanceof Map) {

					cache.addPatch((Map) payload);

				} else if (payload instanceof Update) {

					Update update = (Update) payload;

					if (update.getAgent() != PhanerosAgent.this) {

						knownAgents.put(update.getAgent(), new Point(update.getX(), update.getY()));
					}

				}
			}
		});

		scribeNode.addScribeListener(new ScribeListener() {

			@Override
			public void onScribeMessage(ChordId topic, Object message) {

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
				}
			}
		});

		onPositionChange();
	}

	public ChordNode getChordNode() {
		return chordNode;
	}

	@Override
	public void onCacheMissAt(int x, int y) {

		mapServer.send(new Message(node, mapServer, new PatchRequest(x, y)));
	}

	@Override
	public void onPositionChange() {

		for (PhanerosAgent agent : connections.values().toArray(new PhanerosAgent[] {})) {

			node.send(new Message(node, agent.node, new Update(this, x, y)));
		}

		VisibilityCell newCell = visibility.getCellForPos(x, y);

		if (currentCell == null || currentCell != newCell) {

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
				}
			}

			if (currentCell != null) {

				scribeNode.publish(new ChordId(currentCell.getRegion().toString()), new CellExit(currentCell, this));
			}

			currentCell = newCell;

			scribeNode.publish(new ChordId(currentCell.getRegion().toString()), new CellEnter(currentCell, this, x, y));
		}
	}
}
