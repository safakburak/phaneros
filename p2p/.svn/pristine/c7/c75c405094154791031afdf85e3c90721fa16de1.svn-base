package p2p.application.messages;

import p2p.network.IMessage;

@SuppressWarnings("serial")
public class Subscription implements IMessage 
{
	private String mTag;
	
	private boolean mIsSubscribe;
	
	public Subscription(String tag, boolean isSubscribe) 
	{
		mTag = tag;
		
		mIsSubscribe = isSubscribe;
	}
	
	public String getTopic()
	{
		return mTag;
	}
	
	public boolean isSubscribe()
	{
		return mIsSubscribe;
	}
}
