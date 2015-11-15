package p2p.test.contentdistribution;

import java.util.ArrayList;

import actionsim.log.Logger;
import p2p.application.contentserver.ContentServer;
import p2p.application.loop.Loop;
import p2p.application.peer.Peer;
import p2p.data.Region;
import p2p.network.EndPointFactory;
import p2p.network.EndPointFactory.EndPointType;
import p2p.network.actionsim.ActionSimNetwork;
import p2p.stats.Stats;

public class TestDedicatedPeer 
{
	public static void main(String[] args) 
	{
		
		Logger.init(Logger.INFO);
		Stats.start();
		
		EndPointFactory.setEndPointType(EndPointType.ActionSim);
		
		ActionSimNetwork.initialize();
		
		ContentServer server1 = new ContentServer("server1", new Region(0, 0, 50, 50));
		ContentServer server2 = new ContentServer("server2", new Region(50, 0, 50, 50));
		ContentServer server3 = new ContentServer("server3", new Region(0, 50, 50, 50));
		ContentServer server4 = new ContentServer("server4", new Region(50, 50, 50, 50));
		
		ArrayList<Peer> peers = new ArrayList<Peer>();
		
		peers.add(new Peer("client_0", true, false, true, true));
		
		for(int i = 1; i < 400; i++)
		{
			peers.add(new Peer("client_" + i, false, false, true, true));
		}
		
		ActionSimNetwork.simulation.iterate(300);
		
		server1.start();
		server2.start();
		server3.start();
		server4.start();
		
		for(Peer peer : peers) {
			
			peer.start();
		}
		
		Loop.start();
	}
}
