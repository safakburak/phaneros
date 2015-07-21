package p2p.test.contentdistribution;

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
		Logger.start("Content Distribution Dedicated Peer");
		Stats.start();
		
		EndPointFactory.setEndPointType(EndPointType.PSim);
		
		PSimNetwork.initialize();
		
		new ContentServer("server1", new Region(0, 0, 50, 50));
		new ContentServer("server2", new Region(50, 0, 50, 50));
		new ContentServer("server3", new Region(0, 50, 50, 50));
		new ContentServer("server4", new Region(50, 50, 50, 50));
		
		new Peer("client", true, false, true, true);
		
		for(int i = 0; i < 99; i++)
		{
			new Peer("client" + i, false, false, true, true);
		}
		
		Loop.start();
	}
}
