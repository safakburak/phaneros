package p2p.network;

public interface IForwardListener 
{
	public boolean forwardAllowed(String source, IMessage message);
}
