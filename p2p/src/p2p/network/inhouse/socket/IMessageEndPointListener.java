package p2p.network.inhouse.socket;

import p2p.network.IEndPoint;



public interface IMessageEndPointListener
{
	public void onReceive(IEndPoint endPoint, MessageEnvelope envelope);
}
