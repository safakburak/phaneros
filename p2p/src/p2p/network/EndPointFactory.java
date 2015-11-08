package p2p.network;

import p2p.network.actionsim.ActionSimNetwork;
import p2p.network.inhouse.socket.MessageEndPoint;
import p2p.network.inhouse.trivial.TrivialEndPoint;
import p2p.network.planetsim.PSimNetwork;

public class EndPointFactory 
{
	private static EndPointType sEndPointType;
	
	public enum EndPointType
	{
		PSim,
		Trivial,
		SocketServer,
		ActionSim
	}
	
	public static void setEndPointType(EndPointType endPointType)
	{
		sEndPointType = endPointType;
	}
	
	public static IEndPoint createEndPoint(String id)
	{
		switch (sEndPointType) 
		{
			case PSim:
				return PSimNetwork.getEndPoint(id);
				
			case Trivial:
				return new TrivialEndPoint(id);
				
			case SocketServer:
				return new MessageEndPoint(id);
	
			case ActionSim:
				return ActionSimNetwork.getEndPoint(id);	
				
			default:
				return null;
		}
		
	}
	
}
