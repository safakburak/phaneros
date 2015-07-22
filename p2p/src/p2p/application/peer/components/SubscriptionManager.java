package p2p.application.peer.components;

import java.util.ArrayList;

import p2p.application.peer.IPeerComponent;
import p2p.application.peer.Peer;
import p2p.data.IntPair;
import p2p.network.IEndPoint;
import p2p.visibility.VisibilityMap;

public class SubscriptionManager implements IPeerComponent
{
	private IEndPoint mMessagingEndPoint;
	
	private ArrayList<IntPair> mSubscriptions = new ArrayList<IntPair>();
	
	private VisibilityMap mVisibility;

	private Peer mOwner;

	private IntPair mPreviousPatch;

	public SubscriptionManager(Peer owner, IEndPoint messagingEndPoint, VisibilityMap visibility) 
	{
		mOwner = owner;
		
		mMessagingEndPoint = messagingEndPoint;
		
		mVisibility = visibility;
	}
	
	@Override
	public void update(long deltaTime) 
	{
		if(mPreviousPatch == null || mOwner.currentPatch.equals(mPreviousPatch) == false)
		{
			//for content queries
			if(mPreviousPatch != null)
			{
				mMessagingEndPoint.unsubscribe("content_" + mPreviousPatch.getX() + "_" + mPreviousPatch.getY());
			}
			
			mMessagingEndPoint.subscribe("content_" + mOwner.currentPatch.getX() + "_" + mOwner.currentPatch.getY());
			
			//for updates
			ArrayList<IntPair> newSubs = mVisibility.get(mOwner.currentPatch);
			
			for(IntPair ch : mSubscriptions)
			{
				if(!newSubs.contains(ch))
				{
					mMessagingEndPoint.unsubscribe("patch_" + ch.getX() + "_" + ch.getY());
				}
			}
			
			for(IntPair ch : newSubs)
			{
				if(!mSubscriptions.contains(ch))
				{
					mMessagingEndPoint.subscribe("patch_" + ch.getX() + "_" + ch.getY());
				}
			}
			
			mSubscriptions = (ArrayList<IntPair>) newSubs.clone();
			
			mPreviousPatch = mOwner.currentPatch;
		}
	}
}