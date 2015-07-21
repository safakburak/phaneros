package p2p.application.messages;

import p2p.data.IntPair;
import p2p.network.IMessage;

@SuppressWarnings("serial")
public class PatchUpdate implements IMessage 
{
	private IntPair mOldPatchPos;
	private IntPair mNewPatchPos;
	private String mSender;
	
	public PatchUpdate(String sender, IntPair oldPatchPos, IntPair newPatchPos) 
	{
		mSender = sender;
		mOldPatchPos = oldPatchPos;
		mNewPatchPos = newPatchPos;
	}

	public IntPair getOldPatchPos() 
	{
		return mOldPatchPos;
	}

	public IntPair getNewPatchPos() 
	{
		return mNewPatchPos;
	}
	
	public String getSender()
	{
		return mSender;
	}
}
