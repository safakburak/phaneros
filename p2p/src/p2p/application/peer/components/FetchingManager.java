package p2p.application.peer.components;

import java.util.ArrayList;

import p2p.application.messages.PatchAvailable;
import p2p.application.messages.PatchEnvelope;
import p2p.application.messages.PatchQuery;
import p2p.application.messages.PatchRequest;
import p2p.application.peer.IPeerComponent;
import p2p.application.peer.Peer;
import p2p.cache.Cache;
import p2p.data.IntPair;
import p2p.network.IEndPoint;
import p2p.network.IMessageListener;
import p2p.stats.Stats;
import p2p.visibility.VisibilityMap;

public class FetchingManager implements IPeerComponent
{
	private Cache mPatchCache;
	
	private IEndPoint mEndPoint;
	
	private VisibilityMap mVisibility;
	
	private ArrayList<IntPair> mInProcess = new ArrayList<IntPair>();
	
	private Peer mOwner;
	
	private long mSinceLastFetch;
	
	private boolean mIsServingPeer;
	
	public FetchingManager(Peer owner, IEndPoint endPoint, Cache patchCache, VisibilityMap visibility, boolean isServingPeer) 
	{
		mOwner = owner;
		
		mEndPoint = endPoint;
		
		mPatchCache = patchCache;
		
		mVisibility = visibility;
		
		mIsServingPeer = isServingPeer;
		
		mEndPoint.addMessageListener(PatchEnvelope.class, new IMessageListener<PatchEnvelope>() {
			@Override
			public void onMessage(PatchEnvelope message, String source) 
			{
				if(mInProcess.contains(message.getPayLoad().getPos()))
				{
					mPatchCache.put(message.getPayLoad());
					
					mInProcess.remove(message.getPayLoad().getPos());
				}
			}
		});
		
		mEndPoint.addMessageListener(PatchAvailable.class, new IMessageListener<PatchAvailable>() {
			@Override
			public void onMessage(PatchAvailable message, String source) 
			{
				if(!mInProcess.contains(message.getPatchPos()))
				{
					Stats.requestSent(mOwner.getId(), source, message.getPatchPos());
					
					mInProcess.add(message.getPatchPos());
					
					PatchRequest patchRequest = new PatchRequest(message.getPatchPos(), message.isForwarded());
					
					mEndPoint.sendMessage(source, patchRequest);
				}
			}
		});
	}
	
	@Override
	public void update(long deltaTime) 
	{
		mSinceLastFetch += deltaTime;
		
		if(mSinceLastFetch >= 500)
		{
			requestPvs();
			
			prefetch();
			
			mSinceLastFetch -= 500;
		}
	}
	
	private void requestPvs()
	{
		IntPair patchPos = mOwner.currentPatch;
		
		if(mPatchCache.getPatch(patchPos) == null)
		{
			queryPatch(patchPos);
		}
		
		if(mOwner.targetPatch != null && mPatchCache.getPatch(mOwner.targetPatch) == null)
		{
			queryPatch(mOwner.targetPatch);
		}
		
		for(IntPair pvsPatch : mVisibility.get(patchPos))
		{
			if(mPatchCache.getPatch(pvsPatch) == null)
			{
				queryPatch(pvsPatch);
			}
		}
	}
	
	private void prefetch()
	{
		
	}
	
	private void queryPatch(IntPair patchPos)
	{
		PatchQuery patchQuery = new PatchQuery(mOwner.getId(), patchPos, false);
		
		if(mIsServingPeer)
		{
			for (IntPair pvsPatch : mVisibility.get(patchPos)) 
			{
				String topic = "content_" + pvsPatch.getX() + "_" + pvsPatch.getY();
				
				mEndPoint.castMessage(topic, patchQuery);
				
				Stats.querySent(mOwner.getId(), topic, patchPos);
			}
		}
		
		mEndPoint.castMessage("seed", patchQuery);
		Stats.querySent(mOwner.getId(), "seed", patchPos);
	}
}
