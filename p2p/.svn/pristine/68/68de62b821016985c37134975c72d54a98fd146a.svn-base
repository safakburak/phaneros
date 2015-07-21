package p2p.application.peer.components;

import p2p.application.messages.PositionUpdate;
import p2p.application.messages.PatchUpdate;
import p2p.application.peer.IPeerComponent;
import p2p.application.peer.Peer;
import p2p.data.IntPair;
import p2p.log.Stats;
import p2p.network.IEndPoint;

public class UpdateManager implements IPeerComponent
{
	private IEndPoint mEndPoint;
	
	private Peer mOwner;
	
	private IntPair mPreviousPatch;
	
	private IntPair mPreviousPos;
	
	private boolean mIsP2P;
	
	private boolean mIsPvsUsed;
	
	public UpdateManager(Peer owner, IEndPoint messagingEndPoint, boolean isP2P, boolean isPvsUsed) 
	{
		mOwner = owner;
		
		mEndPoint = messagingEndPoint;
		
		mIsP2P = isP2P;
		
		mIsPvsUsed = isPvsUsed;
	}
	
	@Override
	public void update(long deltaTime) 
	{
		if(mPreviousPos == null || mOwner.currentPos.equals(mPreviousPos) == false)
		{
			PositionUpdate advertisement = new PositionUpdate(mOwner.getId(), mOwner.currentPos);

			if(mIsP2P)
			{
				if(mIsPvsUsed)
				{
					mEndPoint.castMessage("patch_" + mOwner.currentPatch.getX() + "_" + mOwner.currentPatch.getY(), advertisement);
				}
				else
				{
					mEndPoint.castMessage("p2pBroadcast", advertisement);
				}
			}
			else
			{
				mEndPoint.castMessage("updateServer", advertisement);
			}
			
			Stats.positionUpdateSent(mOwner.getId());
			
			mPreviousPos = mOwner.currentPos;
		}
		
		if(mPreviousPatch == null || mOwner.currentPatch.equals(mPreviousPatch) == false)
		{
			PatchUpdate patchPosUpdate = new PatchUpdate(mOwner.getId(), mPreviousPatch, mOwner.currentPatch);
		
			if(mIsP2P)
			{
				if(mIsPvsUsed)
				{
					if(mPreviousPatch != null)
					{
						mEndPoint.castMessage("patch_" + mPreviousPatch.getX() + "_" + mPreviousPatch.getY(), patchPosUpdate);
					}
					
					mEndPoint.castMessage("patch_" + mOwner.currentPatch.getX() + "_" + mOwner.currentPatch.getY(), patchPosUpdate);
				}
				else
				{
					mEndPoint.castMessage("p2pBroadcast", patchPosUpdate);
				}
			}
			else
			{
				mEndPoint.castMessage("updateServer", patchPosUpdate);
			}
			
			mPreviousPatch = mOwner.currentPatch;
		}
	}
}
