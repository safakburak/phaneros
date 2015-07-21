package p2p.application.messages;

import p2p.data.IntPair;
import p2p.network.IMessage;

@SuppressWarnings("serial")
public class PatchRequest implements IMessage
{
	private IntPair mPatchPos = null;
	
	private boolean mIsForwarded = false;
	
	public PatchRequest(IntPair patchPos, boolean isForwarded) 
	{
		mPatchPos = patchPos;
		mIsForwarded = isForwarded;
	}
	
	public IntPair getPatchPos()
	{
		return mPatchPos;
	}
	
	public boolean isForwarded()
	{
		return mIsForwarded;
	}
}
