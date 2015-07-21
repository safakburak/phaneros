package p2p.application.messages;

import p2p.data.IntPair;
import p2p.network.IMessage;

@SuppressWarnings("serial")
public class PositionUpdate implements IMessage 
{
	private IntPair mPosition;

	private String mSender;
	
	public PositionUpdate(String sender, IntPair position) 
	{
		mSender = sender;
		mPosition = position;
	}

	public IntPair getPosition() 
	{
		return mPosition;
	}
	
	public String getSender()
	{
		return mSender;
	}
}
