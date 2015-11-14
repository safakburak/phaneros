package p2p.test.updatedissemination;

import java.util.ArrayList;

import p2p.application.contentserver.ContentServer;
import p2p.application.loop.Loop;
import p2p.application.peer.Peer;
import p2p.log.Logger;
import p2p.log.Stats;
import p2p.network.EndPointFactory;
import p2p.network.EndPointFactory.EndPointType;
import p2p.network.actionsim.ActionSimNetwork;

public class TestNaivePeerToPeer 
{
	public static void main(String[] args) 
	{
		Logger.init(Logger.INFO);
		Stats.start();
		
		EndPointFactory.setEndPointType(EndPointType.ActionSim);
		
		ActionSimNetwork.initialize();
		
		ContentServer server = new ContentServer("server", null);
		
		ArrayList<Peer> peers = new ArrayList<Peer>();
		
		peers.add(new Peer("client", true, true, true, false));
		
		for(int i = 0; i < 400; i++)
		{
			peers.add(new Peer("client" + i, false, true, true, false));
		}
		
		ActionSimNetwork.simulation.iterate(300);
		
		server.start();
		
		for(Peer peer : peers) {
			
			peer.start();
		}
		
		Loop.start();
	}
}
