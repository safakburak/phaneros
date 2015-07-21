package p2p.test.updatedissemination;

import p2p.application.contentserver.ContentServer;
import p2p.application.loop.Loop;
import p2p.application.peer.Peer;
import p2p.application.updateserver.UpdateServer;
import p2p.log.Logger;
import p2p.log.Stats;
import p2p.network.EndPointFactory;
import p2p.network.EndPointFactory.EndPointType;

public class TestServerClient 
{
	public static void main(String[] args) 
	{
		Logger.start("Update Server Client");
		Stats.start();
		
		EndPointFactory.setEndPointType(EndPointType.Trivial);
		
		new ContentServer("server", null);
		new UpdateServer("updateServer");
		
		new Peer("client", true, false, false, false);
		
		for(int i = 0; i < 99; i++)
		{
			new Peer("client" + i, false, false, false, false);
		}
		
		Loop.start();
	}
}
