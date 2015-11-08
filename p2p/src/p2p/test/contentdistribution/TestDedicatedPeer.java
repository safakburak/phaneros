package p2p.test.contentdistribution;

import java.util.ArrayList;

import p2p.application.contentserver.ContentServer;
import p2p.application.loop.Loop;
import p2p.application.peer.Peer;
import p2p.data.Region;
import p2p.log.Logger;
import p2p.log.Stats;
import p2p.network.EndPointFactory;
import p2p.network.EndPointFactory.EndPointType;
import p2p.network.planetsim.PSimNetwork;

public class TestDedicatedPeer 
{
	public static void main(String[] args) 
	{
		
		Logger.init();
		Stats.start();
		
		EndPointFactory.setEndPointType(EndPointType.PSim);
		
		PSimNetwork.initialize();
		
		ContentServer server1 = new ContentServer("server1", new Region(0, 0, 50, 50));
		ContentServer server2 = new ContentServer("server2", new Region(50, 0, 50, 50));
		ContentServer server3 = new ContentServer("server3", new Region(0, 50, 50, 50));
		ContentServer server4 = new ContentServer("server4", new Region(50, 50, 50, 50));
		
		ArrayList<Peer> peers = new ArrayList<Peer>();
		
		peers.add(new Peer("client_0", true, false, true, true));
		
		for(int i = 1; i < 100; i++)
		{
			peers.add(new Peer("client_" + i, false, false, true, true));
		}
		
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
