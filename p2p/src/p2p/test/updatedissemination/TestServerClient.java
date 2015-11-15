package p2p.test.updatedissemination;

import java.util.ArrayList;

import actionsim.log.Logger;
import p2p.application.contentserver.ContentServer;
import p2p.application.loop.Loop;
import p2p.application.peer.Peer;
import p2p.application.updateserver.UpdateServer;
import p2p.network.EndPointFactory;
import p2p.network.EndPointFactory.EndPointType;
import p2p.stats.Stats;

public class TestServerClient 
{
	public static void main(String[] args) 
	{
		Logger.init(Logger.INFO);
		Stats.start();
		
		EndPointFactory.setEndPointType(EndPointType.Trivial);
		
		ContentServer contentServer = new ContentServer("server", null);
		UpdateServer updateServer = new UpdateServer("updateServer");
		
		ArrayList<Peer> peers = new ArrayList<Peer>();
		
		peers.add(new Peer("client", true, false, false, false));
		
		for(int i = 0; i < 400; i++)
		{
			peers.add(new Peer("client" + i, false, false, false, false));
		}
		
		contentServer.start();
		updateServer.start();
		
		for(Peer peer : peers) {
			
			peer.start();
		}
		
		Loop.start();
	}
}
