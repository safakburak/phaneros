package p2p.test.contentdistribution;

import java.util.ArrayList;

import p2p.application.contentserver.ContentServer;
import p2p.application.loop.Loop;
import p2p.application.peer.Peer;
import p2p.log.Logger;
import p2p.log.Stats;
import p2p.network.EndPointFactory;
import p2p.network.EndPointFactory.EndPointType;
import p2p.network.actionsim.ActionSimNetwork;

public class TestMethod 
{
	public static void main(String[] args) 
	{
		Logger.init();
		Stats.start();
		
		EndPointFactory.setEndPointType(EndPointType.ActionSim);
		
		ActionSimNetwork.initialize();
		
		ContentServer contentServer = new ContentServer("server", null);
		
		ArrayList<Peer> peers = new ArrayList<Peer>();
		
		peers.add(new Peer("client_0", true, true, true, true));
		
		for(int i = 1; i < 400; i++)
		{
			peers.add(new Peer("client_" + i, false, true, true, true));
		}
		
		ActionSimNetwork.simulation.iterate(300);
		
		contentServer.start();
		
		for(Peer peer : peers) {
			
			peer.start();
		}
		
		Loop.start();
	}
}
