package p2p.network.inhouse.socket;

import java.io.Serializable;

import p2p.network.IMessage;


public class MessageEnvelope implements Serializable
{
	private String mSourceId;
	
	private String mDestinationId;
	
	private String mTopic;
	
	private IMessage mMessage;
	

	public MessageEnvelope(String sourceId, IMessage message) 
	{
		mSourceId = sourceId;
		mMessage = message;
	}
	
	public String getSourceId()
	{
		return mSourceId;
	}
	
	public String getDestinationId()
	{
		return mDestinationId;
	}
	
	public String getTopic()
	{
		return mTopic;
	}
	
	public IMessage getMessage()
	{
		return mMessage;
	}

	public void setDestination(String destination)
	{
		mDestinationId = destination;
	}
	
	public void setTopic(String topic)
	{
		mTopic = topic;
	}
}
