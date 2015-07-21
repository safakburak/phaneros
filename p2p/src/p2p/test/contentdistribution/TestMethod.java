package p2p.test.contentdistribution;

import p2p.application.contentserver.ContentServer;
import p2p.application.loop.Loop;
import p2p.application.peer.Peer;
import p2p.log.Logger;
import p2p.log.Stats;
import p2p.network.EndPointFactory;
import p2p.network.EndPointFactory.EndPointType;
import p2p.network.planetsim.PSimNetwork;

public class TestMethod 
{
	public static void main(String[] args) 
	{
		Logger.start("Content Distribution PVS P2P");
		Stats.start();
		
		EndPointFactory.setEndPointType(EndPointType.PSim);
		
		PSimNetwork.initialize();
		
		new ContentServer("server", null);
		
		new Peer("client", true, true, true, true);
		
		for(int i = 0; i < 99; i++)
		{
			new Peer("client" + i, false, true, true, true);
		}
		
		Loop.start();
	}
}
