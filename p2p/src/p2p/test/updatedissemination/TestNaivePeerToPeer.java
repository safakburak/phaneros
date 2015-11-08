package p2p.test.updatedissemination;

import java.util.ArrayList;

import p2p.application.contentserver.ContentServer;
import p2p.application.loop.Loop;
import p2p.application.peer.Peer;
import p2p.log.Logger;
import p2p.log.Stats;
import p2p.network.EndPointFactory;
import p2p.network.EndPointFactory.EndPointType;
import p2p.network.planetsim.PSimNetwork;

public class TestNaivePeerToPeer 
{
	public static void main(String[] args) 
	{
		Logger.init();
		Stats.start();
		
		EndPointFactory.setEndPointType(EndPointType.PSim);
		
		PSimNetwork.initialize();
		
		ContentServer server = new ContentServer("server", null);
		
		ArrayList<Peer> peers = new ArrayList<Peer>();
		
		peers.add(new Peer("client", true, true, true, false));
		
		for(int i = 0; i < 99; i++)
		{
			peers.add(new Peer("client" + i, false, true, true, false));
		}
		
		
		server.start();
		
		for(Peer peer : peers) {
			
			peer.start();
		}
		
		Loop.start();
	}
}
