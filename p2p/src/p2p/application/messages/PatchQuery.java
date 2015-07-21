package p2p.application.messages;

import p2p.data.IntPair;
import p2p.network.IMessage;

@SuppressWarnings("serial")
public class PatchQuery implements IMessage
{
	private String mRequesterId;
	
	private IntPair mPatchPos;
	
	private boolean mIsForwarded = false;
	
	public PatchQuery(String requesterId, IntPair patchPos, boolean isForwarded) 
	{
		mRequesterId = requesterId;
		mPatchPos = patchPos;
		mIsForwarded = isForwarded;
	}
	
	public IntPair getPatchPos()
	{
		return mPatchPos;
	}
	
	public String getRequesterId()
	{
		return mRequesterId;
	}
	
	public boolean isForwarded()
	{
		return mIsForwarded;
	}
}
