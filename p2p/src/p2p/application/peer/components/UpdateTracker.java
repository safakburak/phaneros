package p2p.application.peer.components;

import p2p.application.messages.PositionUpdate;
import p2p.application.messages.PatchUpdate;
import p2p.application.peer.Peer;
import p2p.data.IntPair;
import p2p.log.Stats;
import p2p.network.IEndPoint;
import p2p.network.IMessageListener;
import p2p.visibility.VisibilityMap;

public class UpdateTracker
{
	private Peer mOwner;
	
	private IEndPoint mEndPoint;
	
	private VisibilityMap mVisibility;

	public UpdateTracker(Peer owner, IEndPoint endPoint, VisibilityMap visibility, boolean isP2P, boolean isPvsUsed) 
	{
		mOwner = owner;
		
		mEndPoint = endPoint;
		
		mVisibility = visibility;
		
		if(isP2P)
		{
			if(isPvsUsed)
			{
				
			}
			else
			{
				mEndPoint.subscribe("p2pBroadcast");
			}
		}
		else
		{
			mEndPoint.subscribe("updateServerBroadcast");
		}
		
		mEndPoint.addMessageListener(PositionUpdate.class, new IMessageListener<PositionUpdate>() {
			@Override
			public void onMessage(PositionUpdate message, String source) 
			{
				Stats.positionUpdateReceived(mOwner.getId());
				
				if(message.getSender().equals(mOwner.mId) == false)
				{
					if(mOwner.peerPoses.containsKey(message.getSender()))
					{
						mOwner.peerPoses.put(message.getSender(), message.getPosition());
					}
				}
			}
		});
		
		mEndPoint.addMessageListener(PatchUpdate.class, new IMessageListener<PatchUpdate>() {
			public void onMessage(PatchUpdate message, String source) 
			{
				if(message.getSender().equals(mOwner.mId) == false)
				{
					if(mVisibility.get(mOwner.currentPatch).contains(message.getNewPatchPos()) == false)
					{
						mOwner.peerPoses.remove(message.getSender());
					}
					else
					{
						mOwner.peerPoses.put(message.getSender(), new IntPair(-1, -1));
					}
				}
			};
		});
	}
}
